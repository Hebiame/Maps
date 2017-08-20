package karolinakaminska.github.com.maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.SENSOR_SERVICE;

public class MapViewFragment extends Fragment implements CompassListener, LocationTrackerListener, GoogleMap.OnCameraMoveStartedListener {
    private MapView mMapView;
    private GoogleMap googleMap;
    private Marker marker;
    private Compass compass;
    private LocationTracker locationTracker;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean moveCamera = false;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        compass = new Compass((SensorManager) getContext().getSystemService(SENSOR_SERVICE));
        compass.addListener(this);

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationTracker = new LocationTracker(locationManager);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final GoogleMap.OnCameraMoveStartedListener cameraMoveStartedListener = this;
        final LocationTrackerListener locationTrackerListener = this;
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setCompassEnabled(false);
                uiSettings.setMapToolbarEnabled(false);

                if (!checkPermission(getActivity())) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
                else {
                    try {
                        locationTracker.addListener(locationTrackerListener);
                        locationTracker.start();
                    } catch (SecurityException e) {
                        Utils.showToast("Brak uprawnień", getContext());
                    }
                }

                googleMap.setOnCameraMoveStartedListener(cameraMoveStartedListener);
                setupFabs(rootView);
            }
        });

        return rootView;
    }

    private void setupFabs(View rootView){
        final FloatingActionButton centerFab = (FloatingActionButton) rootView.findViewById(R.id.center_fab);
        final FloatingActionButton startFab = (FloatingActionButton) rootView.findViewById(R.id.start_fab);
        centerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(marker != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0f));
                    moveCamera = true;
                }
            }
        });
        startFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //............
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE)
        {
            moveCamera = false;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                    Utils.showToast("Brak uprawnień", getContext());
                }
        }
    }

    public void onAzimuthChanged(float azimuth) {
        if (marker != null)
        {
            marker.setRotation(azimuth);
        }
    }

    public void onLocationChanged(Location location) {
        if (marker == null)
        {
            Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
            Bitmap scaledArrow = Utils.scaleBitmap(arrow, 60, 60);
            marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .flat(true).anchor(0.5f, 0.66f).icon(BitmapDescriptorFactory.fromBitmap(scaledArrow)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0f));
        }
        else {
            moveMarker(marker.getPosition(), new LatLng(location.getLatitude(), location.getLongitude()));
        }

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

                if (moveCamera) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18f));
                }
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        compass.start();
        locationTracker.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        compass.stop();
        locationTracker.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        compass.stop();
        locationTracker.stop();
        compass.removeListener(this);
        locationTracker.removeListener(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}

