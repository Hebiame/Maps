package karolinakaminska.github.com.maps;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import karolinakaminska.github.com.Constants;

public class DetailsActivity extends AppCompatActivity {

    private GoogleMap map;

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
                    map.addPolyline(new PolylineOptions().clickable(true).addAll(locationsLatLng).color(R.color.colorPrimaryDark         ));

                    //Utils.showToast(String.valueOf(locationsLatLng.get(0).latitude) + String.valueOf(locationsLatLng.get(0).longitude), getApplicationContext());
                }
            }
        });

    }


}
