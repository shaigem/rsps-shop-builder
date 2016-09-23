package org.bitbucket.shaigem.rssb.fx.control;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * @author http://stackoverflow.com/users/1155209/jewelsea
 */
public class Fraction extends VBox {

    private Text numeratorText;
    private Text denominatorText;
    private Line divider;

    private double offset;


    public Fraction(int numerator, int denominator) {
        init(numerator + "", denominator + "");
    }

    private void init(String numerator, String denominator) {
        setAlignment(Pos.CENTER);
        numeratorText = new Text(numerator);
        denominatorText = new Text(denominator);
        offset = numeratorText.getBaselineOffset() * 1.5;
        double dividerWidth =
                Math.max(
                        numeratorText.getLayoutBounds().getWidth(),
                        denominatorText.getLayoutBounds().getWidth()
                ) + 6;
        divider = new Line(0, 1, dividerWidth, 1);
        divider.setStroke(Color.valueOf("#f7edb7"));
        getChildren().addAll(
                numeratorText,
                divider,
                denominatorText
        );
    }

    public void setNumeratorText(String text) {
        numeratorText.setText(text);
    }

    public void setDenominatorText(String text) {
        denominatorText.setText(text);
    }

    public Text getNumeratorText() {
        return numeratorText;
    }

    public Text getDenominatorText() {
        return denominatorText;
    }

    public Line getDivider() {
        return divider;
    }

    public double getBaselineOffset() {
        return offset;
    }
}