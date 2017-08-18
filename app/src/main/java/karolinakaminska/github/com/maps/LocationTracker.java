package karolinakaminska.github.com.maps;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocationTracker {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Criteria criteria;
    private String providerFine;
    private String providerCoarse;
    private Set<LocationTrackerListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<LocationTrackerListener, Boolean>());

    public LocationTracker(LocationManager lc) {
        criteria = new Criteria();
        locationManager = lc;
        locationListener = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                for (LocationTrackerListener listener : listeners) {
                    listener.onLocationChanged(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //getBestProvider(provider, status);
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    public void start() {
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        //criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //providerCoarse = locationManager.getBestProvider(criteria, true);

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        providerFine = locationManager.getBestProvider(criteria, true);

//        try {
//            Location l = locationManager.getLastKnownLocation(providerFine);
//            if (l != null) {
//                   locationListener.onLocationChanged(l);
//            }
//            else {
//                l = locationManager.getLastKnownLocation(providerCoarse);
//                if (l != null){
//                    locationListener.onLocationChanged(l);
//                }
//            }
//
//            String provider = locationManager.getBestProvider(criteria, true);
//            if (provider != null) {
//                locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
//            }
//        } catch (SecurityException e) {
//            throw e;
//        }
            //showToast("first provider");

        try {
            Location l = locationManager.getLastKnownLocation(providerFine);
            if (l != null) {
                   locationListener.onLocationChanged(l);
            }
        String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 0, 2, locationListener);
            }
        } catch (SecurityException e) {
            throw e;
        }
    }

    public void stop() {
        locationManager.removeUpdates(locationListener);

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

    public void addListener(LocationTrackerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LocationTrackerListener listener) {
        listeners.remove(listener);
    }
}
