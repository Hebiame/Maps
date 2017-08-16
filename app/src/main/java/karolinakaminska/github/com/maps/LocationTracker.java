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

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationTracker {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng currentPos;
    //private MarkerOptions markerOptions;
    private Marker marker;
    private Criteria criteria;
    private String providerFine;
    private String providerCoarse;
    private GoogleMap map;


    public LocationTracker(Marker m, LocationManager lc, GoogleMap mp) {
        marker = m;
        criteria = new Criteria();
        currentPos = new LatLng(0, 0);
        locationManager = lc;
        map = mp;
    }

    public void start() {
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
                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 18.0f));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                getBestProvider(provider, status);
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


            try {
                locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 0, 0, locationListener);
            } catch (SecurityException e) {
                throw e;
            }
            //showToast("first provider");
    }


    private void getBestProvider(String provider, int status) {
        if (provider.equals(providerFine)) {

                if (status == 0) {
                    try {
                        locationManager.requestLocationUpdates(providerCoarse, 1000, 1, locationListener);
                    } catch (SecurityException e) {
                        throw e;
                    }
                    //showToast("Coarse provider");
                }
                if (status == 2) {
                    try {
                        locationManager.requestLocationUpdates(providerFine, 1000, 1, locationListener);
                    } catch (SecurityException e) {
                        throw e;
                    }
                    //showToast("Fine provider");
                }

        }
    }

}
