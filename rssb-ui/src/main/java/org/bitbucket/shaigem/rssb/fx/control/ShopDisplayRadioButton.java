package org.bitbucket.shaigem.rssb.fx.control;

import com.sun.javafx.css.StyleConverterImpl;
import javafx.css.*;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2016-03-14.
 */
public class ShopDisplayRadioButton extends RadioButton {

    public ShopDisplayRadioButton(DisplayMode mode) {
        super();
        this.getStyleClass().setAll("shop-display-radio-button");
        setDisplayMode(mode);

    }

    public enum DisplayMode {ICON, EXPANDED}

    @Override
    public String getUserAgentStylesheet() {
        return ShopDisplayRadioButton.class.getResource("display_radio_button.css").toExternalForm();
    }
  //  @Override
   // protected Skin<?> createDefaultSkin()	{
   //    return new ShopDisplayRadioButtonSkin(this);
 //   }


    /***************************************************************************
     * *
     * Stylesheet Handling                                                     *
     * *
     **************************************************************************/

    private StyleableObjectProperty<DisplayMode> displayModeType = new SimpleStyleableObjectProperty<>(StyleableProperties.BUTTON_TYPE, ShopDisplayRadioButton.this, "displayModeType", DisplayMode.ICON);

    public DisplayMode getDisplayMode() {
        return displayModeType == null ? DisplayMode.ICON : displayModeType.get();
    }

    public StyleableObjectProperty<DisplayMode> displayModeTypeProperty() {
        return this.displayModeType;
    }

    public void setDisplayMode(DisplayMode type) {
        this.displayModeType.set(type);
    }


    private static class StyleableProperties {
        private static final CssMetaData<ShopDisplayRadioButton, DisplayMode> BUTTON_TYPE =
                new CssMetaData<ShopDisplayRadioButton, DisplayMode>("-fx-display-type",
                        DisplayModeConverter.getInstance(), DisplayMode.ICON) {
                    @Override
                    public boolean isSettable(ShopDisplayRadioButton control) {
                        return control.displayModeType == null || !control.displayModeType.isBound();
                    }

                    @Override
                    public StyleableProperty<DisplayMode> getStyleableProperty(ShopDisplayRadioButton control) {
                        return control.displayModeTypeProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                    BUTTON_TYPE
            );
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    // inherit the styleable properties from parent
    private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        if (STYLEABLES == null) {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            styleables.addAll(getClassCssMetaData());
            styleables.addAll(Labeled.getClassCssMetaData());
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        return STYLEABLES;
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }

    public static class DisplayModeConverter extends StyleConverterImpl<String, DisplayMode> {
        // lazy, thread-safe instatiation
        private static class Holder {
            static final DisplayModeConverter INSTANCE = new DisplayModeConverter();
        }

        public static StyleConverter<String, DisplayMode> getInstance() {
            return Holder.INSTANCE;
        }

        private DisplayModeConverter() {
            super();
        }

        @Override
        public DisplayMode convert(ParsedValue<String, DisplayMode> value, Font not_used) {
            String string = value.getValue();
            try {
                return DisplayMode.valueOf(string);
            } catch (IllegalArgumentException | NullPointerException exception) {
                return DisplayMode.ICON;
            }
        }

        @Override
        public String toString() {
            return "DisplayModeConverter";
        }
    }
}
