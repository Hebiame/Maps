package karolinakaminska.github.com;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;

import karolinakaminska.github.com.maps.LocationTracker;
import karolinakaminska.github.com.maps.LocationTrackerListener;
import karolinakaminska.github.com.maps.R;


public class LocationSamplerService extends Service implements LocationTrackerListener {
    private LinkedList<LatLng> list;
    private Timestamp startDate;
    private LocationTracker locationTracker;
    private NotificationManager notificationManager;
    private int NOTIFICATION = R.string.location_sampler_started;


    public class LocalBinder extends Binder {
        LocationSamplerService getService() {
            return LocationSamplerService.this;
        }
    }


    @Override
    public void onCreate() {
        list = new LinkedList();
        startDate = new Timestamp(System.currentTimeMillis());
        locationTracker = new LocationTracker((LocationManager) getSystemService(LOCATION_SERVICE));
        locationTracker.start();
        locationTracker.addListener(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(Constants.BROADCAST_ACTION), 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.arrow)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.ticker_text))
                .setOngoing(true)
                .build();
        notificationManager.notify(NOTIFICATION, notification);

        startForeground(1010, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onLocationChanged(Location l) {
        list.add(new LatLng(l.getLatitude(), l.getLongitude()));
    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.BUNDLE_KEY, new ArrayList<LatLng>(list));

        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                // Puts the status into the Intent
                .putExtra(Constants.EXTENDED_DATA_STATUS, bundle)
                .putExtra(Constants.LOCATION_SAMPLER_START_DATE, startDate.getTime())
                .putExtra(Constants.LOCATION_SAMPLER_END_DATE, new Timestamp(System.currentTimeMillis()).getTime());
        // Broadcasts the Intent to receivers in this app.
        Log.e("gowno", "onDestroy: hheehe");
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        locationTracker.stop();
        locationTracker.removeListener(this);
        stopForeground(true);
        super.onDestroy();
    }

    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
