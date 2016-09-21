package org.bitbucket.shaigem.rssb.ui.builder.explorer;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import org.bitbucket.shaigem.rssb.event.ActiveFormatPluginChangedEvent;
import org.bitbucket.shaigem.rssb.event.CreateNewShopTabRequest;
import org.bitbucket.shaigem.rssb.event.RemoveAllShopsEvent;
import org.bitbucket.shaigem.rssb.event.ShopSaveEvent;
import org.bitbucket.shaigem.rssb.model.ActiveFormatManager;
import org.bitbucket.shaigem.rssb.model.ShopRepository;
import org.bitbucket.shaigem.rssb.model.ShopTabManager;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;
import org.bitbucket.shaigem.rssb.ui.search.SearchPresenter;
import org.bitbucket.shaigem.rssb.ui.search.SearchView;
import org.sejda.eventstudio.DefaultEventStudio;
import org.sejda.eventstudio.annotation.EventListener;

import javax.inject.Inject;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created on 2015-08-28.
 */
public class ShopExplorerPresenter implements Initializable {

    @Inject
    ShopRepository repository;
    @Inject
    ShopTabManager shopTabManager;
    @Inject
    DefaultEventStudio eventStudio;
    @Inject
    ActiveFormatManager activeFormatManager;

    @FXML
    StackPane searchPane;

    @FXML
    TableView<Shop> shopTableView;
    @FXML
    Button createNewShopButton;
    @FXML
    Button deleteSelectedShopButton;
    @FXML
    Button removeAllShopsButton;
    @FXML
    MenuItem duplicateMenuItem;
    @FXML
    MenuItem openMenuItem;
    @FXML
    MenuItem removeMenuItem;

