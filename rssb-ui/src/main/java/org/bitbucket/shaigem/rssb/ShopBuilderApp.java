package org.bitbucket.shaigem.rssb;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bitbucket.shaigem.rssb.plugin.ShopPluginManager;
import org.bitbucket.shaigem.rssb.store.ItemImageStore;
import org.bitbucket.shaigem.rssb.store.ItemNameStore;
import org.bitbucket.shaigem.rssb.ui.BuilderWindowView;

import java.io.File;

/**
 * Created on 2015-08-10.
 */
public class ShopBuilderApp extends Application {

    private static ShopBuilderApp singleton;

    public static ShopBuilderApp getSingleton() {
        return singleton;
    }

    public ShopBuilderApp() {
        singleton = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadResources();
        BuilderWindowView mainWindowView = new BuilderWindowView();
        Scene scene = new Scene(mainWindowView.getView());
        scene.getStylesheets().addAll
                (this.getClass().getClassLoader().getResource("css/builder_style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Shop Builder 0.1");
        primaryStage.show();


    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
        ShopPluginManager.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }


    private void loadResources() {
        ShopPluginManager.initialize(ShopPluginManager.DEBUG ? new File("rssb-plugin-matrix/target/classes/").toURI(): new File("./plugins/").toURI());
        try {
            ItemNameStore.parseItemNames();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ItemImageStore.setupStoreArchiveDetecter();

    }


}
