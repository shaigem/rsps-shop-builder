package org.bitbucket.shaigem.rssb.ui.builder.explorer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import org.bitbucket.shaigem.rssb.event.ActiveFormatPluginChangedEvent;
import org.bitbucket.shaigem.rssb.event.CreateNewShopTabRequest;
import org.bitbucket.shaigem.rssb.event.RemoveShopRequest;
import org.bitbucket.shaigem.rssb.event.ShopSaveEvent;
import org.bitbucket.shaigem.rssb.model.ShopRepository;
import org.bitbucket.shaigem.rssb.model.ShopTabManager;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;
import org.sejda.eventstudio.DefaultEventStudio;
import org.sejda.eventstudio.annotation.EventListener;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 2015-08-28.
 */
public class ShopExplorerPresenter implements Initializable {

    @Inject
    ShopRepository repository;

    @Inject
    ShopTabManager shopTabManager;

    @FXML
    TableView<Shop> shopTableView;


    private final TableColumn<Shop, String> nameColumn = new TableColumn<>("Name");

    @Inject
    DefaultEventStudio eventStudio;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shopTableView.setItems(repository.getMasterShopDefinitions());
        setupNameColumn();
        shopTableView.setOnMousePressed((event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    final Shop selectedShop = shopTableView.getSelectionModel().getSelectedItem();
                    eventStudio.broadcast(new CreateNewShopTabRequest(selectedShop));
                }
            }
        }));
        eventStudio.addAnnotatedListeners(this);
    }

    @EventListener
    private void onRemoveShopRequest(RemoveShopRequest request) {
        final Shop shopToRemove = shopTableView.getSelectionModel().getSelectedItem();
        boolean removed = repository.getMasterShopDefinitions().remove(shopToRemove);
        if (removed) {
            shopTabManager.isOpen(shopToRemove).ifPresent(shopPresenter ->
                    shopTabManager.forceClose(shopPresenter));
        }
    }


    @EventListener
    private void onSaveShop(ShopSaveEvent saveShopEvent) {
        if (saveShopEvent.onSuccess()) {
            refreshExplorer();
        }
    }

    @EventListener
    private void onActiveFormatPluginChanged(ActiveFormatPluginChangedEvent event) {
        shopTableView.getColumns().clear();
        shopTableView.getColumns().add(nameColumn);
        setupCustomPluginColumns(event.getFormatPlugin());
    }


    private void refreshExplorer() {
        shopTableView.refresh();
        shopTableView.sort();
        //TODO scroll to the shop that is currently open
    }

    private void setupNameColumn() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        shopTableView.getColumns().add(nameColumn);
    }

    private void setupCustomPluginColumns(BaseShopFormatPlugin baseShopFormatPlugin) {
        baseShopFormatPlugin.getCustomTableColumns().forEach(tableColumn -> shopTableView.getColumns().add((TableColumn<Shop, ?>) tableColumn));
    }
}