package karolinakaminska.github.com.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationTracker {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng currentPos;
    private MarkerOptions markerOptions;
    private Marker marker;
    private Criteria criteria;
    private String providerFine;
    private String providerCoarse;
    private Context context;
    private Activity activity;
    private GoogleMap map;
    private static final int PERMISSION_REQUEST_CODE = 200;

    public LocationTracker(Marker m, Context c, Activity a, GoogleMap mp) {
        marker = m;
        criteria = new Criteria();
        currentPos = new LatLng(0, 0);
        context = c;
        activity = a;
        map = mp;
    }

    public void start() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        providerFine = locationManager.getBestProvider(criteria, true);
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        providerCoarse = locationManager.getBestProvider(criteria, true);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentPos = new LatLng(location.getLatitude(), location.getLongitude());
                marker.setPosition(currentPos);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 18.0f));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                getBestprovider(provider, status);
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!checkPermission(context)) {
            requestPermission();
        }
        else {
            locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 0, 0, locationListener);
            //showToast("first provider");
        }
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    public void getBestprovider(String provider, int status) {
        if (provider.equals(providerFine)) {
            if(checkPermission(context)) {
                if (status == 0) {
                    locationManager.requestLocationUpdates(providerCoarse, 1000, 1, locationListener);
                    //showToast("Coarse provider");
                }
                if (status == 2) {
                    locationManager.requestLocationUpdates(providerFine, 1000, 1, locationListener);
                    //showToast("Fine provider");
                }
            }
        }
    }

}
