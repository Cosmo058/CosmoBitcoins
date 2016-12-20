package com.cosmo.cosmobitcoins;

import android.os.StrictMode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "CosmoBitcoins";

    double btc_available = 0;
    double mxn_available = 0;
    double last_btc_price = 0;
    double mxn_to_earn = 0;
    double btc_to_earn = 0;
    double fee =0 ;
    double ask = 0;
    double bid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //TODO DEGUB imlementar las operaciones de red en un hilo separado del main
        StrictMode.setThreadPolicy(policy);                                                         //TODO DEGUB imlementar las operaciones de red en un hilo separado del main

        HttpBitso http = new HttpBitso();

        try {
            JSONObject j = http.sendGet();
            Log.d(TAG, "Last BTC price: " + j.getString("last"));
            Log.d(TAG,"Ask (precio de compra): " + j.getString("ask"));
            Log.d(TAG,"Bid (precio de venta) : " + j.getString("bid"));
            last_btc_price = Double.parseDouble(j.getString("last"));
            ask = Double.parseDouble(j.getString("ask"));
            bid = Double.parseDouble(j.getString("bid"));
            Log.d(TAG,"Bid-Ask: "+Double.toString(bid-ask));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("\nTesting 2 - Send Http POST request");
        try {
            JSONObject j = http.sendPost("balance");
            Log.d(TAG,"BTC available: "+j.getString("btc_available"));

            btc_available = Double.parseDouble(j.getString("btc_available"));
            mxn_available = Double.parseDouble(j.getString("mxn_available"));
            fee = Double.parseDouble(j.getString("fee"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        btc_to_earn = mxn_available/ask*(1L-(fee/100L));
        mxn_to_earn = btc_available*bid*(1L-(fee/100L));

        Log.d(TAG,"BTC if buy: " +btc_to_earn);
        Log.d(TAG,"MXN if sell: "+mxn_to_earn);

    }

    @Override
    public void onResume(){
        super.onResume();

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
