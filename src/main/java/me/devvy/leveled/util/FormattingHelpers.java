package me.devvy.leveled.util;

import java.text.NumberFormat;

public class FormattingHelpers {

    public static String getFormattedInteger(int num){
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setGroupingUsed(true);
        return nf.format(num);
    }

    // Given a string, capitalize the first letter only
    public static String capitalize(String old) {

        // Empty string just give it back
        if (old.isEmpty())
            return old;

        // Get the first char but uppercase
        String firstChar = String.valueOf(old.charAt(0)).toUpperCase();
        // If there's extra letters append them
        if (old.length() > 1)
            return firstChar + old.substring(1).toLowerCase();

        // There's only one character just return it
        return firstChar;
    }

    // Given a string, capitalize the first letter of every word
    public static String capitalizeFully(String old) {

        // Split up the string by spaces into an array of words
        String[] splitBySpace = old.toLowerCase().split(" ");

        // Loop through all the words and extract the first letter, capitalize it, and append rest of word
        for (int i = 0; i < splitBySpace.length; i++)
            splitBySpace[i] = capitalize(splitBySpace[i]);

        return String.join(" ", splitBySpace);
    }

}
