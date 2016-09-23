package org.bitbucket.shaigem.rssb.ui.builder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.bitbucket.shaigem.rssb.event.ActiveFormatPluginChangedEvent;
import org.bitbucket.shaigem.rssb.event.RemoveAllShopsEvent;
import org.bitbucket.shaigem.rssb.model.ActiveFormatManager;
import org.bitbucket.shaigem.rssb.model.ShopRepository;
import org.bitbucket.shaigem.rssb.model.ShopTabManager;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;
import org.bitbucket.shaigem.rssb.plugin.ShopFormat;
import org.bitbucket.shaigem.rssb.ui.builder.explorer.ShopExplorerView;
import org.bitbucket.shaigem.rssb.ui.builder.itemlist.ItemListView;
import org.bitbucket.shaigem.rssb.ui.builder.properties.PropertiesView;
import org.bitbucket.shaigem.rssb.ui.builder.shop.ShopPresenter;
import org.bitbucket.shaigem.rssb.util.AlertDialogUtil;
import org.sejda.eventstudio.DefaultEventStudio;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * @author Abyss
 */
public class BuilderWindowPresenter implements Initializable {

    private BooleanProperty defaultExpandedItemDisplay;

    @Inject
    ShopTabManager tabManager;

    @Inject
    ShopRepository repository;

    @Inject
    DefaultEventStudio eventStudio;

    @Inject
    ActiveFormatManager activeFormatManager;

    @FXML
    VBox rootPane;
    @FXML
    SplitPane leftSplitPane;
    @FXML
    SplitPane rightSplitPane;

    @FXML
    StackPane shopStackPane;
    @FXML
    BorderPane shopPane;
    @FXML
    TabPane shopTabPane;

    @FXML
    Label noShopsOpenLabel;

    @FXML
    MenuItem switchFormatMenuItem;

    @FXML
    RadioMenuItem expandedItemDisplayRadioItem;

    public void initialize(URL location, ResourceBundle resources) {
        tabManager.setBuilderWindowPresenter(this);
        defaultExpandedItemDisplay = new SimpleBooleanProperty();
        defaultExpandedItemDisplay.bind(expandedItemDisplayRadioItem.selectedProperty());
        noShopsOpenLabel.visibleProperty().bind(tabManager.currentShopProperty().isNull());
        setupLeftSplitPaneItems();
        setupRightSplitPaneItems();
        listenForTabSelection();
        eventStudio.addAnnotatedListeners(this);
    }


    private void initializeAccelerators() {
        ObservableMap<KeyCombination, Runnable> accelerators =
                rootPane.getScene().getAccelerators();

        accelerators.put(
                new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), () ->
                        acceptIfViewingShopIsPresent(ShopPresenter::onCopyAction));

