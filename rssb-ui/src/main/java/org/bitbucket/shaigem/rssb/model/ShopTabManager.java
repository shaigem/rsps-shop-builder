package org.bitbucket.shaigem.rssb.model;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import org.bitbucket.shaigem.rssb.event.ActiveFormatPluginChangedEvent;
import org.bitbucket.shaigem.rssb.event.CreateNewShopTabRequest;
import org.bitbucket.shaigem.rssb.event.RemoveAllShopsEvent;
import org.bitbucket.shaigem.rssb.event.ShopCloseEvent;
import org.bitbucket.shaigem.rssb.fx.ShopTab;
import org.bitbucket.shaigem.rssb.fx.control.ShopDisplayRadioButton;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.bitbucket.shaigem.rssb.ui.builder.BuilderWindowPresenter;
import org.bitbucket.shaigem.rssb.ui.builder.shop.ShopPresenter;
import org.bitbucket.shaigem.rssb.ui.builder.shop.ShopView;
import org.bitbucket.shaigem.rssb.ui.builder.shop.toolbar.ShopToolBarPresenter;
import org.bitbucket.shaigem.rssb.ui.builder.shop.toolbar.ShopToolBarView;
import org.sejda.eventstudio.DefaultEventStudio;
import org.sejda.eventstudio.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 2016-03-13.
 */
public final class ShopTabManager {

    private static final Logger LOG = LoggerFactory.getLogger(ShopTabManager.class);

    private BuilderWindowPresenter builderWindowPresenter;

    private ObjectProperty<ShopPresenter> currentShopProperty;

    private ObservableList<ShopPresenter> openShops;

    @Inject
    DefaultEventStudio eventStudio;

    @PostConstruct
    public void init() {
        openShops = FXCollections.observableArrayList();
        currentShopProperty = new SimpleObjectProperty<>();
        eventStudio.addAnnotatedListeners(this);
    }


    @EventListener
    private void onCreateNewShopTabRequest(CreateNewShopTabRequest request) {
        createNewTab(request.getShop());
    }

    @EventListener
    private void onActiveFormatPluginChanged(ActiveFormatPluginChangedEvent event) {
        closeAll();
    }

    @EventListener
    private void onRemoveAllShops(RemoveAllShopsEvent event) {
        closeAll();
    }


    private void createNewTab(Shop shop) {
        final Optional<ShopPresenter> openTab = isOpen(shop);
        if (openTab.isPresent()) {
            ShopTab tab = openTab.get().getTab();
            getTabPane().getSelectionModel().select(tab);
            return;
        }
        final ShopView shopView = new ShopView();
        final ShopPresenter shopPresenter = (ShopPresenter) shopView.getPresenter();
        final ShopTab tab = new ShopTab(shop.toString());
        tab.setContextMenu(createTabContextMenu(tab));
        final BorderPane borderPane = new BorderPane();
        final StackPane stackPane = new StackPane();
        initializeShop(shopPresenter, tab, shop);
        stackPane.getChildren().addAll(shopView.getView());
        borderPane.setCenter(stackPane);
        final ShopToolBarView toolBarView = new ShopToolBarView();
        final ShopToolBarPresenter toolBarPresenter = (ShopToolBarPresenter) toolBarView.getPresenter();
        toolBarPresenter.setShopPresenter(shopPresenter);
        borderPane.setTop(toolBarView.getView());
        tab.setContent(borderPane);
        openShops.add(shopPresenter);
        getTabPane().getTabs().add(tab);
        getTabPane().getSelectionModel().selectLast();
        registerTabCloseEvent(tab, shopPresenter);
    }

    /**
     * Force close a open shop tab. This does not ask the user to save any changes!
     *
     * @param presenter the shop presenter to close
     */
    public void forceClose(ShopPresenter presenter) {
        Platform.runLater(() -> {
            final Tab tab = presenter.getTab();
            boolean removed = getTabPane().getTabs().remove(tab);
            if (removed) {
                onRemoval(presenter);
            }
        });
    }

