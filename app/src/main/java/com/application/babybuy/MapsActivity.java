package com.application.babybuy;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import com.application.babybuy.databinding.BabybuyMapActivityBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private BabybuyMapActivityBinding binding;

    String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = BabybuyMapActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //  obtain the SupportMapFragment and receive a notification, When the map is prepared for usage,
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // back button
        Button back = (Button) findViewById(R.id.map_back);
        back.setOnClickListener( view -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });


        BuyItem item = (BuyItem) getIntent().getSerializableExtra("VIEW");

        lat = item.getLat();
        lng = item.getLng();

        LatLng sydney = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

        if (sydney == null) {
            sydney = new LatLng(-34, 151);
        }

        mMap.addMarker(new MarkerOptions().position(sydney).title("Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Add self zoom feature
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
    }


}