    private SearchPresenter searchPresenter;
    private String searchPattern;
    private FilteredList<Shop> filteredList;
    private final TableColumn<Shop, String> nameColumn = new TableColumn<>("Name");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableItems();
        shopTableView.setPlaceholder(new Label("No shops loaded"));
        setSearchField();
        setupNameColumn();
        onTableMousePressed();
        disableControlsUnlessHasSelection();
        createNewShopButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.PLUS, "1.4em"));
        deleteSelectedShopButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.DELETE, "1.4em"));
        removeAllShopsButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.PLAYLIST_REMOVE, "1.4em"));
        eventStudio.addAnnotatedListeners(this);
    }

    @FXML
    public void onOpenAction() {
        final Optional<Shop> shopToOpen =
                Optional.ofNullable(shopTableView.getSelectionModel().getSelectedItem());
        shopToOpen.ifPresent(shop -> eventStudio.broadcast(new CreateNewShopTabRequest(shop)));
    }

    @FXML
    public void onDuplicateAction() {
        final Optional<Shop> shopToCopy =
                Optional.ofNullable(shopTableView.getSelectionModel().getSelectedItem());
        shopToCopy.ifPresent(shop -> {
            final Shop copiedShop = shop.copy();
            copiedShop.setName(shop.getName() + "(c)");
            repository.getMasterShopDefinitions().add(copiedShop);
            eventStudio.broadcast(new CreateNewShopTabRequest(copiedShop));
            shopTableView.getSelectionModel().select(copiedShop);
            shopTableView.scrollTo(copiedShop);
            searchPresenter.resetSearch();
        });
    }

    @FXML
    public void onNewShopAction() {
        final Shop newShop = activeFormatManager.getFormat().getDefaultShop().copy();
        repository.getMasterShopDefinitions().add(newShop);
        eventStudio.broadcast(new CreateNewShopTabRequest(newShop));
        shopTableView.getSelectionModel().select(newShop);
        shopTableView.scrollTo(newShop);
        searchPresenter.resetSearch();
    }

    @FXML
    public void onRemoveAllShopsAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
        alert.setTitle("Remove All Shops");
        alert.setContentText("Are you sure you want to delete all shops?");
        alert.showAndWait().ifPresent(buttonType -> {
            if(buttonType.equals(ButtonType.YES)) {
                repository.getMasterShopDefinitions().clear();
                searchPresenter.resetSearch();
                eventStudio.broadcast(new RemoveAllShopsEvent());
            }

        });
    }

    @FXML
    public void onRemoveSelectedShopAction() {
        final Optional<Shop> shopToRemove =
                Optional.ofNullable(shopTableView.getSelectionModel().getSelectedItem());
        shopToRemove.ifPresent(shop -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
            alert.setHeaderText("Delete: " + shop);
            alert.setContentText("Are you sure you would like to delete this shop? You cannot undo this action.");
            alert.getDialogPane().setPrefWidth(500);
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType.equals(ButtonType.YES)) {
                    boolean removed = repository.getMasterShopDefinitions().remove(shop);
                    shopTableView.scrollTo(shopTableView.getSelectionModel().getSelectedIndex());
                    if (removed) {
                        shopTabManager.isOpen(shop).ifPresent(shopPresenter ->
                                shopTabManager.forceClose(shopPresenter));
                    }
                }
            });
        });
    }

    private void setupTableItems() {
        // filtered list is read-only and so if we want column sorting to work with filtering,
        // we would have to wrap it into a sorted list
        filteredList = new FilteredList<>(repository.getMasterShopDefinitions(), l -> true);
        SortedList<Shop> sortedList = new SortedList<>(filteredList);
        shopTableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(shopTableView.comparatorProperty());
    }

    private void onTableMousePressed() {
        shopTableView.setOnMousePressed((event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    onOpenAction();
                }
            }
        }));
    }

    private void setSearchField() {
        SearchView searchView = new SearchView();
        SearchPresenter searchPresenter = (SearchPresenter) searchView.getPresenter();
        searchPresenter.setPromptText("Search Shops");
        searchPresenter.textProperty().addListener(
                ((observable, oldValue, newValue) -> setSearchPattern(newValue)));
        this.searchPresenter = searchPresenter;
        searchPane.getChildren().add(searchView.getView());
    }


    private void setSearchPattern(String pattern) {
        this.searchPattern = pattern;
        searchPatternChange();
    }

    private void searchPatternChange() {
        filteredList.setPredicate(item ->
                searchPattern == null || searchPattern.isEmpty() || searchPattern.length() < 3 || item.getName().toLowerCase().contains(searchPattern.toLowerCase()));
    }

    @EventListener
    private void onRemoveAllShops(RemoveAllShopsEvent event) {
        searchPresenter.resetSearch();
    }

    @EventListener
    private void onSaveShop(ShopSaveEvent saveShopEvent) {
        if (saveShopEvent.onSuccess()) {
            refreshExplorer();
            final Shop savedShop = saveShopEvent.getSavedShopPresenter().getShop();
            shopTableView.scrollTo(savedShop);
            shopTableView.getSelectionModel().select(savedShop);
        }
    }

    @EventListener
    private void onActiveFormatPluginChanged(ActiveFormatPluginChangedEvent event) {
        shopTableView.getColumns().clear();
        shopTableView.getColumns().add(nameColumn);
        setupCustomPluginColumns(event.getFormatPlugin());
    }

    /**
     * Certain controls (copy, delete, etc.) are disabled unless there are any
     * items/indices selected in the shop table view.
     */
    private void disableControlsUnlessHasSelection() {
        final BooleanBinding emptyBinding =
                Bindings.isEmpty(shopTableView.getSelectionModel().getSelectedIndices());
        deleteSelectedShopButton.disableProperty().bind(emptyBinding);
        duplicateMenuItem.disableProperty().bind(emptyBinding);
        openMenuItem.disableProperty().bind(emptyBinding);
        removeMenuItem.disableProperty().bind(emptyBinding);
    }

    private void refreshExplorer() {
        shopTableView.refresh();
        shopTableView.sort();
    }

    private void setupNameColumn() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        shopTableView.getColumns().add(nameColumn);
    }

    private void setupCustomPluginColumns(BaseShopFormatPlugin baseShopFormatPlugin) {
        baseShopFormatPlugin.getCustomTableColumns().forEach(tableColumn -> shopTableView.getColumns().add((TableColumn<Shop, ?>) tableColumn));
    }
}