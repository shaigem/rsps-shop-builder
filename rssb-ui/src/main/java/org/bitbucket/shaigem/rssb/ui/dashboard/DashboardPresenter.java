package org.bitbucket.shaigem.rssb.ui.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.bitbucket.shaigem.rssb.event.RefreshDashboardEvent;
import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;
import org.bitbucket.shaigem.rssb.plugin.RSSBPluginManager;
import org.bitbucket.shaigem.rssb.ui.builder.BuilderWindowPresenter;
import org.bitbucket.shaigem.rssb.ui.builder.BuilderWindowView;
import org.bitbucket.shaigem.rssb.ui.dashboard.container.FormatPluginsPanePresenter;
import org.bitbucket.shaigem.rssb.ui.dashboard.container.FormatPluginsPaneView;
import org.bitbucket.shaigem.rssb.ui.dashboard.toolbar.DashboardToolbarPresenter;
import org.bitbucket.shaigem.rssb.ui.dashboard.toolbar.DashboardToolbarView;
import org.sejda.eventstudio.DefaultEventStudio;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 03/09/16.
 */
public class DashboardPresenter implements Initializable {

    @FXML
    BorderPane root;

    @Inject
    DefaultEventStudio eventStudio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFormatPluginsPane(); // must be first or else it messes the shadow in setToolBar!
        setToolBar();
        RSSBPluginManager.INSTANCE.addListenerToPluginSet(change -> {
            if (change.wasAdded()) {
                eventStudio.broadcast(new RefreshDashboardEvent(change.getElementAdded()));
            }
        });
    }


    public void showBuilder(BaseShopFormatPlugin formatPlugin) {
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
        DashboardToolbarPresenter presenter = (DashboardToolbarPresenter) toolbar.getPresenter();
        presenter.setDashboardPresenter(this);
        root.setTop(toolbar.getView());
    }

    private void setFormatPluginsPane() {
        FormatPluginsPaneView view = new FormatPluginsPaneView();
        FormatPluginsPanePresenter presenter = (FormatPluginsPanePresenter) view.getPresenter();
        presenter.setDashboardPresenter(this);
        root.setCenter(view.getView());
    }


    private Stage getDashboardStage() {
        return (Stage) root.getScene().getWindow();
    }

    public Window getDashboardWindow() {
        return root.getScene().getWindow();
    }
}
