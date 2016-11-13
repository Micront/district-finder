package com.main.getOpenData;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataGovSpb {
    public static void main(String[] args) {
        DataGovSpb dataGovSpb = new DataGovSpb();
        String result = dataGovSpb.getData();
        System.out.println(result);
    }

    public String getData() {
        String HEADER_SECURITY_TOKEN = "adabe8bdb5e423e23c4a3a576a01dcf22b87ba0d";
        StringBuilder result = new StringBuilder();
        String queryUrl = "http://data.gov.spb.ru/api/v1/datasets/18/versions/latest/data";


        HttpURLConnection conn;

//      get data with java.net
        try {
            URL url = new URL(queryUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Authorization","Token " + HEADER_SECURITY_TOKEN);
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //      get data with lib org.apache
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(queryUrl);
        request.addHeader("Authorization","Token " + HEADER_SECURITY_TOKEN);

        try {
            HttpResponse response = client.execute(request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                            response.getEntity().getContent()));

            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
