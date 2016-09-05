package org.bitbucket.shaigem.rssb.ui.dashboard.toolbar;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.bitbucket.shaigem.rssb.event.RefreshShopFormatPluginsEvent;
import org.bitbucket.shaigem.rssb.plugin.RSSBPluginManager;
import org.sejda.eventstudio.DefaultEventStudio;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 03/09/16.
 */
public class DashboardToolbarPresenter implements Initializable {

    @FXML
    Region spacer;

    @FXML
    ToolBar toolBar;

    @Inject
    DefaultEventStudio eventStudio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(spacer, Priority.ALWAYS);
        addToolbarItems();
    }

    private final Button aboutButton = button("About");
    private final Button refreshButton = button("Refresh");

    private void addToolbarItems() {
        refreshButton.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.REFRESH));
        refreshButton.setOnAction(event -> {
            boolean added = RSSBPluginManager.INSTANCE.refreshShopFormatPlugins();
            eventStudio.broadcast(new RefreshShopFormatPluginsEvent(added));
        });
        toolBar.getItems().addAll(refreshButton, aboutButton);
    }

    private Button button(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("dashboard-toolbar-button");
        return button;
    }
}
