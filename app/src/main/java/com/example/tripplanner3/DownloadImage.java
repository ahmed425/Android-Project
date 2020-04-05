package com.example.tripplanner3;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class DownloadImage extends AsyncTask<String, Void, Integer> {

    String webLink;
    Context cont;
    Trip trip;
    String url;
    SharedPreferences saving;
    SharedPreferences.Editor edit;

    public DownloadImage(String webLink, Context cont, Trip trip) {
        this.webLink = webLink;
        this.cont = cont;
        this.trip = trip;
        saving = cont.getSharedPreferences("imgurl", 0);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        Log.i("doinback", "2na Guwa il Asymc Task !!!!");
        URL url;
        HttpURLConnection httpURLConnection;
        String point = null;
        String inputString;
        try {
            Log.i("7parseUrl", "7nbd2 ngeeb il JSON YA RAB !!!!");
            url = new URL(webLink);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            Log.i("parseUrl", "7ngeeb il JSON mn il URL !!!!");
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
                Log.i("json", inputString + "bgeeb il Json");
            }
            Log.i("afterJsonParsing", builder.toString());
            String arrayString = builder.toString();
            JSONObject jsonObject = new JSONObject(arrayString);
            JSONArray array = jsonObject.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONObject overview_polyline = routes.getJSONObject("overview_polyline");
            point = overview_polyline.getString("points");
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
            JSONArray array2 = jsonObject.getJSONArray("routes");
            JSONObject tripDist = array2.getJSONObject(0);
            JSONArray legs = tripDist.getJSONArray("legs");
            JSONObject dirs = legs.getJSONObject(0);
            JSONObject directions = dirs.getJSONObject("distance");
            String distance = directions.getString("text");
            Log.i("il-distance", distance);
            //////////
            JSONArray array3 = jsonObject.getJSONArray("routes");
            JSONObject tripTime = array3.getJSONObject(0);
            JSONArray legs2 = tripTime.getJSONArray("legs");
            JSONObject timeid = legs2.getJSONObject(0);
            JSONObject times = timeid.getJSONObject("duration");
            String time = times.getString("text");
            Log.i("il-time", time);
            Double hours = 0.0;
            Double mins = 0.0;
            String[] hourstr = time.split(" ");
            if (time.contains("hours")) {
                hours = Double.parseDouble(hourstr[0]);
//                Log.i("il-hours",hours+"");
//                Log.i("il-hours",time.indexOf(" hour")+"hours");
//                Log.i("il-hours",time.indexOf(" min")+"mins");
                mins = Double.parseDouble(hourstr[2]);
                hours = hours + (mins / 60);
                Log.i("il-hours", mins + "");
                Log.i("il-hours", hours + "");
            } else {
                mins = Double.parseDouble(hourstr[0]);
                hours = hours + (mins / 60);
                Log.i("il-hours", mins + "");
                Log.i("il-hours", hours + "");
            }

            Log.i("il-hghjg", hours + "  ");
            distance = distance.split(" ")[0];
            Double speed = Double.parseDouble(distance) / hours;
            String spd = hours.toString().substring(0, 3);
            String distvel = "Duration = " + time + " Velocity = " + speed + "Km/h";
            Log.i("il-hghjg", hours + "  " + speed);
            //String distvel = "Duration = "+15+" Velocity = "+55+"Km/h";
//            SQLAdapter adapter = new SQLAdapter(cont);
//            long i2 = adapter.updateTripdist(trip.getId(),distvel);
//            Log.i("elupdateelgdeed",i2+" "+distvel);


            /////////////////////////////////////////////////////////////////////////////////////////////////////////
            Bitmap img = null;
            HttpURLConnection connection;
            InputStream is;
            String urlstr = "https://maps.googleapis.com/maps/api/staticmap?size=600x300&center=" + trip.getSlat() +
                    "," + trip.getSlong() + "|" + trip.getElat() + "," + trip.getElong() +
                    "&path=fillcolor:red7Ccolor:red%7Cenc:" + point;
            URL urlimg = new URL(urlstr + "&key=AIzaSyBPO5WzFBdWKUZw24YvKZ0jpd23ciwMawQ");
            Log.i("overview_polyline", overview_polyline.toString());
            Log.i("try", urlimg + "");
            connection = (HttpURLConnection) urlimg.openConnection();
            is = connection.getInputStream();
            Log.i("try", "0 fkdkfjsdfsdfdsfdsfsdfdsf0000000000000000000");
            img = BitmapFactory.decodeStream(is);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    , "imgetr" + trip.getId());
            FileOutputStream out = new FileOutputStream(file);
            Log.i("try", "222222222222222222222222222222222222222");
            img.compress(Bitmap.CompressFormat.PNG, 90, out);
            Log.i("try", "33333333333333333333333333333333333333");
            out.flush();
            Log.i("try", "4444444444444444444444444444444444444444");

            Log.i("urlstr", urlstr);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer s) {
        super.onPostExecute(s);
/*
        if(s!= null) {
            Toast.makeText(cont, s+"2y 7agga", Toast.LENGTH_LONG).show();
//            Picasso.with(cont)
//                    .load("https://maps.googleapis.com/maps/api/staticmap?size=400x300&center="+url.getStart_lat()+
//                            ","+url.getStart_long()+"|"+url.getEnd_lat()+","+url.getEnd_long()+
//                            "&zoom=11&path=fillcolor:red7Ccolor:red%7Cenc:"+s+"&key=AIzaSyCw_w2LB0TTREWDbUotVkgzHNjDGHTSqHk");

        }else {
            Toast.makeText(cont," il S fady", Toast.LENGTH_LONG).show();
        }*/

    }
}