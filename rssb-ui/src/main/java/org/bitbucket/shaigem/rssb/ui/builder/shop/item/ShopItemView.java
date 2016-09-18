package org.bitbucket.shaigem.rssb.ui.builder.shop.item;

import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.bitbucket.shaigem.rssb.fx.control.ShopDisplayRadioButton;
import org.bitbucket.shaigem.rssb.ui.builder.shop.item.popover.ShopItemInfoPopoverPresenter;
import org.bitbucket.shaigem.rssb.ui.builder.shop.item.popover.ShopItemInfoPopoverView;
import org.bitbucket.shaigem.rssb.util.ItemAmountUtil;
import org.controlsfx.control.PopOver;

import java.util.Objects;


/**
 * Created on 2015-08-13.
 */
public class ShopItemView extends Region {

    private static final Image ITEM_BACKGROUND_ICON = new Image(ShopItemView.class.
            getClassLoader().getResourceAsStream("images/shop_holder.png"));

    private static final Image ITEM_BACKGROUND_EXPANDED = new Image(ShopItemView.class.
            getClassLoader().getResourceAsStream("images/shop_holder_exp.png"));

    private PopOver popOver;
    private ShopItemInfoPopoverPresenter shopItemInfoPopoverPresenter;

    private final ShopItemPresenter shopItemPresenter;
    private final Label amountLabel = new Label();
    private final Label nameLabel = new Label();
    private final Label indexLabel = new Label();
    private final ImageView backgroundImageView = new ImageView();
    private final ImageView itemImageView = new ImageView();
    private final Tooltip tooltip = new Tooltip();
    private final MenuItem changeAmount = new MenuItem("Change Amount for Selected Items");
    private final MenuItem deleteMenuItem = new MenuItem("Delete Selected");

    private final ContextMenu contextMenu = new ContextMenu(changeAmount,
            deleteMenuItem);

    private ShopDisplayRadioButton.DisplayMode displayMode;

    public ShopItemView() {
        super();
        getStyleClass().addAll("shop-item-view");
        amountLabel.getStyleClass().add("shop-item-amount-label");
        nameLabel.getStyleClass().add("shop-item-name-label");
        indexLabel.getStyleClass().add("shop-item-index-label");
        itemImageView.getStyleClass().add("shop-item-image-view");
        shopItemPresenter = new ShopItemPresenter(this);
        setupNodes();
        handleMouseOverEffects();
        handleShowContextMenu();
    }


    void showInfoPopOver() {
        if (Objects.isNull(popOver)) {
            popOver = constructInfoPopOver();
            shopItemInfoPopoverPresenter.refreshNodes();
        }
        if(Objects.nonNull(popOver)) {
            if (!popOver.isShowing()) {
                popOver.show(this);
            }
            shopItemInfoPopoverPresenter.refreshAmountText();
        }
    }

    public Label getAmountLabel() {
        return amountLabel;
    }

    void updateAmountLabel(int amount) {
        amountLabel.setText(ItemAmountUtil.getFormattedAmount(amount));
        amountLabel.setTextFill(ItemAmountUtil.getPaintForAmount(amount));
    }

    public void updateNameLabel(String name) {
        nameLabel.setText(name);
    }

    public void updateIdLabel(int id) {
        indexLabel.setText("ID: " + id);
    }

    public void setTooltipText(String text) {
        tooltip.setText(text);
    }

    public ImageView getItemImageView() {
        return itemImageView;
    }

    public MenuItem getChangeAmountMenuItem() {
        return changeAmount;
    }

    public MenuItem getDeleteMenuItem() {
        return deleteMenuItem;
    }

    public ShopItemPresenter getPresenter() {
        return shopItemPresenter;
    }

    public ShopItemInfoPopoverPresenter getInfoPresenter() {
        return shopItemInfoPopoverPresenter;
    }

    public PopOver getPopOver() {
        return popOver;
    }

    public void updateDisplayMode(ShopDisplayRadioButton.DisplayMode mode) {
        this.displayMode = mode;
        displayModeChanged();
    }


    private void displayModeChanged() {
        refreshNodes();
    }

    private void refreshNodes() {
        if (displayMode == ShopDisplayRadioButton.DisplayMode.EXPANDED) {
            setupExpandedNodes(false);
        } else {
            setupIconNodes(false);
        }
    }

    private void setupNodes() {
        setupIconNodes(true);
        setupExpandedNodes(true);
        getChildren().addAll(backgroundImageView, itemImageView, amountLabel, nameLabel, indexLabel);
        Tooltip.install(this, tooltip);
    }

    private void setupIconNodes(boolean initial) {
        if (initial) {
            return;
        }
        backgroundImageView.setImage(ITEM_BACKGROUND_ICON);
        getChildren().removeAll(nameLabel, indexLabel); //fixes the layout if we remove the labels instead of hiding it
    }

    private void setupExpandedNodes(boolean initial) {
        if (initial) {
            return;
        }
        backgroundImageView.setImage(ITEM_BACKGROUND_EXPANDED);
        if (shopItemPresenter.getItem() != null) {
            updateNameLabel(shopItemPresenter.getItem().getName());
            updateIdLabel(shopItemPresenter.getItem().getId());
        }
        if (!getChildren().contains(nameLabel) && !getChildren().contains(indexLabel))
            getChildren().addAll(nameLabel, indexLabel);
    }

    private PopOver constructInfoPopOver() {
        PopOver popOver = new PopOver();
        ShopItemInfoPopoverView infoView = new ShopItemInfoPopoverView();
        shopItemInfoPopoverPresenter = (ShopItemInfoPopoverPresenter) infoView.getPresenter();
        shopItemInfoPopoverPresenter.setAttachedItemView(this);
        shopItemInfoPopoverPresenter.getAmountTextField().setOnAction((event -> popOver.hide()));
        shopItemInfoPopoverPresenter.getAmountLabel().textProperty().addListener(((observable, oldValue, newValue) ->
        {
            int amount = Integer.valueOf(newValue.replace("Amount: ", ""));
            updateAmountLabel(amount);
        }));
        Parent infoParent = infoView.getView();
        popOver.setArrowSize(0);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver.setDetachable(false);
        popOver.setContentNode(infoParent);
        return popOver;
    }

    private void handleMouseOverEffects() {
        final Glow glow = new Glow();
        glow.setInput(new DropShadow(10.0, Color
                .web("#da6b0f")));
        setOnMouseEntered((event -> setEffect(glow)));
        setOnMouseExited((event -> setEffect(null)));
    }

    private void handleShowContextMenu() {
        setOnMousePressed((event -> {
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
                if(Objects.nonNull(popOver)) {
                    popOver.hide();
                }
            } else {
                contextMenu.hide();
            }
        }));
    }

    @Override
    public String getUserAgentStylesheet() {
        return ShopItemView.class.getResource("shopitem.css").toExternalForm();
    }
}
