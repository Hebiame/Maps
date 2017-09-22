package karolinakaminska.github.com.maps;

import android.app.FragmentManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;

import karolinakaminska.github.com.Constants;

public class DetailsActivity extends AppCompatActivity {

    private GoogleMap map;
    private float distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                String locations = getIntent().getStringExtra(Constants.LOCATIONS_KEY);
                if (locations.length() != 0) {
                    String[] locationsStr = locations.split(";");
                    ArrayList<LatLng> locationsLatLng = new ArrayList<>();
                    for (String i : locationsStr) {
                        String[] tmp = i.split(",");
                        locationsLatLng.add(new LatLng(Double.valueOf(tmp[1]), Double.valueOf(tmp[0])));
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationsLatLng.get(0), 17.0f));
                    PolylineOptions polylineOptions = new PolylineOptions().clickable(true).addAll(locationsLatLng).color(R.color.colorPrimaryDark);

                    float totalDistance = 0;

                    for(int i = 1; i < polylineOptions.getPoints().size(); i++) {
                        Location currLocation = new Location("this");
                        currLocation.setLatitude(polylineOptions.getPoints().get(i).latitude);
                        currLocation.setLongitude(polylineOptions.getPoints().get(i).longitude);

                        Location lastLocation = new Location("this");
                        lastLocation.setLatitude(polylineOptions.getPoints().get(i-1).latitude);
                        lastLocation.setLongitude(polylineOptions.getPoints().get(i-1).longitude);

                        totalDistance += lastLocation.distanceTo(currLocation);
                    }
                    distance = totalDistance;
                    TextView textView = findViewById(R.id.length_textView);

                    textView.setText(String.format("%.3f", distance/1000) + "km");

                    Polyline polyline = map.addPolyline(polylineOptions);
                    polyline.setJointType(JointType.ROUND);
                    polyline.setStartCap(new RoundCap());
                    polyline.setEndCap(new RoundCap());
                    //Utils.showToast(String.valueOf(locationsLatLng.get(0).latitude) + String.valueOf(locationsLatLng.get(0).longitude), getApplicationContext());
                }
            }
        });
    }

}
