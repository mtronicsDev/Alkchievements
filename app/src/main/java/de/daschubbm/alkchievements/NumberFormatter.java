package de.daschubbm.alkchievements;

import java.math.BigDecimal;

/**
 * Created by Maxi on 30.09.2016.
 */
class NumberFormatter {
    static String formatPrice(float price) {
        BigDecimal num = new BigDecimal(price);
        num = num.setScale(2, BigDecimal.ROUND_HALF_UP);
        return num.toString();
    }

    static String formatPrice(String price) {
        BigDecimal num = new BigDecimal(price);
        num = num.setScale(2, BigDecimal.ROUND_HALF_UP);
        return num.toString();
    }
}
