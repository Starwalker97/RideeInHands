package com.example.rideeinhands;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rideeinhands.fragments.MyTripsFragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TripDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tripName, tripDetail, startingPoint, destinationPoint, dateTime;
    public static TripModel activeTrip;
    GoogleMap mMap;
    List<String> startinglatlng, endinglatlng;
    List<List<HashMap<String, String>>> routes;
    ImageView del;
    FirebaseFirestore firebaseFirestore;
    private JSONObject jObject;
    String whichActivity;
    ExtendedFloatingActionButton reqsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tripName = findViewById(R.id.tripName);
        tripDetail = findViewById(R.id.tripDetails);
        startingPoint = findViewById(R.id.starting_point);
        destinationPoint = findViewById(R.id.destination_point);
        reqsBtn = findViewById(R.id.reqs);
        dateTime = findViewById(R.id.dateTime);
        del = findViewById(R.id.del);
        whichActivity = getIntent().getStringExtra("whichActivity");
        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("Trips")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Pending");


        int itemPosition = getIntent().getIntExtra("position", 0);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("TAG", queryDocumentSnapshots.size() + "");
                        AlertDialog.Builder builder = new AlertDialog.Builder(TripDetailActivity.this);
                        builder.setTitle("Confirmation")
                                .setMessage("Are you sure you want to delete this trip?")
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        collectionReference.document(queryDocumentSnapshots.getDocuments().get(itemPosition).getId())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Toast.makeText(TripDetailActivity.this, "Trip Deleted", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(TripDetailActivity.this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        builder.show();


                    }
                });
            }
        });

        try {
            if (whichActivity.equals("AvailableTrips")) {
                activeTrip = AvailableTrips.tripsList.get(itemPosition);
                del.setVisibility(View.GONE);
                reqsBtn.setText("Request Trip");
                reqsBtn.setIcon(getDrawable(R.drawable.ic_near_me_black_24dp));
            } else {
                activeTrip = MyTripsFragment.activetripsList.get(itemPosition);
                reqsBtn.setText("See Requests");
            }
        } catch (NullPointerException ex) {
            activeTrip = MyTripsFragment.activetripsList.get(itemPosition);
            reqsBtn.setText("See Requests");
        }


        reqsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reqsBtn.getText().toString().equals("Request Trip")) {
                    String currentdate = Calendar.getInstance().getTime().toString();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Status", "Requested");
                    map.put("DateTime", currentdate);
                    map.put("PickUpPoint", getIntent().getStringExtra("PickupPoint"));
                    reqsBtn.setEnabled(false);
                    firebaseFirestore.collection("Trips")
                            .document(activeTrip.getUserId())
                            .collection("Pending")
                            .document(activeTrip.getTripId())
                            .collection("Requests")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .set(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reqsBtn.setText("Cancel Request");
                                        reqsBtn.setEnabled(true);
                                        reqsBtn.setTextColor(Color.WHITE);
                                        reqsBtn.setBackgroundColor(getColor(R.color.colorPrimary));
                                        reqsBtn.setIcon(getDrawable(R.drawable.ic_clear_white_24dp));
                                    } else {
                                        Toast.makeText(TripDetailActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TripDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                } else if (reqsBtn.getText().toString().equals("See Requests")) {
                    Intent intent = new Intent(TripDetailActivity.this, RequestsActivity.class);
                    startActivity(intent);
                }
            }
        });

        tripName.setText(activeTrip.getName());
        tripDetail.setText(activeTrip.getDetail());
        startingPoint.setText(activeTrip.getStart());
        destinationPoint.setText(activeTrip.getDestination());
        dateTime.setText(activeTrip.getDate() + " , " + activeTrip.getTime());
        startinglatlng = Arrays.asList(activeTrip.getStartLocation().split(","));
        endinglatlng = Arrays.asList(activeTrip.getDestinationLocation().split(","));

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
                        .target(new LatLng(Double.parseDouble(endinglatlng.get(0)), Double.parseDouble(endinglatlng.get(1))))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                try {
                    jObject = new JSONObject(activeTrip.getRoute());
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
}
