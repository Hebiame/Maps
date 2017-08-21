package karolinakaminska.github.com;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;

import karolinakaminska.github.com.maps.LocationTrackerListener;


public class LocationSamplerService extends IntentService implements LocationTrackerListener {
    private LinkedList<LatLng> list;
    private Intent intent;

    public LocationSamplerService() {
        super("LocationSamplerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        this.intent = intent;
        Log.e("XDDDDDD", "zabij sie");
        list = new LinkedList();
    }

    @Override
    public void onLocationChanged(Location l) {
        list.add(new LatLng(l.getLatitude(), l.getLongitude()));
    }

    @Override
    public void onDestroy() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.BUNDLE_KEY, new ArrayList<LatLng>(list));

        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                // Puts the status into the Intent
                .putExtra(Constants.EXTENDED_DATA_STATUS, bundle);
        // Broadcasts the Intent to receivers in this app.
        Log.e("gowno", "onDestroy: hheehe");
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        super.onDestroy();
    }
}