        accelerators.put(
                new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN), () ->
                        acceptIfViewingShopIsPresent(ShopPresenter::onPasteAction));

        accelerators.put(
                new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN), () ->
                        acceptIfViewingShopIsPresent(ShopPresenter::selectAllItems));

        accelerators.put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), () ->
                        acceptIfViewingShopIsPresent(ShopPresenter::save));

        accelerators.put(
                new KeyCodeCombination(KeyCode.DELETE), () ->
                        acceptIfViewingShopIsPresent(ShopPresenter::deleteSelectedItems));

        accelerators.put(
                new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN,
                        KeyCombination.SHIFT_DOWN), () ->
                        acceptIfViewingShopIsPresent(ShopPresenter::onChangeAmountAction));
    }


    private void acceptIfViewingShopIsPresent(Consumer<ShopPresenter> consumer) {
        final Optional<ShopPresenter> currentShop =
                tabManager.getCurrentViewingShop();

        if (currentShop.isPresent()) {
            consumer.accept(currentShop.get());
        }
    }


    public void onShow(BaseShopFormatPlugin requestedFormatPlugin) {
        boolean needsChanging = activeFormatManager.getFormatPlugin() != requestedFormatPlugin;
        if (needsChanging) {
            activeFormatManager.setFormatPlugin(requestedFormatPlugin);
            updateStageTitle();
            eventStudio.broadcast(new ActiveFormatPluginChangedEvent(
                    requestedFormatPlugin));
        }
        removeBlurFromWindow();
        if (rootPane.getScene().getAccelerators().isEmpty()) {
            initializeAccelerators();

        }
    }

    @FXML
    void onSwitchFormatAction() {
        Stage builderStage = getStage();
        Stage owner = (Stage) builderStage.getOwner();
        blurWindow();
        owner.show();
        if (owner.getOnCloseRequest() == null) {
            owner.setOnCloseRequest(event -> removeBlurFromWindow());
        }
    }

    @FXML
    void onOpenAction() {
        final ShopFormat<?> shopFormat = activeFormatManager.getFormat();
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(shopFormat.getDefaultFileName());
        chooser.getExtensionFilters().addAll(shopFormat.getExtensions());
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*"));
        chooser.setTitle("Open Shops");
        //TODO save last directory and use it here
        Optional<File> chosenFile = Optional.ofNullable(chooser.showOpenDialog(getWindow()));
        chosenFile.ifPresent((file -> {
            try {
                repository.populate((Collection<Shop>) shopFormat.load(file));
                // must send a RemoveAllShopsEvent!
                // Population of the repository needs to close all open shops and perform other actions!
                eventStudio.broadcast(new RemoveAllShopsEvent());
            } catch (Throwable throwable) {
                Alert exceptionAlert = AlertDialogUtil.createExceptionDialog(throwable);
                exceptionAlert.setHeaderText("Exception Caught");
                exceptionAlert.setContentText("Exception was caught while loading shops.");
                exceptionAlert.showAndWait();
            }

        }));
    }

    @FXML
    void onExportAction() {
        final ShopFormat<Shop> shopFormat = activeFormatManager.getFormat();
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(shopFormat.getDefaultFileName());
        chooser.getExtensionFilters().addAll(shopFormat.getExtensions());
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*"));
        //TODO save last directory and use it here
        chooser.setTitle("Export All Shops to File");
        tabManager.getOpenShops().forEach(presenter -> {
            if (presenter.hasBeenModified()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initStyle(StageStyle.UTILITY);
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.setTitle("Unsaved Changes");
                alert.setHeaderText("Save: " + presenter.getShop());
                alert.setContentText("You have some unsaved changes. Would you like to save them before exporting?");
                alert.getDialogPane().setPrefWidth(525);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.YES) {
                        presenter.save();
                    }
                }
            }
        });
        Optional<File> chosenFile = Optional.ofNullable(chooser.showSaveDialog(getWindow()));
        chosenFile.ifPresent(file -> {
            try {
                shopFormat.export(file, repository.getMasterShopDefinitions());
                Alert alert = AlertDialogUtil.createInformationDialog(Alert.AlertType.INFORMATION, "",
                        "All shops were exported to: " + file.getPath() + "");
                alert.setHeaderText("All Shops Were Exported");
                alert.setContentText("Exported to: " + file.getName() + " successfully!");
                alert.show();
            } catch (Throwable throwable) {
                Alert exceptionAlert = AlertDialogUtil.createExceptionDialog(throwable);
                exceptionAlert.setHeaderText("Exception Caught");
                exceptionAlert.setContentText("Exception was caught while exporting shops.");
                exceptionAlert.showAndWait();
            }
        });
    }

    private void setupLeftSplitPaneItems() {
        setShopExplorerArea();
    }


    private void setupRightSplitPaneItems() {
        setItemListArea();
        setPropertiesArea();

    }

    private void setShopExplorerArea() {
        ShopExplorerView shopExplorerView = new ShopExplorerView();
        leftSplitPane.getItems().add(shopExplorerView.getView());
    }

    private void setItemListArea() {
        ItemListView itemListView = new ItemListView();
        rightSplitPane.getItems().add(itemListView.getView());
    }

    private void setPropertiesArea() {
        PropertiesView propertiesView = new PropertiesView();
        rightSplitPane.getItems().add(propertiesView.getView());
    }


    private void listenForTabSelection() {
        shopTabPane.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
        {
            Optional<ShopPresenter> presenter = tabManager.getPresenterForTab(newValue);
            tabManager.setCurrentShop(presenter.isPresent() ? presenter.get() : null);
        }));
    }

    private void updateStageTitle() {
        getStage().setTitle("Shop Builder [" +
                activeFormatManager.getFormat().descriptor().getName() + "]");
    }

    private final BoxBlur boxBlur = new BoxBlur(5, 5, 2);

    private void blurWindow() {
        rootPane.setEffect(boxBlur);
    }

    private void removeBlurFromWindow() {
        rootPane.setEffect(null);
    }

    public TabPane getShopTabPane() {
        return shopTabPane;
    }

    public boolean byDefaultExpandItemDisplay() {
        return defaultExpandedItemDisplay.get();
    }

    private Window getWindow() {
        return rootPane.getScene().getWindow();
    }

    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }
}
