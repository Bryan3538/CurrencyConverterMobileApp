/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobiledev.bryan.currencyconverter;

/**
 *
 * @author Bryan
 */
public enum CurrencyConverterConstants {

    UNITED_STATES_DOLLAR("US Dollar", "$", "USD", 1.0),
    EU_EURO("Euro", "€", "EUR", 0.9106),
    CANADIAN_DOLLAR("Canadian Dollar", "$", "CAD", 1.2158),
    BRITISH_POUND("British Pound", "£", "GBP", .6606),
    AUSTRALIAN_DOLLAR("Australian Dollar", "$", "AUD", 1.2737),
    JAPANESE_YEN("Japanese Yen", "¥", "JPY", 120.205),
    CHINESE_YUAN("Chinese Yuan", "¥", "CNY", 6.2063),
    MEXICAN_PESO("Mexican Peso", "$", "MXN", 15.1205),
    INDIAN_RUPEE("Indian Rupee", "₹", "INR", 63.7114);
    
    private String currencyName, currencySymbol, currencyCode;
    private double defaultExchangeRate;

    private CurrencyConverterConstants(String currencyName,
            String currencySymbol, String currencyCode, double defaultExchangeRate) {
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
        this.currencyCode = currencyCode;
        this.defaultExchangeRate = defaultExchangeRate;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }

    public double getDefaultExchangeRate() {
        return defaultExchangeRate;
    }
}
