package com.cosmo.cosmobitcoins;

import android.os.StrictMode;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "CosmoBitcoins";
    private SwipeRefreshLayout swipeContainer;


    double btc_available = 0;
    double mxn_available = 0;
    double last_btc_price = 0;
    double mxn_to_earn = 0;
    double btc_to_earn = 0;
    double fee =0 ;
    double ask = 0;
    double bid = 0;
    long timestamp = 0;

    TextView tv10,tv11,tv12,tv13,tv14,tv15,tv16,tv17,tv19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv10 = (TextView)findViewById(R.id.textView10);
        tv11 = (TextView)findViewById(R.id.textView11);
        tv12 = (TextView)findViewById(R.id.textView12);
        tv13 = (TextView)findViewById(R.id.textView13);
        tv14 = (TextView)findViewById(R.id.textView14);
        tv15 = (TextView)findViewById(R.id.textView15);
        tv16 = (TextView)findViewById(R.id.textView16);
        tv17 = (TextView)findViewById(R.id.textView17);
        tv17 = (TextView)findViewById(R.id.textView17);
        tv19 = (TextView)findViewById(R.id.textView19);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                System.out.println("Refreshed");
                getBalance();
                setBalance();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //TODO DEGUB imlementar las operaciones de red en un hilo separado del main
        StrictMode.setThreadPolicy(policy);                                                         //TODO DEGUB imlementar las operaciones de red en un hilo separado del main

        getBalance();
    }

    @Override
    public void onResume(){
        super.onResume();
        setBalance();
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

    public void getBalance(){
        HttpBitso http = new HttpBitso();

        try {
            JSONObject j = http.sendGet();
            Log.d(TAG, "Last BTC price: " + j.getString("last"));
            Log.d(TAG,"Ask (precio de compra): " + j.getString("ask"));
            Log.d(TAG,"Bid (precio de venta) : " + j.getString("bid"));
            timestamp = Long.parseLong(j.getString("timestamp"));
            last_btc_price = Double.parseDouble(j.getString("last"));
            ask = Double.parseDouble(j.getString("ask"));
            bid = Double.parseDouble(j.getString("bid"));
            Log.d(TAG,"Bid-Ask: "+Double.toString(bid-ask));

            j = http.sendPost("balance");
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

    public void setBalance(){
        tv10.setText(Double.toString(last_btc_price));
        tv11.setText(Double.toString(ask));
        tv12.setText(Double.toString(bid));
        tv13.setText(String.format("%.3f", bid - ask));
        tv14.setText(Double.toString(btc_available)+" BTC");
        tv15.setText(Double.toString(mxn_available)+" MXN");
        tv16.setText(String.format("%.6f", btc_to_earn)+" BTC");
        tv17.setText(String.format("%.3f",mxn_to_earn)+" MXN");
        tv19.setText(new SimpleDateFormat("d MMM yyyy 'at' hh:mm:ss a").format(new Date(timestamp*1000)));

        swipeContainer.setRefreshing(false); //Moverlo a un lugar mejor
    }
}