    private ContextMenu createTabContextMenu(ShopTab tab) {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem closeItem = new MenuItem("Close");
        closeItem.setOnAction(event -> tab.requestClose());
        MenuItem closeOthersItem = new MenuItem("Close Others");
        closeOthersItem.setOnAction(event -> closeAllButThis(tab));
        closeOthersItem.disableProperty().bind(Bindings.size
                (getTabPane().getTabs()).isEqualTo(1));
        MenuItem closeAllItem = new MenuItem("Close All");
        closeAllItem.setOnAction(event -> closeAll(true));
        contextMenu.getItems().addAll(closeItem, closeOthersItem, closeAllItem);
        return contextMenu;
    }

    private void registerTabCloseEvent(Tab tab, ShopPresenter presenter) {
        tab.setOnCloseRequest((e) -> handleOnTabClose(presenter));
    }

    /**
     * Handles the closing of a tab when the user closes it manually.
     *
     * @param shopPresenter the shop presenter that is closing
     */
    private void handleOnTabClose(ShopPresenter shopPresenter) {
        if (shopPresenter.hasBeenModified()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("Save: " + shopPresenter.getShop());
            alert.setContentText("You have some unsaved changes. Would you like to save them before closing?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.YES) {
                    shopPresenter.save();
                }
            }
        }
        onRemoval(shopPresenter);
    }


    private void onRemoval(ShopPresenter presenter) {
        eventStudio.broadcast(new ShopCloseEvent(presenter));
        presenter.cleanup();
        openShops.remove(presenter);
    }

    private void closeAll(boolean checkShopForChanges) {
        if (checkShopForChanges) {
            getShopTabs().listIterator().forEachRemaining(ShopTab::requestClose);
            openShops.clear();
            return;
        }
        getTabPane().getTabs().clear();
        openShops.clear();
    }

    private void closeAllButThis(ShopTab tab) {
        getShopTabs().stream().filter(openTab -> openTab != tab).collect(Collectors.toList()).listIterator().
                forEachRemaining(ShopTab::requestClose);
    }

    private void closeAll() {
        closeAll(false);
    }


    public Optional<ShopPresenter> getPresenterForTab(Tab tab) {
        return openShops.stream().filter((presenter -> presenter.getTab() == tab)).findFirst();
    }

    private List<ShopTab> getShopTabs() {
        return getTabPane().getTabs().stream().map(tab -> (ShopTab) tab).collect(Collectors.toList());
    }

    private TabPane getTabPane() {
        return builderWindowPresenter.getShopTabPane();
    }

    public void setBuilderWindowPresenter(BuilderWindowPresenter presenter) {
        this.builderWindowPresenter = presenter;
    }

    public Optional<ShopPresenter> getCurrentViewingShop() {
        return Optional.ofNullable(currentShopProperty.get());
    }

    public ObjectProperty<ShopPresenter> currentShopProperty() {
        return currentShopProperty;
    }

    public void setCurrentShop(ShopPresenter currentShop) {
        this.currentShopProperty.set(currentShop);
    }

    public List<ShopPresenter> getOpenShops() {
        return openShops;
    }

    public Optional<ShopPresenter> isOpen(Shop shop) {
        return openShops.stream().filter((shopPresenter -> shopPresenter.getShop() == shop)).findFirst();
    }

    private void initializeShop(ShopPresenter shopPresenter, ShopTab tab, Shop shop) {
        shopPresenter.setMainWindowPresenter(builderWindowPresenter);
        shopPresenter.setTab(tab);
        shopPresenter.setShop(shop);
        shopPresenter.setDisplayMode(builderWindowPresenter.byDefaultExpandItemDisplay() ?
                ShopDisplayRadioButton.DisplayMode.EXPANDED : ShopDisplayRadioButton.DisplayMode.ICON);
    }
}
