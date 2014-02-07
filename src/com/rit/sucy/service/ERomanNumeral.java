package com.rit.sucy.service;

import org.apache.commons.lang.Validate;

/**
 * Handles conversions between Roman Numeral Strings and integers
 */
public class ERomanNumeral {

    private enum RomanNumber
    {
        //It is important that the order is in reverse
        M (1000), D (500), C (100), L (50), X (10), V (5), I (1);

        private final int valueInDec;

        /**
         * Maps the RomanNumbers to decimal equivalents
         *
         * @param decimal value which represents the roman number
         */
        private RomanNumber(int decimal)
        {
            this.valueInDec = decimal;
        }

        /**
         * Return the decimal representation
         *
         * @return decimal of the roman number
         */
        public int getInDecimal ()
        {
            return valueInDec;
        }
    }

    /**
     * Gets the Roman Numeral string representing the given value
     *
     * @param value value to be converted
     * @return      Roman Numeral String
     */
    public static String numeralOf(int value) {
        Validate.isTrue(value > 0, "Roman numbers can't express zero or negative numbers!");

        StringBuilder builder = new StringBuilder();
        RomanNumber[] romanNumbers = RomanNumber.values();

        for (int i = 0; i < romanNumbers.length; i++){
            RomanNumber romanNumber = romanNumbers[i];

            // Regular values
            while (value >= romanNumber.getInDecimal()) {
                value -= romanNumber.getInDecimal();
                builder.append(romanNumber.name());
            }

            // Subtraction values
            if (i < romanNumbers.length - 1) {
                int index = i - i % 2 + 2;
                RomanNumber subtractNum = romanNumbers[index];

                if (value >= romanNumber.getInDecimal() - subtractNum.getInDecimal()) {
                    value -= romanNumber.getInDecimal() - subtractNum.getInDecimal();
                    builder.append(subtractNum.name());
                    builder.append(romanNumber.name());
                }
            }
        }
        return builder.toString();
    }

    /**
     * Parses a Roman Numeral string into an integer
     *
     * @param romanNumeral Roman Numeral string to parse
     * @return             integer value (0 if invalid string)
     */
    public static int getValueOf(String romanNumeral) {
        char[] numerals = romanNumeral.toCharArray();
        int total = 0;

        for (int i = 0; i < numerals.length; i++) {
            int value = getNumeralValue(numerals[i]);
            if (i < numerals.length - 1) {
                if (getNumeralValue(numerals[i + 1]) > value) value = -value;
            }
            if (value == 0) return 0;
            total += value;
        }
        return total;
    }

    /**
     * Gets the value of the given Roman Numeral character
     *
     * @param numeral Roman Numeral character
     * @return        value of the character
     */
    public static int getNumeralValue(char numeral) {
        String romanNumeral = ("" + numeral).toUpperCase();
        try {
            RomanNumber romanNumber = RomanNumber.valueOf(romanNumeral);
            return romanNumber.getInDecimal();
        }
        catch (IllegalArgumentException e) {
            return 0;
        }
    }
}
