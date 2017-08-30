package karolinakaminska.github.com;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import karolinakaminska.github.com.maps.Utils;

public class LocationSamplerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PathReaderDbHelper dbHelper = new PathReaderDbHelper(context);
        SQLiteDatabase db = null;
        AsyncTask getDb = new GetWritableDbTask().execute(dbHelper);
        Bundle bundle = intent.getBundleExtra(Constants.EXTENDED_DATA_STATUS);
        long startDate = intent.getLongExtra(Constants.LOCATION_SAMPLER_START_DATE, 0);
        long endDate = intent.getLongExtra(Constants.LOCATION_SAMPLER_END_DATE, 0);
        ArrayList<LatLng> list = bundle.getParcelableArrayList(Constants.BUNDLE_KEY);
        ArrayList<String> latlngs = new ArrayList<>();
        for (LatLng i : list) {
            double lng = i.longitude;
            double lat = i.latitude;
            latlngs.add(lng + "," + lat);
        }

        try {
            db = (SQLiteDatabase) getDb.get();
        } catch (Exception e) {
            Utils.showToast("Brak dostępu do bazy", context);
        }
        ContentValues values = new ContentValues();
        values.put(PathReaderContract.PathEntry.COLUMN_NAME_LOCATIONS, TextUtils.join(";", latlngs));
        String kupa = TextUtils.join(";", latlngs);
        Log.e("XD", kupa);
        values.put(PathReaderContract.PathEntry.COLUMN_NAME_START_DATE, startDate);
        values.put(PathReaderContract.PathEntry.COLUMN_NAME_END_DATE, endDate);
        if (db == null) {
            Utils.showToast("Baza danych nie istnieje", context);
        } else {
            long newRowId = db.insert(PathReaderContract.PathEntry.TABLE_NAME, null, values);
        }

        Log.e("ŻAL.PL", "XDDDDDDDDDDDDDDDDDDDDDD");
    }

}
