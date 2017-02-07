package com.seunindustries.finddrive;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Created by P12278 on 2016-09-27.
 */
public class ServletRawDataAsyncTask extends AsyncTask<ArrayList<RawData>, Void, String> {

    Context context;
    Gson gson = new Gson();

    ServletRawDataAsyncTask(Activity activity){
        context = activity;
    }

    @Override
    protected String doInBackground(ArrayList<RawData>... params) {

        String result = null;
        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL("https://drivelist-144601.appspot.com/myservlet");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            Type type = new TypeToken<List<RawData>>(){}.getType();
            Log.d("ServletRawDataAsyncTask", "params :" + params);

            ArrayList<RawData> arrData = new ArrayList<RawData>();

            String job = gson.toJson(params, type);

            OutputStream os = urlConnection.getOutputStream();
            os.write(job.getBytes());
            os.flush();

            Log.d("ServletRawDataAsyncTask", "ServletRawDataAsyncTask ");

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuffer jb = new StringBuffer();
                String line = null;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = reader.readLine()) != null)
                        jb.append(line);
                } catch (Exception e) { /*report an error*/ }

                result = jb.toString();

            } else {
                result = "Error + : " + responseCode;
            }

        } catch (MalformedURLException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        } finally {
            urlConnection.disconnect();
        }

        return result;
    }


    @Override
    protected void onPostExecute(String result) {

        if(result.isEmpty()){
            Toast.makeText(context, "received null from server", Toast.LENGTH_LONG).show();
        }else{
            if (result.equals("PUT")) {
                Toast.makeText(context, R.string.updated, Toast.LENGTH_SHORT).show();
                ((Activity)context).finish();

            }
            //....
        }
    }
}
