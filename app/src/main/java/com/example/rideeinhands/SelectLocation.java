package com.example.rideeinhands;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SelectLocation extends AppCompatActivity {
    TextInputLayout destinationLayout, startingLayout;
    EditText startingPoint, destinationPoint;
    int AUTOCOMPLETE_STARTING_POINT_REQUEST_CODE = 1;
    int AUTOCOMPLETE_DESTINATION_POINT_REQUEST_CODE = 2;
    LatLng startingPointLoc, destinationPointLoc;
    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setMyLocationEnabled(true);

                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        });


        ShowLocation();

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));

        PlacesClient placesClient = Places.createClient(this);
        destinationLayout = findViewById(R.id.destination_layout);
        destinationPoint = findViewById(R.id.destination_point);
        startingPoint = findViewById(R.id.starting_point);
        startingLayout = findViewById(R.id.start_layout);

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
}
