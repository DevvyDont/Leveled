package io.github.devvydoo.levelingoverhaul.util;

import java.text.NumberFormat;

public class FormattingHelpers {

    public static String getFormattedInteger(int num){
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setGroupingUsed(true);
        return nf.format(num);
    }

}
