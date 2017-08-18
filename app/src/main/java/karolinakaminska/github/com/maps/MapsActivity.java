package karolinakaminska.github.com.maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, CompassListener, LocationTrackerListener {

    private GoogleMap mMap;
    private Marker marker;
    private Compass compass;
    private LocationTracker locationTracker;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    /*----set  color----*/
        //window.setStatusBarColor(this.getResources().getColor(R.color.my_statusbar_color));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        compass = new Compass((SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE));
        compass.addListener(this);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationTracker = new LocationTracker(locationManager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
        locationTracker.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
        locationTracker.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compass.stop();
        locationTracker.stop();
        compass.removeListener(this);
        locationTracker.removeListener(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        Bitmap arrow = BitmapFactory.decodeResource(this.getResources(), R.drawable.arrow);
        Bitmap scaledArrow = scaleBitmap(arrow, 60, 60);

        LatLng startPos = new LatLng(0, 0);
        marker = mMap.addMarker(new MarkerOptions().position(startPos).flat(true).anchor(0.5f, 0.66f).icon(BitmapDescriptorFactory.fromBitmap(scaledArrow)));


        if (!checkPermission(this)) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        else {
            try {
                locationTracker.addListener(this);
                locationTracker.start();
            } catch (SecurityException e) {
                Utils.showToast("Brak uprawnień", this);
            }
        }

        Log.d("xD", "hehe dziala");
        compass.start();
    }


    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

    public void onAzimuthChanged(float azimuth) {
        if (marker != null)
        {
            marker.setRotation(azimuth);
        }
    }

    public void onLocationChanged(Location location) {
        moveMarker(marker.getPosition(), new LatLng(location.getLatitude(), location.getLongitude()));
    }

    protected void moveMarker(final LatLng startPos, final LatLng endPos) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1000;
        final Interpolator interpolator = new FastOutSlowInInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * endPos.longitude + (1 - t)
                        * startPos.longitude;
                double lat = t * endPos.latitude + (1 - t)
                        * startPos.latitude;

                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        locationTracker.start();

                } else {
                    Utils.showToast("Brak uprawnień", this);
                }
        }
    }
}

//remove listeners
