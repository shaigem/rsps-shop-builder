package org.bitbucket.shaigem.rssb.ui.explorer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import org.bitbucket.shaigem.rssb.model.ShopRepository;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.bitbucket.shaigem.rssb.plugin.AbstractShopPlugin;
import org.bitbucket.shaigem.rssb.plugin.ShopPluginManager;
import org.bitbucket.shaigem.rssb.ui.BuilderWindowPresenter;
import org.bitbucket.shaigem.rssb.ui.shop.ShopSaveEvent;
import org.sejda.eventstudio.DefaultEventStudio;
import org.sejda.eventstudio.annotation.EventListener;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 2015-08-28.
 */
public class ShopExplorerPresenter implements Initializable {

    private BuilderWindowPresenter builderWindowPresenter;

    @Inject
    ShopRepository repository;

    @FXML
    TableView<Shop> shopTableView;
    @FXML
    TableColumn<Shop, String> nameColumn;

    @Inject
    DefaultEventStudio eventStudio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shopTableView.setItems(repository.getMasterShopDefinitions());
        setupNameColumn();
        setupCustomPluginColumns();
        shopTableView.setOnMousePressed((event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    final Shop selectedShop = shopTableView.getSelectionModel().getSelectedItem();
                    builderWindowPresenter.updateShowingShop(selectedShop);
                }
            }
        }));
        eventStudio.addAnnotatedListeners(this);
    }


    @EventListener
    private void onSaveShop(ShopSaveEvent saveShopEvent) {
        if (saveShopEvent.success()) {
            refreshExplorer();
        }
    }


    private void refreshExplorer() {
        shopTableView.refresh();
        shopTableView.sort();
        //TODO scroll to the shop that is currently open
    }

    private void setupNameColumn() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    }

    private void setupCustomPluginColumns() {
        AbstractShopPlugin plugin = ShopPluginManager.INSTANCE.getLoadedPlugin();
        plugin.getCustomTableColumns().forEach(tableColumn -> shopTableView.getColumns().add((TableColumn<Shop, ?>) tableColumn));
    }

    public void setBuilderWindowPresenter(BuilderWindowPresenter builderWindowPresenter) {
        this.builderWindowPresenter = builderWindowPresenter;
    }

}