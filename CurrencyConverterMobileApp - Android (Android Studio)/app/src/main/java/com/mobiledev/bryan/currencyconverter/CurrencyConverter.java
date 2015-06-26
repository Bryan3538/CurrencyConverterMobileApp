/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobiledev.bryan.currencyconverter;

/**
 *
 * @author Bryan
 */
public class CurrencyConverter {

    private String currencyName, currencySymbol, currencyCode;
    private double exchangeRate;

    public CurrencyConverter(CurrencyConverterConstants c) {
        this.currencyName = c.getCurrencyName();
        this.currencySymbol = c.getCurrencySymbol();
        this.currencyCode = c.getCurrencyCode();
        this.exchangeRate = c.getDefaultExchangeRate();
    }

    public CurrencyConverter(String currencyName, String currencySymbol,
            String currencyCode, double exchangeRate) {

        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
        this.currencyCode = currencyCode;
        this.exchangeRate = exchangeRate;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    
    public double convertToUsd(double amountInThisCurrency) {
        return amountInThisCurrency * (1 / exchangeRate);
    }
    
    public double convertFromUsd(double amountInUsd) {
        return amountInUsd * exchangeRate;
    }
}
