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
import org.bitbucket.shaigem.rssb.event.ImportPluginRequest;
import org.bitbucket.shaigem.rssb.plugin.RSSBPluginManager;
import org.bitbucket.shaigem.rssb.ui.dashboard.DashboardPresenter;
import org.bitbucket.shaigem.rssb.util.AlertDialogUtil;
import org.sejda.eventstudio.DefaultEventStudio;
import org.sejda.eventstudio.annotation.EventListener;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
        eventStudio.addAnnotatedListeners(this);
    }

    @EventListener
    private void onImportPluginRequest(ImportPluginRequest pluginRequest) {
        File fileToImport = pluginRequest.getFileToImport();
        importFileAsPlugin(fileToImport);
    }

    private void importFileAsPlugin(File fileToImport) {
        //TODO use RSSBPluginManager.DEFAULT_SHOP_PLUGINS_URI when jar plugins are built
        File to = new File(System.getProperty("user.dir") + "/plugins/" + fileToImport.getName());
        // try to load the selected plugin first
        try {
            boolean added = RSSBPluginManager.INSTANCE.load(fileToImport.toURI());
            if (added) {
                // if the plugin loaded successfully
                // try to copy it to the plugin's directory
                try {
                    Files.copy(fileToImport.toPath(), to.toPath());
                } catch (IOException e) {
                    Alert alert = AlertDialogUtil.createExceptionDialog(e);
                    alert.setHeaderText("Cannot Copy Plugin");
                    alert.setContentText("Cannot copy plugin to plugins folder! Plugin can still be used.");
                    alert.getDialogPane().setPrefWidth(450);
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
        } catch (IllegalAccessError exception) { // rare exception
            Alert alert = AlertDialogUtil.createExceptionDialog(exception);
            alert.setHeaderText("Error Importing Plugin");
            alert.setContentText("Cannot import plugin. Please check if this jar is a RSSB plugin.");
            alert.getDialogPane().setPrefWidth(450);
            alert.show();
        }
    }

    private void openImportChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR files", "*.jar"));
        Optional<File> fileOptional =
                Optional.ofNullable(fileChooser.showOpenDialog(dashboardPresenter.getDashboardWindow()));
        fileOptional.ifPresent(this::importFileAsPlugin);
    }

    private final Button importButton =
            button(createIcon(MaterialDesignIcon.IMPORT, "1.1em"), "Import");
    private final Button refreshButton = button(createIcon(FontAwesomeIcon.REFRESH), "Refresh");
    private final Button aboutButton = button(createIcon(MaterialDesignIcon.INFORMATION_OUTLINE, "1.2em"),
            "About");

    private void addToolbarItems() {
        //TODO About dialog
        importButton.setOnAction(event -> openImportChooser());
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
