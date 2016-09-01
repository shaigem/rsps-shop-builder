package org.bitbucket.shaigem.rssb.util;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Created on 2015-09-05.
 */
public final class ItemAmountUtil {

    private static final Paint YELLOW = Color.YELLOW;
    private static final Paint WHITE = Color.WHITE;
    private static final Paint GREEN = Paint.valueOf("0x00FF80");

    private static final int K_MODIFIER = 1000;
    private static final int M_MODIFIER = 1000000;

    public static Paint getPaintForAmount(int amount) {
        if (amount >= 1 && amount < 99999) // yellow
            return YELLOW;
        else if (amount >= 100000 && amount < 9999999) // white
            return WHITE;
        else if (amount >= 10000000)// green
            return GREEN;
        return YELLOW;
    }

    public static String getFormattedAmount(int amount) {
        if (amount >= 0 && amount < 99999)
            return String.valueOf(amount);
        else if (amount >= 100000 && amount < 9999999)
            return amount / K_MODIFIER + "K";
        else if (amount >= 10000000)
            return amount / M_MODIFIER + "M";
        return String.valueOf(amount);
    }

    public static int getUnformattedAmount(String amountString) {
        amountString = amountString.toLowerCase();
        try {
            if (amountString.contains("k") || amountString.contains("m")) {
                String amountNoSuffix = amountString.substring(0, amountString.length() - 1);
                int amount = amountNoSuffix.isEmpty() ? 1 : Integer.parseInt(amountNoSuffix);
                if (amountString.contains("k"))
                    return amount * K_MODIFIER;
                else if (amountString.contains("m")) {
                    return amount * M_MODIFIER;
                }
            }
            return Integer.parseInt(amountString);
        } catch (NumberFormatException exception) {
            return Integer.MAX_VALUE;
        }
    }
}
