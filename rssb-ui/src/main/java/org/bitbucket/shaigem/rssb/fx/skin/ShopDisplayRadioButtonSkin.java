package org.bitbucket.shaigem.rssb.fx.skin;

import com.sun.javafx.scene.control.behavior.ToggleButtonBehavior;
import com.sun.javafx.scene.control.skin.LabeledSkinBase;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.bitbucket.shaigem.rssb.fx.ShopDisplayImageUtil;
import org.bitbucket.shaigem.rssb.fx.control.ShopDisplayRadioButton;


/**
 * Created on 2016-03-14.
 */
public class ShopDisplayRadioButtonSkin extends LabeledSkinBase<RadioButton, ToggleButtonBehavior<RadioButton>> {

    private ShopDisplayRadioButton button;

    private StackPane rootPane;

    private ImageView imageView;

    public ShopDisplayRadioButtonSkin(ShopDisplayRadioButton button) {
        super(button, new ToggleButtonBehavior<>(button));
        this.button = button;
        createNodes();
        updateDisplayModeButton(button.getDisplayMode());
        updateChildren();
        registerListeners();

    }

    private void createNodes() {
        rootPane = new StackPane();
        imageView = new ImageView();
        rootPane.getStyleClass().setAll("display-radio");
        rootPane.getChildren().add(imageView);
    }

    private void registerListeners() {
        button.displayModeTypeProperty().addListener((o, oldVal, newVal) -> updateDisplayModeButton(newVal));
        button.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            updateDisplayModeButton(button.getDisplayMode());
        }));
    }


    private void updateDisplayModeButton(ShopDisplayRadioButton.DisplayMode type) {
        imageView.setImage(ShopDisplayImageUtil.getImageForDisplayMode(type, button.isSelected()
        ));
    }


    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (rootPane != null) {
            getChildren().add(rootPane);
        }
    }

    /***************************************************************************
     * *
     * Layout                                                                  *
     * *
     **************************************************************************/


    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(snapSize(super.computeMinHeight(width - rootPane.minWidth(-1), topInset, rightInset, bottomInset, leftInset)),
                topInset + rootPane.minHeight(-1) + bottomInset);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSize(rootPane.prefWidth(-1));
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(snapSize(super.computePrefHeight(width - rootPane.prefWidth(-1), topInset, rightInset, bottomInset, leftInset)),
                topInset + rootPane.prefHeight(-1) + bottomInset);
    }

    @Override
    protected void layoutChildren(final double x, final double y,
                                  final double w, final double h) {
        final RadioButton radioButton = getSkinnable();
        final double radioWidth = rootPane.prefWidth(-1);
        final double radioHeight = rootPane.prefHeight(-1);
        final double computeWidth = Math.max(radioButton.prefWidth(-1), radioButton.minWidth(-1));
        final double labelWidth = Math.min(computeWidth - radioWidth, w - snapSize(radioWidth));
        final double labelHeight = Math.min(radioButton.prefHeight(labelWidth), h);
        final double maxHeight = Math.max(radioHeight, labelHeight);
        final double xOffset = computeXOffset(w, labelWidth + radioWidth, radioButton.getAlignment().getHpos()) + x;
        final double yOffset = computeYOffset(h, maxHeight, radioButton.getAlignment().getVpos()) + y;

        layoutLabelInArea(xOffset + radioWidth, yOffset, labelWidth, maxHeight, radioButton.getAlignment());
        rootPane.resize(snapSize(radioWidth), snapSize(radioHeight));
        positionInArea(rootPane, xOffset, yOffset, radioWidth, maxHeight, 0, radioButton.getAlignment().getHpos(), radioButton.getAlignment().getVpos());
    }


    private static double computeXOffset(double width, double contentWidth, HPos hpos) {
        if (hpos == null) {
            return 0;
        }
        switch (hpos) {
            case LEFT:
                return 0;
            case CENTER:
                return (width - contentWidth) / 2;
            case RIGHT:
                return width - contentWidth;
            default:
                return 0;
        }
    }

    private static double computeYOffset(double height, double contentHeight, VPos vpos) {
        if (vpos == null) {
            return 0;
        }
        switch (vpos) {
            case TOP:
                return 0;
            case CENTER:
                return (height - contentHeight) / 2;
            case BOTTOM:
                return height - contentHeight;
            default:
                return 0;
        }
    }

}
