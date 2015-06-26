package com.mobiledev.bryan.currencyconvertermobileapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
/**
 * Created by Bryan on 5/3/2015.
 */
public class ExchangeRateWebLoader {
    private static ExchangeRateWebLoader instance;
    private static final String URL_TEMPLATE =
            "http://finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1&s=USD%s=X";
    private HashMap<String, Double> previouslyLoadedValues;
    private static final int WAIT_TIME = 1000;
    private static final String LOG_TAG = "ExchangeRateWebLoader";

    private ExchangeRateWebLoader() {
        previouslyLoadedValues = new HashMap<>();
    }

    public static synchronized ExchangeRateWebLoader getInstance() {
        if (instance == null)
            instance = new ExchangeRateWebLoader();

        return instance;
    }

    public double loadExchangeRateFor(String currencyCode) {
        double exchangeRate = 0.0;

        if (previouslyLoadedValues.containsKey(currencyCode))
            return previouslyLoadedValues.get(currencyCode);


        try {
            URL webApiUrl = new URL(String.format(URL_TEMPLATE, currencyCode));
            URLConnection con = webApiUrl.openConnection();
            con.setConnectTimeout(WAIT_TIME);
            con.setReadTimeout(WAIT_TIME);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String rawInput = rawInput = reader.readLine();

            String[] parts = rawInput.split(",");

            if (parts.length >= 2) {
                exchangeRate = Double.parseDouble(parts[1]);
            } else {
                Log.w(LOG_TAG, LOG_TAG + "Log: Currency " + currencyCode + " failed to "
                        + "download properly. Ensure an internet connection is available and that "
                        + " the currency code is valid");
                exchangeRate = -1;
            }

            reader.close();
        } catch (SocketTimeoutException ste) {
            exchangeRate = -1;
            Log.w(LOG_TAG, LOG_TAG + "Log: " + "Connection timeout while downloading exchange rate for "
                + currencyCode + ".");
        } catch (Exception e) {
            exchangeRate = -1;
            Log.e(LOG_TAG, LOG_TAG + "Log: " + e.getMessage() + "\n" + e.getStackTrace(), e);
        }

        if (exchangeRate > 0)
            previouslyLoadedValues.put(currencyCode, exchangeRate);


        return exchangeRate;
    }
}
