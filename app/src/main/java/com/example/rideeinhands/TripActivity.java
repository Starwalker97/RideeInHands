package com.example.rideeinhands;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rideeinhands.databinding.ActivityTripBinding;
import com.example.rideeinhands.models.TripModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TripActivity extends AppCompatActivity {

    ActivityTripBinding binding;
    GoogleMap mMap;
    List<String> startinglatlng, endinglatlng;
    List<List<HashMap<String, String>>> routes;
    FirebaseFirestore firebaseFirestore;
    private JSONObject jObject;
    String whichActivity;
    MaterialButton reqsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TripActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to finish the trip?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseFirestore.collection("Trips").document(getIntent().getStringExtra("tripID")).update("Status","Completed")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(TripActivity.this, "Trip Completed", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(TripActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }
        });
        binding.status.setText("Trip Started");
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Trips").document(getIntent().getStringExtra("tripID")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                startinglatlng = Arrays.asList(documentSnapshot.getString("StartLocation").split(","));
                endinglatlng = Arrays.asList(documentSnapshot.getString("DestinationLocation").split(","));
                SupportMapFragment mapFragment =
                        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;

                        mMap.setMyLocationEnabled(true);

                        mMap.getUiSettings().setMyLocationButtonEnabled(false);

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(startinglatlng.get(0)), Double.parseDouble(startinglatlng.get(1))))
                                .title("Starting Point")
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMap.addMarker(markerOptions);

                        MarkerOptions markerOptions1 = new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(endinglatlng.get(0)), Double.parseDouble(endinglatlng.get(1))))
                                .title("Destination Point")
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.addMarker(markerOptions1);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(endinglatlng.get(0)), Double.parseDouble(endinglatlng.get(1)))));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(startinglatlng.get(0)), Double.parseDouble(startinglatlng.get(1))))      // Sets the center of the map to location user
                                .zoom(12)                   // Sets the zoom
                                .bearing(90)                // Sets the orientation of the camera to east
                                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        try {
                            jObject = new JSONObject(documentSnapshot.getString("Route"));
                            DirectionsJSONParser parser = new DirectionsJSONParser();
                            routes = null;
                            routes = parser.parse(jObject);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ArrayList points = null;
                        PolylineOptions lineOptions = null;

                        for (int i = 0; i < routes.size(); i++) {
                            points = new ArrayList();
                            lineOptions = new PolylineOptions();

                            List<HashMap<String, String>> path = routes.get(i);

                            for (int j = 0; j < path.size(); j++) {
                                HashMap point = path.get(j);

                                double lat = Double.parseDouble(point.get("lat").toString());
                                double lng = Double.parseDouble(point.get("lng").toString());
                                LatLng position = new LatLng(lat, lng);
                                points.add(position);
                            }

                            lineOptions.width(10);
                            lineOptions.color(Color.BLUE);

                            lineOptions.addAll(points);

                            lineOptions.geodesic(true);

                        }

                        // Drawing polyline in the Google Map for the i-th route
                        mMap.addPolyline(lineOptions)
                                .setStartCap(new RoundCap());


                    }
                });

            }
        });




    }
}