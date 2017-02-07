package com.seunindustries.finddrive;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by P12278 on 2016-09-26.
 */
public class CommonUtils {

    public static String SendData(ArrayList<RawData> arrList) {
        String result = null;
        HttpURLConnection urlConnection = null;
        Gson gson = new Gson();

        try {

            URL url = new URL("https://drivelist-144601.appspot.com/myservlet");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            Type type = new TypeToken<List<RawData>>(){}.getType();
            String job = gson.toJson(arrList, type);

            OutputStream os = urlConnection.getOutputStream();
            os.write(job.getBytes());
            os.flush();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuffer jb = new StringBuffer();
                String line = null;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = reader.readLine()) != null)
                        jb.append(line);
                } catch (Exception e) {
                    /*report an error*/
                }
                result = jb.toString();

            } else {
                result = "Error + : " + responseCode;
            }

        } catch (MalformedURLException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        } finally {
            Log.d("CommonUtils", "result : " + result);
            urlConnection.disconnect();
        }
        return result;
    }

    public static String MakeURL(RawData data) {
        //"kakaonavi://navigate?name=우리집&x=314931&y=544756&addr=경기도 용인시 수지구&rpoption=1&key=[api key]"
        String result = "kakaonavi://navigate?name=";

        GeoPoint in_pt = new GeoPoint(data.getLgt(), data.getLat());

        GeoPoint out_pt = GeoTrans.convert(GeoTrans.GEO, GeoTrans.KATEC, in_pt);
        Log.d("CommonUtils", "out_pt x: " + out_pt.getX());
        Log.d("CommonUtils", "out_pt y: " + out_pt.getY());
        Log.d("CommonUtils", "out_pt x: " + (int)out_pt.getX());
        Log.d("CommonUtils", "out_pt y: " + (int)out_pt.getY());

        result = result + data.getDestName()
                + "&x=" + (int)out_pt.getX()
                + "&y=" + (int)out_pt.getY()
                + "&addr=" + data.getAddr()
                + "&vehicle_type=1&rpoption=1&key=[b0274bd779bc4ef3b92f-3ea4c81ea83e]";

        Log.d("CommonUtils", "result : " + result);

        return result;
    }
}
