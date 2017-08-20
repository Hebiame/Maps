package karolinakaminska.github.com.maps;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocationTracker {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Criteria criteria;
    private String providerFine;
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
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        providerFine = locationManager.getBestProvider(criteria, true);


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

    public void addListener(LocationTrackerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LocationTrackerListener listener) {
        listeners.remove(listener);
    }
}
