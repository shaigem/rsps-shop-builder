package org.bitbucket.shaigem.rssb.ui.explorer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.bitbucket.shaigem.rssb.fx.skin.UpdateableListViewSkin;
import org.bitbucket.shaigem.rssb.model.ShopRepository;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.bitbucket.shaigem.rssb.ui.BuilderWindowPresenter;
import org.bitbucket.shaigem.rssb.util.AlertDialogUtil;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created on 2015-08-28.
 */
public class ShopExplorerPresenter implements Initializable {

    private BuilderWindowPresenter builderWindowPresenter;

    @Inject
    ShopRepository repository;

    @FXML
    ListView<Shop> shopListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UpdateableListViewSkin<Shop> skin = new UpdateableListViewSkin<>(shopListView);
        shopListView.setSkin(skin);
        shopListView.setItems(repository.getMasterShopDefinitions());
        shopListView.setCellFactory((cell) -> new ListCell<Shop>() {
            @Override
            protected void updateItem(Shop item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty || item != null) {
                    setText(item.toString());
                    setOnMousePressed((mouseEvent) -> {
                        if (mouseEvent.isPrimaryButtonDown()) {
                            if (mouseEvent.getClickCount() == 2) {
                                builderWindowPresenter.updateShowingShop(item);
                            }
                        }
                    });
                } else {
                    setText("");
                    setOnMousePressed(null);
                }
            }
        });
    }

    private StringBuilder duplicateKeysStringBuilder = new StringBuilder();

    public void checkForDuplicateKeys(Shop shop) {
        if (hasDuplicateKeys(shop)) {
            List<Shop> duplicates = getDuplicateKeys(shop);
            duplicates.forEach((abstractShop -> {
                duplicateKeysStringBuilder.append("---------------------------------");
                duplicateKeysStringBuilder.append(System.lineSeparator());
                duplicateKeysStringBuilder.append("Id/Key: ").append(abstractShop.getKey());
                duplicateKeysStringBuilder.append(System.lineSeparator());
                duplicateKeysStringBuilder.append("Name: ").append(abstractShop.getName());
                duplicateKeysStringBuilder.append(System.lineSeparator());
                duplicateKeysStringBuilder.append("---------------------------------");
            }));
            Alert alert = AlertDialogUtil.createInformationDialog(Alert.AlertType.WARNING,
                    "Shop Key/ID Duplicate List:", duplicateKeysStringBuilder.toString());
            alert.setContentText(shop + " has the same key as some other shop! Please change it to avoid problems!");
            alert.setHeaderText("Duplicate Key for " + shop);
            alert.show();
        }
        duplicateKeysStringBuilder.setLength(0);
    }


    private boolean hasDuplicateKeys(Shop shop) {
        return !getDuplicateKeys(shop).isEmpty();
    }

    private List<Shop> getDuplicateKeys(Shop shop) {
        return repository.getMasterShopDefinitions().stream().filter((shopFromList) ->
                (shopFromList.getKey() == shop.getKey()) && (shop != shopFromList)).collect(Collectors.toList());
    }

    public void refreshListView() {
        ((UpdateableListViewSkin<Shop>) shopListView.getSkin()).refresh();
    }

    public void setBuilderWindowPresenter(BuilderWindowPresenter builderWindowPresenter) {
        this.builderWindowPresenter = builderWindowPresenter;
    }

}

