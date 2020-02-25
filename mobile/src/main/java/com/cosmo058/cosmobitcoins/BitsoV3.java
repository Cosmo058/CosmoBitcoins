package com.cosmo058.cosmobitcoins;

/* Created by Cosmo058 on 27/12/17. */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class BitsoV3 {
    private final String USER_AGENT = "Bitso API from Java";
    private final int client = Integer.parseInt( System.getenv("BITSO_CLIENT"));
    private final String key = System.getenv("BITSO_API_KEY");
    private final String secret = System.getenv("BITSO_SECRET");

    // HTTP GET request
    public JSONObject sendGet() throws Exception {
        String url = "https://api.bitso.com/v2/ticker?book=btc_mxn";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent",USER_AGENT);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        return new JSONObject(response.toString());
    }



    // HTTP POST request
    public JSONObject sendPost(String action) throws Exception { //fee from balance in percent 1 = 1%
        JSONObject J_obj = new JSONObject();
        long nonce = System.currentTimeMillis()/1000;
        String signature_seed = Long.toString(nonce)+Integer.toString(client)+key;
        String signature = HMAC.hmacDigest(signature_seed,secret,"HmacSHA256");
        String url ="";

        J_obj.put("key",key);
        J_obj.put("nonce",nonce);
        J_obj.put("signature",signature);

        if(!action.equals(""))
            url = "https://api.bitso.com/v3/" + action;

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.addRequestProperty("User-Agent",USER_AGENT);

        String urlParameters = J_obj.toString();

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new JSONObject(response.toString());
    }
}

