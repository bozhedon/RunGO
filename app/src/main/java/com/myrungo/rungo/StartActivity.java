package com.myrungo.rungo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StartActivity extends AppCompatActivity implements LocationListener, GpsStatus.Listener, OnMapReadyCallback {
    private SharedPreferences sharedPreferences;
    private LocationManager mLocationManager;
    private Location startLocation;
    private static Data data;
    private Button start;
    private Button stop;
    private Button map_btn;
    private TextView currentSpeed;
    private TextView distance;
    private TextView result;
    private Chronometer time;
    private Data.onGpsServiceUpdate onGpsServiceUpdate;
    private boolean firstfix;
    private boolean first = true;
    private boolean map_active = false;
    private int senddata=0;
    private SupportMapFragment mapFragment;
    private String currentTime;
    private GoogleMap map;
    private CatView catView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        data = new Data(onGpsServiceUpdate);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        catView = findViewById(R.id.cat_view);
        catView.stop();
        start = findViewById(R.id.Start);
        stop = findViewById(R.id.Stop);
        map_btn = findViewById(R.id.map_btn);
        /*
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ChangeProgressEvent(senddata));
                Intent myIntent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(myIntent);
                dialog.cancel();
                dialog.dismiss();
            }
        });*/
        onGpsServiceUpdate = new Data.onGpsServiceUpdate() {
            @Override
            public void update() {
                double maxSpeedTemp = data.getMaxSpeed();
                double distanceTemp = data.getDistance();
                double averageTemp;
                if (sharedPreferences.getBoolean("auto_average", false)) {
                    averageTemp = data.getAverageSpeedMotion();
                } else {
                    averageTemp = data.getAverageSpeed();
                }

                String speedUnits;
                String distanceUnits;

                speedUnits = " км/ч";
                if (distanceTemp <= 1000.0) {
                    distanceUnits = " м";
                } else {
                    distanceTemp /= 1000.0;
                    distanceUnits = " км";
                }

                SpannableString s = new SpannableString(String.format("%.0f", maxSpeedTemp) + speedUnits);
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 4, s.length(), 0);

                s = new SpannableString(String.format("%.0f", averageTemp) + speedUnits);
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 4, s.length(), 0);

                senddata=(int) Math.round(distanceTemp);
                s = new SpannableString(String.format("%.3f", distanceTemp) + distanceUnits);
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 2, s.length(), 0);
                distance.setText(s);
                if(data.getPositions().size()>0){
                    List<LatLng> locationPoints = data.getPositions();
                    refreshMap(map);
                    drawRouteOnMap(map, locationPoints);
                }
            }
        };

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        distance = findViewById(R.id.distance);
        time = findViewById(R.id.time);
        currentSpeed = findViewById(R.id.speed);

        time.setText("00:00:00");
        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean isPair = true;

            @Override
            public void onChronometerTick(Chronometer chrono) {
                long time;
                if (data.isRunning()) {
                    time = SystemClock.elapsedRealtime() - chrono.getBase();
                    data.setTime(time);
                } else {
                    time = data.getTime();
                }

                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                chrono.setText(hh + ":" + mm + ":" + ss);
                currentTime = hh + ":" + mm + ":" + ss;
                if (data.isRunning()) {
                    chrono.setText(hh + ":" + mm + ":" + ss);
                } else {
                    if (isPair) {
                        isPair = false;
                        chrono.setText(hh + ":" + mm + ":" + ss);
                    } else {
                        isPair = true;
                        chrono.setText("");
                    }
                }

            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.INVISIBLE);

        final SharedPreferences prefs = Objects.requireNonNull(this)
                .getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);

        String preferedSkin = prefs.getString("SKIN", CatView.Skins.COMMON.toString().toLowerCase());

        switch (preferedSkin) {
            case "bad":
                catView.setSkin(CatView.Skins.BAD);
                break;

            case "karate":
                catView.setSkin(CatView.Skins.KARATE);
                break;

            case "business":
                catView.setSkin(CatView.Skins.BUSINESS);
                break;

            case "normal":
                catView.setSkin(CatView.Skins.NORMAL);
                break;

            default:
                catView.setSkin(CatView.Skins.COMMON);
        }
    }

    public void onStartClick(View v) {
        if (!data.isRunning()) {
            data.setRunning(true);
            catView.run();
            start.setBackground(this.getResources().getDrawable(R.drawable.pause));
            stop.setEnabled(false);
            stop.setBackground(this.getResources().getDrawable(R.drawable.stop_inactive));
            time.setBase(SystemClock.elapsedRealtime() - data.getTime());
            time.start();
            data.setFirstTime(true);
            startService(new Intent(getBaseContext(), MyService.class));
        } else {
            data.setRunning(false);
            catView.stop();
            start.setBackground(this.getResources().getDrawable(R.drawable.play));
            stop.setEnabled(true);
            stop.setBackground(this.getResources().getDrawable(R.drawable.stop));
            stopService(new Intent(getBaseContext(), MyService.class));
        }
    }

    public void onMapClick(View v) {
        if(!map_active){
            map_active=true;
            mapFragment.getView().setVisibility(View.VISIBLE);
            startLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (startLocation!=null){
                double lat = startLocation.getLatitude();
                double lon = startLocation.getLongitude();
                LatLng startPoint = new LatLng(lat,lon);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(startPoint)
                        .zoom(15)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
        else if(map_active) {
            map_active=false;
            mapFragment.getView().setVisibility(View.INVISIBLE);
        }
    }

    public void onStopClick(View v) {
        //Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("distance", data.getDistance());
        //intent.putExtra("average", data.getAverageSpeed());
        //intent.putExtra("time", currentTime);
        resetData();
        //startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        catView.resume();
        firstfix = true;
        if (!data.isRunning()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("data", "");
            data = gson.fromJson(json, Data.class);
        }
        if (data == null) {
            data = new Data(onGpsServiceUpdate);
        } else {
            data.setOnGpsServiceUpdate(onGpsServiceUpdate);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            else{
                if (mLocationManager.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
                } else {
                    Toast.makeText(this,"Не удаётся подключиться к GPS",Toast.LENGTH_SHORT).show();
                }

                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showGpsDisabledDialog();
                }
                mLocationManager.addGpsStatusListener(this);
            }
        }
        else{
            if (mLocationManager.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
            } else {
                Toast.makeText(this,"Не удаётся подключиться к GPS",Toast.LENGTH_SHORT).show();
            }
            mLocationManager.addGpsStatusListener(this);
        }


    }

    @Override
    protected void onPause() {
        catView.pause();
        super.onPause();
        mLocationManager.removeUpdates(this);
        mLocationManager.removeGpsStatusListener(this);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        prefsEditor.putString("data", json);
        prefsEditor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), MyService.class));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.hasAccuracy()) {
            SpannableString s = new SpannableString(String.format("%.0f", location.getAccuracy()) + "м");
            s.setSpan(new RelativeSizeSpan(0.75f), s.length() - 1, s.length(), 0);
            if (firstfix) {
                firstfix = false;
            }
        } else {
            firstfix = true;
        }

        if (location.hasSpeed()) {
            String speed = String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6) + "км/ч";

            SpannableString s = new SpannableString(speed);
            s.setSpan(new RelativeSizeSpan(0.25f), s.length() - 4, s.length(), 0);
            currentSpeed.setText(s);
        }
    }

    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                int satsInView = 0;
                int satsUsed = 0;
                Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
                for (GpsSatellite sat : sats) {
                    satsInView++;
                    if (sat.usedInFix()) {
                        satsUsed++;
                    }
                }
                if (satsUsed == 0) {
                    firstfix = true;
                }
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showGpsDisabledDialog();
                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
        }
    }

    public void showGpsDisabledDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Настройки местоположения", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        });
        builder.setTitle("GPS отключен")
                .setMessage("Для работы приложения откройте доступ к местоположению GPS")
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetData(){
        time.stop();
        distance.setText("");
        time.setText("00:00:00");
        data = new Data(onGpsServiceUpdate);
    }

    public static Data getData() {
        return data;
    }

    @Override
    public void onBackPressed() {
        if (data.isRunning()==false)
            super.onBackPressed();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    public static class ChangeProgressEvent {

        public int progressmessage;

        ChangeProgressEvent(int progressmessage) {
            this.progressmessage = progressmessage;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            else{ map.setMyLocationEnabled(true);}
        }

    }
    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
        PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(positions.size()-1).latitude, positions.get(positions.size()-1).longitude))
                .zoom(15)
                .bearing(90)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    private void refreshMap(GoogleMap mapInstance){
        mapInstance.clear();
    }
}