package com.buahbatu.okarta;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.buahbatu.okarta.databinding.ActivityDetailBinding;
import com.buahbatu.okarta.helper.MarginHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "UsingThingSpeakAPI";
    private static final String THINGSPEAK_CHANNEL_ID = "331156";
    private static final String THINGSPEAK_API_KEY = "C5V2PY4BPOLWP5QS";
    private static final String THINGSPEAK_API_KEY_STRING = "api_key";

//    private final String UBIDOTS_API_KEY = "A1E-f4085747c44283505ba4d0c9195d2e72f1f6";
//    private final String UBIDOTS_TOKEN = "A1E-a0N0Tv2gXC1yJWggBGdFc5VRxaQgP5";

    private static final String Battery_Voltage = "field1";
    private static final String MAF_Flow_Rate = "field2";
    private static final String Distance = "field3";
    private static final String Sensor_Top = "field4";
    private static final String Sensor_Bottom = "field5";

    private static final String THINGSPEAK_UPDATE_URL = "https://api.thingspeak.com/update?";
    private static final String THINGSPEAK_CHANNEL_URL = "https://api.thingspeak.com/channels/";
    private static final String THINGSPEAK_FEEDS_LAST = "/feeds/last?";

    private static double value1;
    private static double value2;
    private static double value3;
    private static double value4;
    private static double value5;


    private ActivityDetailBinding detailBinding;

    public ObservableBoolean loadingVisibility;
    public ObservableField<String> readingStatus;
    public ObservableField<String> readingCondition;
    public ObservableField<String> readingCondition2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingVisibility = new ObservableBoolean(true);
        readingStatus = new ObservableField<>();
        readingCondition = new ObservableField<>();
        readingCondition2 = new ObservableField<>();

        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        detailBinding.setDetail(this);

        String sensorId = getIntent().getStringExtra("sensor_id");
        final String attribute = getIntent().getStringExtra("attribute");
//        loadDataFromUbidots(sensorId, attribute);

//        Toast.makeText(this, attribute, Toast.LENGTH_SHORT).show();
//        getData(attribute);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 20 seconds
                getData(attribute);
                handler.postDelayed(this, 20000);
            }
        }, 1000);
    }


    private void getData(final String attribute) {
        //Creating a string request

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getResources().getString(R.string.url_thingspeak),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String status = "";
                        try {
                            JSONObject res = new JSONObject(response);
//                            double read = Double.parseDouble(response);

//                            tambah notifikasi untuk kampas, filter, dan aki. notifikasi akan keluar ketika kampas rem, filter dan aki dalam keadaan BAD
//                             untuk service, akan keluar notifikasi, jika distance diangka 5000, 10000, 15000 dst (sesuai dengan buku service resmi) untuk melakukan service rutin secara berkala
                            switch (attribute) {
                                case "kampas":
                                    status = MarginHelper.getBreakShoeStatus(res.getDouble(Sensor_Top));
//                                    readingCondition.set(MarginHelper.getStatusMessage(DetailActivity.this,
//                                                    MarginHelper.getTranslated(attribute), status));

//                                    contoh notifikasi tapi belum jalan
                                    if (status.equals("BAD")) {
                                        addNotification("Kampas bermasalah", "kampas bermasalah kampas bermasalahkampas bermasalah", DetailActivity.this.getApplicationContext());
                                    }


                                    break;
                                case "filter":
                                    status = MarginHelper.getFilterStatus(res.getDouble(MAF_Flow_Rate));
                                    readingCondition.set(MarginHelper.getStatusMessage(DetailActivity.this,
                                            MarginHelper.getTranslated(attribute), status));
                                    if (status.equals("BAD")) {
                                        addNotification("Filter bermasalah", "Filter bermasalah Filter bermasalah Filter bermasalah", DetailActivity.this.getApplicationContext());
                                    }
                                    break;
                                case "aki":
                                    status = MarginHelper.getAccuStatus(res.getDouble(Battery_Voltage));
                                    readingCondition.set(MarginHelper.getStatusMessage(DetailActivity.this,
                                            MarginHelper.getTranslated(attribute), status));
                                    break;
                                case "service":
                                    status = MarginHelper.getServiceStatus(res.getDouble(Distance));
                                    readingCondition2.set(MarginHelper.getDetailStatus(DetailActivity.this,
                                            MarginHelper.getTranslated(attribute), status));
                                    break;
                            }

                            loadingVisibility.set(false);
                            readingStatus.set(status);


                        } catch (Exception e) {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Gagal menerima data" + e.getMessage(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        error.printStackTrace();
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Gagal menerima data", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {


                    return params;
                } catch (Exception e) {
                    e.getMessage();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "errorParam: \n" + e.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.show();
//                    dialog.dismiss();
                }
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

//    private void addNotification(String mainMessage, String desc) {
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
//
//        NotificationCompat noti = new NotificationCompat.Builder(this)
//                .setContentTitle("ATTENTION!!")
//                .setContentText("Telah terjadi penurunan fungsi. silahkan cek aplikasi")
//                .setSmallIcon(R.mipmap.ic_car_2)
//                .setContentIntent(pIntent)
//                .addAction(R.mipmap.ic_launcher, "call", pIntent).addAction(R.mipmap.ic_launcher, "more", pIntent)
//                .addAction(R.mipmap.ic_launcher, "and more", pIntent).build();
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        noti.flags |= Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(0, noti);
//    }

    private void addNotification(String title, String text, Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_car)
                .setTicker("Hearty365")
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }
}





