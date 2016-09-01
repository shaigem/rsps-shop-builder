package org.bitbucket.shaigem.rssb.ui.shop.item;

import javafx.geometry.Pos;
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
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.bitbucket.shaigem.rssb.fx.control.ShopDisplayRadioButton;
import org.bitbucket.shaigem.rssb.ui.shop.item.popover.ShopItemInfoPopoverPresenter;
import org.bitbucket.shaigem.rssb.ui.shop.item.popover.ShopItemInfoPopoverView;
import org.bitbucket.shaigem.rssb.util.ItemAmountUtil;
import org.controlsfx.control.PopOver;

import java.util.Optional;


/**
 * Created on 2015-08-13.
 */
public class ShopItemView extends Region {

    private static final Image ITEM_BACKGROUND_ICON = new Image(ShopItemView.class.
            getClassLoader().getResourceAsStream("images/shop_holder.png"));

    private static final Image ITEM_BACKGROUND_EXPANDED = new Image(ShopItemView.class.
            getClassLoader().getResourceAsStream("images/shop_holder_exp.png"));

    private Optional<PopOver> infoPopOver;
    private ShopItemInfoPopoverPresenter shopItemInfoPopoverPresenter;

    private final ShopItemPresenter shopItemPresenter;
    private final Label amountLabel = new Label();
    private final Label nameLabel = new Label();
    private final Label indexLabel = new Label();
    private final ImageView backgroundImageView = new ImageView();
    private final ImageView itemImageView = new ImageView();
    private final Tooltip tooltip = new Tooltip();
    private final MenuItem copyMenuItem = new MenuItem("Copy");
    private final MenuItem changeAmount = new MenuItem("Change Amount for Selected Items");
    private final MenuItem deleteMenuItem = new MenuItem("Delete Selected");

    private final ContextMenu contextMenu = new ContextMenu(changeAmount,
            deleteMenuItem, copyMenuItem);

    private ShopDisplayRadioButton.DisplayMode displayMode;


    public ShopItemView() {
        super();
        getStyleClass().addAll("shop-item-view");
        shopItemPresenter = new ShopItemPresenter(this);
        infoPopOver = Optional.empty();
        setupNodes();
//        updateDisplayMode(ShopDisplayRadioButton.DisplayMode.ICON);
        handleMouseOverEffects();
        handleShowContextMenu();
    }


    public void showInfoPopOver() {
        if (!infoPopOver.isPresent()) {
            infoPopOver = Optional.of(constructInfoPopOver());
            shopItemInfoPopoverPresenter.refreshNodes();
        }
        infoPopOver.ifPresent(popOver -> {
            if (!popOver.isShowing())
                popOver.show(this);
            shopItemInfoPopoverPresenter.refreshAmountText();
        });
    }

    public Label getAmountLabel() {
        return amountLabel;
    }

    public void updateAmountLabel(int amount) {
        amountLabel.setText(ItemAmountUtil.getFormattedAmount(amount));
        amountLabel.setTextFill(ItemAmountUtil.getPaintForAmount(amount));
    }

    public void updateNameLabel(String name) {
        nameLabel.setText(name);
    }

    public void updateIdLabel(int id) {
        indexLabel.setText("Id: " + id);
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

    public MenuItem getCopyMenuItem() {
        return copyMenuItem;
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

    public Optional<PopOver> getInfoPopOver() {
        return infoPopOver;
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
            amountLabel.setLayoutX(4);
            amountLabel.setLayoutY(2);
            amountLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            amountLabel.setTextFill(ItemAmountUtil.getPaintForAmount(-1));
            itemImageView.setLayoutX(9);
            itemImageView.setLayoutY(11);
            return;
        }
        backgroundImageView.setImage(ITEM_BACKGROUND_ICON);
        getChildren().removeAll(nameLabel, indexLabel); //fixes the layout if we remove the labels instead of hiding it
    }

    private void setupExpandedNodes(boolean initial) {
        if (initial) {
            nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            nameLabel.setTextFill(Paint.valueOf("#f7edb7"));
            nameLabel.setAlignment(Pos.CENTER_RIGHT);
            nameLabel.setPrefSize(90, 20);
            nameLabel.setLayoutX(52);
            nameLabel.setLayoutY(8);
            indexLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            indexLabel.setTextFill(Paint.valueOf("#e5b051"));
            indexLabel.setPrefSize(90, 20);
            indexLabel.setAlignment(Pos.CENTER_RIGHT);
            indexLabel.setLayoutX(56);
            indexLabel.setLayoutY(33);
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
                infoPopOver.ifPresent(PopOver::hide);
            } else {
                contextMenu.hide();
            }
        }));
    }
}
