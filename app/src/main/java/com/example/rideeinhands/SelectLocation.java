package com.example.rideeinhands;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.rideeinhands.models.TripModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SelectLocation extends AppCompatActivity {
    public static ArrayList<TripModel> tripsList;
    TextInputLayout destinationLayout, startingLayout;

    EditText startingPoint, destinationPoint;
    int AUTOCOMPLETE_STARTING_POINT_REQUEST_CODE = 1;
    int AUTOCOMPLETE_DESTINATION_POINT_REQUEST_CODE = 2;
    LatLng startingPointLoc, destinationPointLoc;
    GoogleMap mMap;
    String tripName, tripDetail, tripDate, tripTime, no_of_passengers, vehicle;
    Toolbar toolbar;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Map<String, Object> hashMap;
    String routeString;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        hashMap = new HashMap<>();

        String whichActivity = getIntent().getStringExtra("whichActivity");

        tripName = getIntent().getStringExtra("tripName");
        tripDate = getIntent().getStringExtra("tripDate");
        tripDetail = getIntent().getStringExtra("tripDetail");
        tripTime = getIntent().getStringExtra("tripTime");
        no_of_passengers = getIntent().getStringExtra("no_of_passengers");
        vehicle = getIntent().getStringExtra("vehicle");


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (ActivityCompat.checkSelfPermission(SelectLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SelectLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);

                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        });

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whichActivity.equals("CreateTrip")) {
                    hashMap.put("Name", tripName);
                    hashMap.put("Detail", tripDetail);
                    hashMap.put("Date", tripDate);
                    hashMap.put("Time", tripTime);
                    hashMap.put("Vehicle", vehicle);
                    hashMap.put("RideHolder", FirebaseAuth.getInstance().getUid());
                    hashMap.put("NumberOfPassengers", Integer.parseInt(no_of_passengers));
                    hashMap.put("Start", startingPoint.getText().toString());
                    hashMap.put("Destination", destinationPoint.getText().toString());
                    hashMap.put("StartLocation", startingPointLoc.latitude + "," + startingPointLoc.longitude);
                    hashMap.put("DestinationLocation", destinationPointLoc.latitude + "," + destinationPointLoc.longitude);
                    hashMap.put("Route", routeString);
                    hashMap.put("Status", "pending");
                    CollectionReference collectionReference = firebaseFirestore.collection("Trips");
                    collectionReference.add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Intent intent = new Intent(SelectLocation.this, RequestsActivity.class);
                            intent.putExtra("docID", documentReference.getId());
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SelectLocation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toast.makeText(SelectLocation.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (whichActivity.equals("GetARide")) {
                    tripsList = new ArrayList<>();
                    Intent intent = new Intent(SelectLocation.this, AvailableTrips.class);
                    intent.putExtra("startLocationLat", startingPointLoc.latitude);
                    intent.putExtra("startLocationLong", startingPointLoc.longitude);
                    intent.putExtra("destinationLocationLat", destinationPointLoc.latitude);
                    intent.putExtra("destinationLocationLong", destinationPointLoc.longitude);
                    intent.putExtra("no_of_passengers", no_of_passengers);
                    startActivity(intent);


                }

            }
        });


        ShowLocation();

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));

        PlacesClient placesClient = Places.createClient(this);
        destinationLayout = findViewById(R.id.destination_layout);
        destinationPoint = findViewById(R.id.destination_point);
        startingPoint = findViewById(R.id.starting_point);
        startingLayout = findViewById(R.id.start_layout);
        destinationPoint.setEnabled(false);


        startingPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress(true);
            }
        });
        startingPoint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    getAddress(true);
            }
        });
        destinationPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress(false);
            }
        });
        destinationPoint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    getAddress(false);
            }
        });


    }

    private void getAddress(boolean isStartingPoint) {
        if (isStartingPoint) {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG);

            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_STARTING_POINT_REQUEST_CODE);
        } else {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_DESTINATION_POINT_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_STARTING_POINT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                startingPoint.setText(place.getName() + " " + place.getAddress());
                startingPointLoc = place.getLatLng();
                LatLng point = startingPointLoc;
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(point)
                        .title("Starting Point")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                destinationPoint.setEnabled(true);
                if (destinationPointLoc != null) {
                    String url = getDirectionsUrl(startingPointLoc, destinationPointLoc);
                    Log.e("URL", url);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }
                mMap.addMarker(markerOptions);
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        startingPointLoc = marker.getPosition();
                        List<Address> addresses = geoLocate(startingPointLoc);
                        startingPoint.setText(addresses.get(0).getAddressLine(addresses.get(0).getMaxAddressLineIndex()));

                    }
                });
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == AUTOCOMPLETE_DESTINATION_POINT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                destinationPoint.setText(place.getName() + " " + place.getAddress());
                destinationPointLoc = place.getLatLng();
                LatLng point = destinationPointLoc;
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(point)
                        .title("Destination Point")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mMap.addMarker(markerOptions);
                String url = getDirectionsUrl(startingPointLoc, destinationPointLoc);
                Log.e("URL", url);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        destinationPointLoc = marker.getPosition();
                        List<Address> addresses = geoLocate(destinationPointLoc);
                        destinationPoint.setText(addresses.get(0).getAddressLine(addresses.get(0).getMaxAddressLineIndex()));
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(url);
                    }
                });
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ShowLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SelectLocation.this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (ActivityCompat.checkSelfPermission(SelectLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SelectLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                                    .zoom(17)                   // Sets the zoom
                                    .bearing(90)                // Sets the orientation of the camera to east
                                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        } else {

                        }
                    }
                });
            }
        }, 1500);
    }

    private List<Address> geoLocate(LatLng latLng) {
        Geocoder geocoder = new Geocoder(SelectLocation.this, Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addresses;

        } catch (IOException e) {

        }
        return null;
    }

    //Code for creating route on the map

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getResources().getString(R.string.google_api_key);


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    public List<List<HashMap<String, String>>> parse(String... json) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(json[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();


            routes = parser.parse(jObject);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return routes;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }

            routeString = jsonData[0];

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

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
                findViewById(R.id.submit).setVisibility(View.VISIBLE);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions)
                    .setStartCap(new RoundCap());

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


}
