package org.bitbucket.shaigem.rssb.fx;

import javafx.scene.image.Image;
import org.bitbucket.shaigem.rssb.fx.control.ShopDisplayRadioButton;

/**
 * Created on 2016-03-14.
 */
public class ShopDisplayImageUtil {

    private final static Image INACTIVE_EXPANDED = new Image(ShopDisplayImageUtil.class.getClassLoader().getResourceAsStream
            ("images/inactive_layout1.png"));

    private final static Image INACTIVE_ICON = new Image(ShopDisplayImageUtil.class.getClassLoader().getResourceAsStream
            ("images/inactive_layout2.png"));


    private final static Image ACTIVE_EXPANED = new Image(ShopDisplayImageUtil.class.getClassLoader().getResourceAsStream
            ("images/active_layout1.png"));

    private final static Image ACTIVE_ICON = new Image(ShopDisplayImageUtil.class.getClassLoader().getResourceAsStream
            ("images/active_layout2.png"));


    public static Image getImageForDisplayMode(ShopDisplayRadioButton.DisplayMode mode, boolean selected) {
        if (mode == ShopDisplayRadioButton.DisplayMode.EXPANDED) {
            return selected ? ACTIVE_EXPANED : INACTIVE_EXPANDED;
        } else {
            return selected ? ACTIVE_ICON : INACTIVE_ICON;
        }

    }

}