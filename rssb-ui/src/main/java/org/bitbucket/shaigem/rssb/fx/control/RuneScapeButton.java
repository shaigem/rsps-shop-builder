package org.bitbucket.shaigem.rssb.fx.control;

import javafx.scene.control.Button;

/**
 * Created on 2016-03-13.
 */
public class RuneScapeButton extends Button {

    public RuneScapeButton() {
        this("");
    }

    public RuneScapeButton(String text) {
        super(text);
        getStyleClass().add("runescape-button");

    }

    @Override
    public String getUserAgentStylesheet() {
        return RuneScapeButton.class.getResource("runescape_btn_style.css").toExternalForm();
    }


}
