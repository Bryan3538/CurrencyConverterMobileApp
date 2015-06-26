package com.mobiledev.bryan.currencyconvertermobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiledev.bryan.currencyconverter.*;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.transform.Result;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private Button convertButton;
    private EditText fromAmountEditText;
    private CurrencyConverter fromCurrencyConverter;
    private CurrencyConverter toCurrencyConverter;
    private ArrayList<CurrencyConverter> currencyConverters;
    private TextView toAmountTextView;
    private boolean loadingFromWeb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter<String> fromCurrencyAdapter = null;
        ArrayAdapter<String> toCurrencyAdapter = null;

        currencyConverters = new ArrayList<>(CurrencyConverterConstants.values().length);
        populateCurrencyConverters();

        fromCurrencySpinner = (Spinner) findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = (Spinner) findViewById(R.id.toCurrencySpinner);
        populateCurrencyAdapters(fromCurrencyAdapter, toCurrencyAdapter);

        fromCurrencySpinner.setOnItemSelectedListener(this);
        toCurrencySpinner.setOnItemSelectedListener(this);

        convertButton = (Button) findViewById(R.id.convertButton);
        convertButton.setOnClickListener(this);
        fromAmountEditText = (EditText) findViewById(R.id.fromAmountEditText);
        toAmountTextView = (TextView) findViewById(R.id.toAmountTextView);

        toAmountTextView.setText(currencyConverters.get(0).getCurrencySymbol() + "0.00");

        loadingFromWeb = false;

        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }

    private void populateCurrencyConverters() {
        for (CurrencyConverterConstants c : CurrencyConverterConstants.values())
            currencyConverters.add(new CurrencyConverter(c));
    }

    private void populateCurrencyAdapters(ArrayAdapter<String> fromCurrencyAdapter,
                                          ArrayAdapter<String> toCurrencyAdapter) {
        StringBuilder b = new StringBuilder();
        ArrayList<String> list = new ArrayList<>();

        for (CurrencyConverter c : currencyConverters) {
            b.setLength(0);
            b.append(c.getCurrencyName());
            b.append(" (");
            b.append(c.getCurrencySymbol());
            b.append(")");
            list.add(b.toString());
        }

        fromCurrencyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        fromCurrencySpinner.setAdapter(fromCurrencyAdapter);
        toCurrencyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        toCurrencySpinner.setAdapter(toCurrencyAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.exitItem:
                quit();
                return true;
            case R.id.updateExchangeRatesItem:
                if (!loadingFromWeb)
                    loadExchangeRatesFromWeb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadExchangeRatesFromWeb() {
        new ExchangeRateWebLoaderTask((ProgressBar) findViewById(R.id.progressBar)).execute();
        loadingFromWeb = true;
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }


    private void quit() {

        fromCurrencySpinner = null;
        toCurrencySpinner = null;
        convertButton = null;
        fromAmountEditText = null;
        fromCurrencyConverter = null;
        toCurrencyConverter = null;
        currencyConverters = null;
        toAmountTextView = null;
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.equals(fromCurrencySpinner)) {
            fromCurrencyConverter = currencyConverters.get(position);
        } else if (parent.equals(toCurrencySpinner)) {
            toCurrencyConverter = currencyConverters.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (parent.equals(fromCurrencySpinner)) {
            fromCurrencySpinner.setSelection(0);
        } else if (parent.equals(toCurrencySpinner)) {
            toCurrencySpinner.setSelection(0);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(convertButton)) {
            //hide the keyboard
            fromAmountEditText.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(fromAmountEditText.getWindowToken(), 0);

            //convert and display
            displayConvertedCurrencyAmount();
        }
    }

    private void displayConvertedCurrencyAmount() {

        if (fromAmountEditText.getText().length() > 0) {
            double fromAmount, toAmount;

            try {

                fromAmount = Double.parseDouble(fromAmountEditText.getText().toString());
            } catch (NumberFormatException nfe) {
                Toast.makeText(this, "You may only enter numeric values for currency!", Toast.LENGTH_SHORT).show();
                return;
            }
            toAmount = toCurrencyConverter.convertFromUsd(fromCurrencyConverter.convertToUsd(fromAmount));

            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            formatter.setMinimumIntegerDigits(1);
            String output = formatter.format(toAmount);
            toAmountTextView.setText(toCurrencyConverter.getCurrencySymbol() + " " + output);
        }
    }

    private class ExchangeRateWebLoaderTask extends AsyncTask<Void, Void, Void> {
        ProgressBar progress;
        int count;
        int total;
        boolean incompleteLoad;

        ExchangeRateWebLoaderTask(ProgressBar progress) {

            this.progress = progress;
            count = 0;
            total = currencyConverters.size();
            progress.setMax(total);
            progress.setProgress(0);
            incompleteLoad = false;
        }

        @Override
        protected Void doInBackground(Void... params) {

            ExchangeRateWebLoader loader = ExchangeRateWebLoader.getInstance();
            double exchangeRate = 0.0;

            for (CurrencyConverter c : currencyConverters) {
                exchangeRate = loader.loadExchangeRateFor(c.getCurrencyCode());

                if (exchangeRate > 0) {
                    c.setExchangeRate(exchangeRate);
                } else {
                    incompleteLoad = true;
                }

                count++;
                publishProgress();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {

            progress.setProgress(count);
        }

        @Override
        protected void onPostExecute(Void result) {
            displayConvertedCurrencyAmount();
            if (progress.getVisibility() == View.VISIBLE)
                progress.setVisibility(View.INVISIBLE);

            loadingFromWeb = false;

            String message = "Done updating exchange rates.";
            int toastLength = Toast.LENGTH_SHORT;

            if(incompleteLoad) {
                message += " Some exchange rates could not be loaded due to connection issues.";
                toastLength = Toast.LENGTH_LONG;
            }


            Toast.makeText(MainActivity.this, message, toastLength).show();
        }

        @Override
        protected void onCancelled() {
            if (progress.getVisibility() == View.VISIBLE)
                progress.setVisibility(View.INVISIBLE);

            loadingFromWeb = false;
            Toast.makeText(MainActivity.this, "Update of exchange rates cancelled.", Toast.LENGTH_SHORT).show();
        }
    }
}
