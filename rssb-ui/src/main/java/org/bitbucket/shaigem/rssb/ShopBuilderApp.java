package org.bitbucket.shaigem.rssb;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bitbucket.shaigem.rssb.plugin.RSSBPluginManager;
import org.bitbucket.shaigem.rssb.store.ItemImageStore;
import org.bitbucket.shaigem.rssb.store.ItemNameStore;
import org.bitbucket.shaigem.rssb.ui.dashboard.DashboardView;

/**
 * Created on 2015-08-10.
 */
public class ShopBuilderApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Stage primaryStage = new Stage();
        loadResources();
        DashboardView dashboardView = new DashboardView();
        Scene scene = new Scene(dashboardView.getView());
       /* BuilderWindowView mainWindowView = new BuilderWindowView();
        Scene scene = new Scene(mainWindowView.getView());
        scene.getStylesheets().addAll
                (this.getClass().getClassLoader().getResource("css/builder_style.css").toExternalForm());
       */
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(713);
        primaryStage.setMinHeight(467);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.setTitle("Shop Builder Dashboard by AbyssPartyy");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
        RSSBPluginManager.INSTANCE.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void loadResources() {
        RSSBPluginManager.INSTANCE.initializeShopFormatPlugins();
        try {
            ItemNameStore.parseItemNames();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ItemImageStore.setupStoreArchiveDetecter();

    }
}
