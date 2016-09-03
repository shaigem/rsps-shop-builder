package org.bitbucket.shaigem.rssb.ui.properties;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import org.bitbucket.shaigem.rssb.fx.controlsfx.CustomPropertyEditorFactory;
import org.bitbucket.shaigem.rssb.ui.shop.ShopCloseEvent;
import org.bitbucket.shaigem.rssb.model.ShopPropertiesManager;
import org.bitbucket.shaigem.rssb.model.ShopTabManager;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.bitbucket.shaigem.rssb.ui.shop.ShopSaveEvent;
import org.bitbucket.shaigem.rssb.ui.shop.ShopPresenter;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanPropertyUtils;
import org.sejda.eventstudio.DefaultEventStudio;
import org.sejda.eventstudio.annotation.EventListener;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is responsible for handling the presentation logic for the properties section of a shop.
 * Some general properties include:
 * <ul>
 * <li>Name of the shop
 * <li>Is the shop a general store?
 * <li>Currency id like coins (id = 995)
 *
 * @author Abyss
 *         Created on 2015-08-13.
 */
public class PropertiesPresenter implements Initializable {
    /**
     * An array list that will only contain ONE element. The reason why this is done in a <code>ArrayList</code>
     * is so that a <code>extractor</code> can be applied to the list so the element's properties can be observed.
     */
    private final ObservableList<Shop> singletonShop = FXCollections.observableArrayList
            (Shop.extractor());

    private ListChangeListener<Shop> propertyChangeListener = null;

    @FXML
    AnchorPane rootPane;
    @FXML
    PropertySheet propertySheet;

    @Inject
    ShopTabManager tabManager;
    @Inject
    ShopPropertiesManager propertiesManager;
    @Inject
    DefaultEventStudio eventStudio;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        propertySheet.visibleProperty().bind(tabManager.currentShopProperty().isNotNull());
        propertySheet.setPropertyEditorFactory(new CustomPropertyEditorFactory());
        tabManager.currentShopProperty().addListener(((observable, oldPresenterValue, newPresenterValue) -> {
            if (newPresenterValue != null) {
                loadProperties(newPresenterValue);
            }
        }));
        eventStudio.addAnnotatedListeners(this);
    }

    @EventListener
    private void onShopTabClose(ShopCloseEvent event) {
        // we gotta clear the cached properties as we are not dealing with it anymore
        propertiesManager.removeProperties(event.getClosingShopPresenter());
    }

    @EventListener
    private void onSaveShop(ShopSaveEvent saveEvent) {
        if (saveEvent.success()) { // only when it actually saved something
            checkAndUpdateShopProperties(saveEvent.getSavedShopPresenter().
                    getPropertySheetItemsForShop());
        }
    }

    private void loadProperties(ShopPresenter shopPresenter) {
        final Shop shopToEdit;
        final ObservableList<PropertySheet.Item> beanProperties;

        if (propertiesManager.hasCached(shopPresenter)) {
            Pair<Shop, ObservableList<PropertySheet.Item>> shopPropertiesPair =
                    propertiesManager.getCachedProperties(shopPresenter);
            shopToEdit = shopPropertiesPair.getKey();
            beanProperties = shopPropertiesPair.getValue();
        } else {
            // we must edit a copied version of the shop so it doesn't automatically
            // save the new properties until the user saves manually
            shopToEdit = shopPresenter.getShop().copy();
            beanProperties = BeanPropertyUtils.getProperties(shopToEdit, propertyDescriptor ->
                    !propertyDescriptor.getName().equals("name")
                            && !propertyDescriptor.getName().equals("items"));
            propertiesManager.cacheProperties(shopPresenter, shopToEdit, beanProperties);
        }
        setShop(shopToEdit);
        bindChangeListenerToPresenter(shopPresenter);
        propertySheet.getItems().setAll(beanProperties);
        //TODO we should probably load properties on a separate thread

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

    /**
     * Updates the original shop's properties with the edited ones.
     *
     * @param oldItems the original/old shop property items
     */
    private void checkAndUpdateShopProperties(ObservableList<PropertySheet.Item> oldItems) {
        oldItems.forEach((oldItem ->
                propertySheet.getItems().filtered(updatedItem ->
                        oldItem.getName().equals(updatedItem.getName())
                                && oldItem.getValue() != updatedItem.getValue()).
                        forEach((newItem -> oldItem.setValue(newItem.getValue())))));
    }

    /**
     * If any of the properties that are being edited change, it will make the shop editor dirty (require saving).
     *
     * @param presenter the currently selected shop editor presenter
     */
    private void bindChangeListenerToPresenter(ShopPresenter presenter) {
        // have to constantly rebind the listener when the editing shop changes!
        ListChangeListener<Shop> listener = ShopPresenter.onPropertyChangeListener(presenter);
        if (propertyChangeListener == null) {
            propertyChangeListener = listener;
        } else {
            getEditingShopList().removeListener(propertyChangeListener);
            propertyChangeListener = listener;
        }
        getEditingShopList().addListener(propertyChangeListener);
    }


    private void setShop(Shop shop) {
        if (singletonShop.isEmpty()) {
            singletonShop.add(0, shop);
        } else {
            singletonShop.set(0, shop);
        }
    }

    private ObservableList<Shop> getEditingShopList() {
        return singletonShop;
    }
}
