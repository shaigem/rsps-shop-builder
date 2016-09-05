package org.bitbucket.shaigem.rssb.ui.dashboard.toolbar;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.bitbucket.shaigem.rssb.plugin.RSSBPluginManager;
import org.bitbucket.shaigem.rssb.ui.dashboard.DashboardPresenter;
import org.bitbucket.shaigem.rssb.util.AlertDialogUtil;
import org.sejda.eventstudio.DefaultEventStudio;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static de.jensd.fx.glyphs.GlyphsDude.createIcon;

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

    private DashboardPresenter dashboardPresenter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(spacer, Priority.ALWAYS);
        addToolbarItems();
    }

    private void importPlugin() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR files", "*.jar"));
        Optional<File> fileOptional =
                Optional.ofNullable(fileChooser.showOpenDialog(dashboardPresenter.getDashboardWindow()));
        fileOptional.ifPresent(source -> {
            //TODO use RSSBPluginManager.DEFAULT_SHOP_PLUGINS_URI when jar plugins are built
            File to = new File(System.getProperty("user.dir") + "/plugins/" + source.getName());
            // try to load the selected plugin first
            boolean added = RSSBPluginManager.INSTANCE.load(source.toURI());
            if (added) {
                // if the plugin loaded successfully
                // try to copy it to the plugin's directory
                try {
                    java.nio.file.Files.copy(source.toPath(), to.toPath());
                } catch (IOException e) {
                    Alert alert = AlertDialogUtil.createExceptionDialog(e);
                    alert.setContentText("An exception has occurred while " +
                            "trying to import the requested plugin!");
                    alert.show();
                }
            } else {
                // nothing imported
                Alert alert = AlertDialogUtil.createInformationDialog(Alert.AlertType.INFORMATION, "",
                        "The requested file cannot be imported. This might be because the file" +
                                " is not a plugin or if the file is already added as a plugin.");
                alert.setHeaderText("Nothing Imported");
                alert.setContentText("Nothing interesting happens.");
                alert.show();
            }
        });
    }

    private final Button importButton =
            button(createIcon(MaterialDesignIcon.IMPORT, "1.1em"), "Import");
    private final Button refreshButton = button(createIcon(FontAwesomeIcon.REFRESH), "Refresh");
    private final Button aboutButton = button(createIcon(MaterialDesignIcon.INFORMATION_OUTLINE, "1.2em"),
            "About");

    private void addToolbarItems() {
        importButton.setOnAction(event -> importPlugin());
        refreshButton.setOnAction(event -> RSSBPluginManager.INSTANCE.refreshShopFormatPlugins());
        toolBar.getItems().addAll(importButton, refreshButton, aboutButton);
    }

    private Button button(Text icon, String text) {
        Button button = button(text);
        button.setGraphic(icon);
        return button;
    }

    private Button button(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("dashboard-toolbar-button");
        return button;
    }

    public void setDashboardPresenter(DashboardPresenter dashboardPresenter) {
        this.dashboardPresenter = dashboardPresenter;
    }
}
