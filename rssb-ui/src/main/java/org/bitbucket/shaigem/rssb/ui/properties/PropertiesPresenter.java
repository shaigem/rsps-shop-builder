package org.bitbucket.shaigem.rssb.ui.properties;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.bitbucket.shaigem.rssb.model.ShopTabManager;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanPropertyUtils;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This class is responsible for handling the presentation logic for the properties section of a shop.
 * Some general properties include:
 * <ul>
 * <li>Name of the shop
 * <li>Is the shop a general store?
 * <li>Currency type like coins
 *
 * @author Abyss
 *         Created on 2015-08-13.
 */
public class PropertiesPresenter implements Initializable {

    @FXML
    AnchorPane rootPane;
    private PropertySheet propertySheet = new PropertySheet();
    @Inject
    ShopTabManager tabManager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        propertySheet.visibleProperty().bind(tabManager.currentShopProperty().isNotNull());

        tabManager.currentShopProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadProperties(newValue.getShop());
            }
        }));

    }

    private void loadProperties(Shop shop) {
        propertySheet.getItems().setAll(BeanPropertyUtils.getProperties(shop).stream().filter
                ((item -> !item.getName().equals("name"))).collect(Collectors.toList()));

     /*   Task task = new Task<ObservableList<PropertySheet.Item>>() {
            @Override
            protected ObservableList<PropertySheet.Item> call() throws Exception {
                return BeanPropertyUtils.getProperties(shop);
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                Platform.runLater(() -> propertySheet.getItems().setAll((Collection<? extends PropertySheet.Item>) event.getSource().getValue()));
            }
        });
        new Thread(task).start();
/*/
    }

}
