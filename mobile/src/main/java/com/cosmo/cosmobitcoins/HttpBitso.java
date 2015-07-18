package com.cosmo.cosmobitcoins;

/**
 * Created by Angel on 18/07/2015.
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class HttpBitso {
    private final String USER_AGENT = "Bitso API from Java";
    private final int client = 21328;
    private final String key ="nrIJgvSMlA";
    private final String secret = "ca0e6ff47644eebed43f9863a56ecb79";

    // HTTP GET request
    public JSONObject sendGet() throws Exception {
        String url = "https://api.bitso.com/v2/ticker?book=btc_mxn";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent",USER_AGENT);

        /*int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);*/

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        //System.out.println(response.toString());

        JSONObject jsonObj = new JSONObject(response.toString());

        return jsonObj;

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
            url = "https://api.bitso.com/v2/" + action;

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

        /*int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);*/

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        //System.out.println(response.toString());

        JSONObject jsonObj = new JSONObject(response.toString());

        return jsonObj;
    }
}
