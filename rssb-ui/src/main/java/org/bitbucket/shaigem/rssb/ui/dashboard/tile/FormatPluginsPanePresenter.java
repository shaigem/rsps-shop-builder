package org.bitbucket.shaigem.rssb.ui.dashboard.tile;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.bitbucket.shaigem.rssb.event.ImportPluginRequest;
import org.bitbucket.shaigem.rssb.event.RefreshDashboardEvent;
import org.bitbucket.shaigem.rssb.event.SetActiveFormatPluginRequest;
import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;
import org.bitbucket.shaigem.rssb.plugin.RSSBPluginManager;
import org.bitbucket.shaigem.rssb.ui.dashboard.DashboardPresenter;
import org.sejda.eventstudio.DefaultEventStudio;
import org.sejda.eventstudio.annotation.EventListener;

import javax.inject.Inject;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Handles the presentation logic for the pane that is responsible for displaying any plugins.
 */
public class FormatPluginsPanePresenter implements Initializable {

    private DashboardPresenter dashboardPresenter;

    private DragAndDropManager dragAndDropManager = new DragAndDropManager();

    @FXML
    FlowPane formatItemPane;
    @FXML
    ScrollPane scrollPane;
    @FXML
    Label noPluginsFoundLabel;
    @FXML
    Label dndLabel;

    @Inject
    DefaultEventStudio eventStudio;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateDashboard();
        dragAndDropManager.init();
        noPluginsFoundLabel.visibleProperty().bind(Bindings.isEmpty(formatItemPane.getChildren()));
        eventStudio.addAnnotatedListeners(this);
    }

    @EventListener
    private void onRefreshDashboard(RefreshDashboardEvent event) {
        FormatDashboardTile tile = new FormatDashboardTile(eventStudio, (BaseShopFormatPlugin) event.getAddedPlugin());
        formatItemPane.getChildren().add(tile);
    }

    @EventListener
    private void onSetActiveFormatRequest(SetActiveFormatPluginRequest request) {
        FormatDashboardTile tile = request.getSource();
        setActiveTile(tile);
        dashboardPresenter.showBuilder(request.getShopFormatPlugin());
    }

    private void setActiveTile(FormatDashboardTile tileToSet) {
        // first set current active tile to inactive
        Optional<FormatDashboardTile> currentActiveTile =
                formatItemPane.getChildren().stream().map(
                        node -> (FormatDashboardTile) node).filter(FormatDashboardTile::isActive).findFirst();
        currentActiveTile.ifPresent(activeTile -> activeTile.setActive(false));
        // then set the new tile to become active
        tileToSet.setActive(true);
    }


    private void populateDashboard() {
        for (int i = 0; i < 26; i++)


            RSSBPluginManager.INSTANCE.getLoadedPlugins().forEach((shopFormatPlugin -> formatItemPane.getChildren().add(
                    new FormatDashboardTile(eventStudio, (BaseShopFormatPlugin) shopFormatPlugin))));
    }

    public void setDashboardPresenter(DashboardPresenter dashboardPresenter) {
        this.dashboardPresenter = dashboardPresenter;
    }

    private final class DragAndDropManager {
        void init() {
            dndLabel.setOnDragOver(event -> dragConsume(event, onDragOverConsumer()));
            dndLabel.setOnDragDropped(event -> dragConsume(event, onDragDroppedConsumer()));
            dndLabel.setOnDragExited(event -> dndLabel.setVisible(false));

            formatItemPane.setOnDragOver(event -> dragConsume(event, onDragOverConsumer()));
            formatItemPane.setOnDragDropped(event -> dragConsume(event, onDragDroppedConsumer()));
            formatItemPane.setOnDragExited(event -> dndLabel.setVisible(false));
        }

        private Consumer<DragEvent> onDragOverConsumer() {
            return dragEvent -> {
                dndLabel.setVisible(true);
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            };
        }

        private Consumer<DragEvent> onDragDroppedConsumer() {
            return dragEvent -> {
                dragEvent.getDragboard().getFiles().forEach
                        (file -> eventStudio.broadcast(new ImportPluginRequest(file)));
                dndLabel.setVisible(false);
                dragEvent.setDropCompleted(true);
            };
        }


        private void dragConsume(DragEvent event, Consumer<DragEvent> consumer) {
            if (
                    event.getDragboard().hasFiles()
                    && event.getDragboard().getFiles().stream().allMatch(file -> matchesJARExtension(file.getName()))) {
                consumer.accept(event);
            }
            event.consume();
        }

        private boolean matchesJARExtension(String fileName) {
            return FilenameUtils.wildcardMatch(fileName, "*.jar", IOCase.INSENSITIVE);
        }
    }

}
