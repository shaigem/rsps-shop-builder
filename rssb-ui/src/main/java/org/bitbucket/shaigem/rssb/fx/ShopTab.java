package org.bitbucket.shaigem.rssb.fx;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;
import javafx.scene.control.Tab;

public class ShopTab extends Tab {

    public ShopTab(String title) {
        super(title);
    }

    public void requestClose() {
        TabPaneBehavior behavior = getBehavior();
        if (behavior.canCloseTab(this)) {
            behavior.closeTab(this);
        }
    }

    private TabPaneBehavior getBehavior() {
        return ((TabPaneSkin) getTabPane().getSkin()).getBehavior();
    }
}