package org.bitbucket.shaigem.rssb.ui.dashboard;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.bitbucket.shaigem.rssb.event.SetActiveFormatPluginRequest;
import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;
import org.bitbucket.shaigem.rssb.plugin.ShopPluginManager;
import org.bitbucket.shaigem.rssb.ui.BuilderWindowPresenter;
import org.bitbucket.shaigem.rssb.ui.BuilderWindowView;
import org.bitbucket.shaigem.rssb.ui.dashboard.toolbar.DashboardToolbarView;
import org.sejda.eventstudio.DefaultEventStudio;
import org.sejda.eventstudio.annotation.EventListener;

import javax.inject.Inject;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created on 03/09/16.
 */
public class DashboardPresenter implements Initializable {

    @FXML
    BorderPane root;
    @FXML
    StackPane center;
    @FXML
    FlowPane formatItemPane;
    @FXML
    Label noPluginsFoundLabel;

    @Inject
    DefaultEventStudio eventStudio;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setToolBar();
        noPluginsFoundLabel.visibleProperty().bind(Bindings.isEmpty(formatItemPane.getChildren()));
        ShopPluginManager.INSTANCE.getLoadedPlugins().forEach((shopFormatPlugin -> formatItemPane.getChildren().add(
                new FormatDashboardTile(eventStudio, (BaseShopFormatPlugin) shopFormatPlugin))));
        eventStudio.addAnnotatedListeners(this);
    }

    @EventListener
    private void onSetActiveFormatRequest(SetActiveFormatPluginRequest request) {
        FormatDashboardTile tile = request.getSource();
        setActiveTile(tile);
        showBuilder(request.getShopFormatPlugin());
    }


    private void showBuilder(BaseShopFormatPlugin formatPlugin) {
        Stage dashboardStage = getDashboardStage();
        Stage builderStage = getBuilder().getKey();
        BuilderWindowPresenter presenter = getBuilder().getValue();
        presenter.onShow(formatPlugin);
        dashboardStage.hide();
        builderStage.show();
    }

    private Pair<Stage, BuilderWindowPresenter> builderPair;

    private Pair<Stage, BuilderWindowPresenter> getBuilder() {
        if (builderPair == null) {
            BuilderWindowView view = new BuilderWindowView();
            BuilderWindowPresenter presenter = (BuilderWindowPresenter) view.getPresenter();
            Stage stage = new Stage();
            Scene scene = new Scene(view.getView());
            stage.setTitle("Shop Builder");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.initOwner(getDashboardStage());
            Pair<Stage, BuilderWindowPresenter> pair = new Pair<>(stage, presenter);
            builderPair = pair;
            return pair;
        }
        return builderPair;
    }


    private void setToolBar() {
        DashboardToolbarView toolbar = new DashboardToolbarView();
        root.setTop(toolbar.getView());
    }

    private void setActiveTile(FormatDashboardTile tileToSet) {
        // first set current active tile to inactive
        Optional<FormatDashboardTile> currentActiveTile =
                formatItemPane.getChildren().filtered(node ->
                        node instanceof FormatDashboardTile).stream().map(
                        node -> (FormatDashboardTile) node).filter(FormatDashboardTile::isActive).findFirst();
        currentActiveTile.ifPresent(activeTile -> activeTile.setActive(false));
        // then set the new tile to become active
        tileToSet.setActive(true);
    }

    private Stage getDashboardStage() {
        return (Stage) root.getScene().getWindow();
    }
}
