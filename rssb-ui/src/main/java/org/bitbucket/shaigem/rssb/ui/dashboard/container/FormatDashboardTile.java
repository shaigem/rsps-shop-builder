package org.bitbucket.shaigem.rssb.ui.dashboard.container;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.bitbucket.shaigem.rssb.event.SetActiveFormatPluginRequest;
import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;
import org.bitbucket.shaigem.rssb.plugin.ShopFormat;
import org.sejda.eventstudio.DefaultEventStudio;

/**
 * Created on 03/09/16.
 */
public class FormatDashboardTile extends AnchorPane {
    // use a AnchorPane instead of region. This fixed a bug with the scrollpane not
    // updating its scrollbar when adding this to a node that is wrapped in a scrollpane
    // ScrollPane -> FlowPane -> FormatDashboardTile (AnchorPane extended) = scroll pane resize bar properly

    private static final PseudoClass ARMED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("armed");
    private static final PseudoClass ACTIVE_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("active");

    private static final String DEFAULT_STYLE_CLASS = "format-tile";
    private static final String NAME_LABEL_STYLE_CLASS = DEFAULT_STYLE_CLASS + "-name";
    private static final String VERSION_LABEL_STYLE_CLASS = DEFAULT_STYLE_CLASS + "-version";
    private static final String AUTHOR_LABEL_STYLE_CLASS = DEFAULT_STYLE_CLASS + "-author";
    private static final String DESCRIPTION_LABEL_STYLE_CLASS = DEFAULT_STYLE_CLASS + "-description";
    private static final String BUTTON_STYLE_CLASS = DEFAULT_STYLE_CLASS + "-button";
    private static final String INNER_BOX_STYLE_CLASS = DEFAULT_STYLE_CLASS + "-inner-box";

    private BaseShopFormatPlugin formatPlugin;

    FormatDashboardTile(DefaultEventStudio eventStudio, BaseShopFormatPlugin shopFormatPlugin) {
        this.formatPlugin = shopFormatPlugin;
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        VBox formatInformationBox = createFormatInformationBox(shopFormatPlugin);
        // create button (will be hidden)
        Button button = new Button();
        button.getStyleClass().add(BUTTON_STYLE_CLASS);
        armed.bind(button.armedProperty());
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setOnAction(event -> eventStudio.broadcast
                (new SetActiveFormatPluginRequest(this, shopFormatPlugin)));

        final VBox innerBox = new VBox();
        final StackPane stackPane = new StackPane(formatInformationBox, button);

        innerBox.getStyleClass().add(INNER_BOX_STYLE_CLASS);
        innerBox.getChildren().add(stackPane);
        prefHeightProperty().bind(innerBox.heightProperty());
        getChildren().add(innerBox);
    }

    private VBox createFormatInformationBox(BaseShopFormatPlugin shopFormatPlugin) {
        final ShopFormat shopFormat = shopFormatPlugin.getFormat();
        Label nameLabel = new Label(shopFormat.descriptor().getName());
        nameLabel.getStyleClass().add(NAME_LABEL_STYLE_CLASS);

        Label versionLabel = new Label(shopFormatPlugin.getVersion());
        versionLabel.getStyleClass().add(VERSION_LABEL_STYLE_CLASS);

        Label authorLabel = new Label(shopFormatPlugin.getAuthor());
        authorLabel.getStyleClass().add(AUTHOR_LABEL_STYLE_CLASS);

        Label descriptionLabel = new Label(shopFormat.descriptor().getDescription());
        descriptionLabel.getStyleClass().add(DESCRIPTION_LABEL_STYLE_CLASS);
        descriptionLabel.setMinHeight(USE_PREF_SIZE);

        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.getChildren().addAll(nameLabel, versionLabel, authorLabel, descriptionLabel);
        return vBox;
    }

    public BaseShopFormatPlugin getFormatPlugin() {
        return formatPlugin;
    }

    private ReadOnlyBooleanWrapper armed = new ReadOnlyBooleanWrapper(false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(ARMED_PSEUDOCLASS_STATE, get());
        }
    };

    public final ReadOnlyBooleanProperty armedProperty() {
        return armed.getReadOnlyProperty();
    }

    public final boolean isArmed() {
        return armed.get();
    }


    private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper() {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(ACTIVE_PSEUDOCLASS_STATE, get());
        }
    };

    public final ReadOnlyBooleanProperty activeProperty() {
        return active.getReadOnlyProperty();
    }

    final boolean isActive() {
        return active.get();
    }

    final void setActive(boolean active) {
        this.active.set(active);
    }
}
