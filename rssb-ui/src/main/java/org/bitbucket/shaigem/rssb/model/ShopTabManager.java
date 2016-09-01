package org.bitbucket.shaigem.rssb.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import org.bitbucket.shaigem.rssb.fx.control.ShopDisplayRadioButton;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.bitbucket.shaigem.rssb.ui.BuilderWindowPresenter;
import org.bitbucket.shaigem.rssb.ui.shop.ShopPresenter;
import org.bitbucket.shaigem.rssb.ui.shop.ShopView;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 * Created on 2016-03-13.
 */
public final class ShopTabManager {

    private BuilderWindowPresenter builderWindowPresenter;

    private ObjectProperty<ShopPresenter> currentShopProperty;

    private List<ShopPresenter> openShops;

    @PostConstruct
    public void init() {
        openShops = FXCollections.observableArrayList();
        currentShopProperty = new SimpleObjectProperty<>();
    }

    public void createNewTab(Shop shop) {
        Optional<ShopPresenter> openTab = isOpen(shop);
        if (openTab.isPresent()) {
            Tab tab = openTab.get().getTab();
            getTabPane().getSelectionModel().select(tab);
            return;
        }
        ShopView shopView = new ShopView();
        ShopPresenter shopPresenter = (ShopPresenter) shopView.getPresenter();
        Tab tab = new Tab(shop.getName());
        StackPane stackPane = new StackPane();
        shopPresenter.setMainWindowPresenter(builderWindowPresenter);
        shopPresenter.setTab(tab);
        shopPresenter.setShop(shop);
        shopPresenter.setDisplayMode(builderWindowPresenter.byDefaultExpandItemDisplay() ?
                ShopDisplayRadioButton.DisplayMode.EXPANDED : ShopDisplayRadioButton.DisplayMode.ICON);
        stackPane.getChildren().add(shopView.getView());
        tab.setContent(stackPane);
        openShops.add(shopPresenter);
        getTabPane().getTabs().add(tab);
        getTabPane().getSelectionModel().selectLast();
        registerTabCloseEvent(tab, shopPresenter);
    }

    public void closeAll() {
        openShops.clear();
        getTabPane().getTabs().clear();
    }


    public void handleOnTabClose(ShopPresenter shopPresenter) {
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
        } else {
            builderWindowPresenter.getExplorerPresenter().checkForDuplicateKeys(shopPresenter.getShop());
            builderWindowPresenter.getExplorerPresenter().refreshListView();
        }

        System.out.println("[TabManager] Closed tab: " + shopPresenter.getShop().getName() + " Response: "
                + openShops.remove(shopPresenter) + " SIZE AFTER: " + openShops.size()
        );
    }

    public Optional<ShopPresenter> getPresenterForTab(Tab tab) {
        return openShops.stream().filter((presenter -> presenter.getTab() == tab)).findFirst();
    }

    private TabPane getTabPane() {
        return builderWindowPresenter.getShopTabPane();
    }


    public void setBuilderWindowPresenter(BuilderWindowPresenter presenter) {
        this.builderWindowPresenter = presenter;
    }

    public ShopPresenter getCurrentViewingShop() {
        return currentShopProperty.get();
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

    private void registerTabCloseEvent(Tab tab, ShopPresenter presenter) {
        tab.setOnCloseRequest((e) -> {
            //    Tab targetTab = (Tab) e.getTarget();
            handleOnTabClose(presenter);
            // openShops.stream().filter((shopPresenter1 -> shopPresenter1.getTab() == targetTab)).findFirst().
            //      ifPresent((this::handleOnTabClose));


        });

    }


    private Optional<ShopPresenter> isOpen(Shop shop) {
        return openShops.stream().filter((shopPresenter -> shopPresenter.getShop() == shop)).findFirst();
    }


}
