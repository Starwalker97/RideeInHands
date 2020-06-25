package com.example.rideeinhands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class HeadTowardsActivity extends AppCompatActivity {
    private static boolean gps_enabled;
    private static boolean network_enabled;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_towards);

        if (ContextCompat.checkSelfPermission(HeadTowardsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HeadTowardsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1901);
        } else {
            setLocation();
        }
        FirebaseFirestore.getInstance().collection("Trips").document(getIntent().getStringExtra("tripID"))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists() && documentSnapshot.getString("Status").equals("Started")) {
                            TextView textView = findViewById(R.id.status);
                            textView.setText("Your Trip has been started");
                            Button button = findViewById(R.id.cancel_button);
                            button.setTextColor(getColor(R.color.colorPrimary));
                            button.setText("I've reached my destination");
                        } else if (documentSnapshot.exists() && documentSnapshot.getString("Status").equals("Completed")) {
                            TextView textView = findViewById(R.id.status);
                            textView.setText("The trip has been completed");
                            Button button = findViewById(R.id.cancel_button);
                            button.setTextColor(getColor(R.color.colorPrimary));
                            button.setText("I've reached my destination");
                        }
                    }
                });
        FirebaseFirestore.getInstance().collection("Requests").document(getIntent().getStringExtra("requestID"))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                FirebaseFirestore.getInstance().collection("Users").document(documentSnapshot.getString("To"))
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TextView textView = findViewById(R.id.name);
                        textView.setText(documentSnapshot.getString("Name"));
                        CircleImageView imageView = findViewById(R.id.image);
                        Picasso.get().load(Uri.parse(documentSnapshot.getString("ProfilePicture"))).into(imageView);
                    }
                });
            }
        });


        FirebaseFirestore.getInstance().collection("Requests").document(getIntent().getStringExtra("requestID"))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                to = documentSnapshot.getString("To");
            }
        });

        ImageView imageView = findViewById(R.id.chat_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeadTowardsActivity.this, ChatActivity.class);
                intent.putExtra("With", to);
                startActivity(intent);
            }
        });
        Button button = findViewById(R.id.cancel_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().toString().equals("Cancel Request")) {
                    FirebaseFirestore.getInstance().collection("Requests").document(getIntent().getStringExtra("requestID"))
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(HeadTowardsActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else if (button.getText().toString().equals("I've reached my destination")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HeadTowardsActivity.this)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you have reached your destination?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseFirestore.getInstance().collection("Requests").document(getIntent().getStringExtra("requestID"))
                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            FirebaseFirestore.getInstance().collection("Wallets").document(documentSnapshot.getString("From"))
                                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot1) {
                                                    Long price = documentSnapshot.getLong("Price");
                                                    Long l = documentSnapshot1.getLong("Total");
                                                    Long payment = l - documentSnapshot.getLong("Price");
                                                    FirebaseFirestore.getInstance().collection("Wallets").document(documentSnapshot.getString("From"))
                                                            .update("Total", payment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseFirestore.getInstance().collection("Wallets").document(documentSnapshot.getString("To"))
                                                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                                                    Long l = documentSnapshot2.getLong("Total");
                                                                    Long payment = l + documentSnapshot.getLong("Price");
                                                                    FirebaseFirestore.getInstance().collection("Wallets").document(documentSnapshot.getString("To"))
                                                                            .update("Total", payment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Intent intent = new Intent(HeadTowardsActivity.this, MainActivity.class);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    });

                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
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
            }
        });

    }

    private void setLocation() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
//                try{
//                boolean success = googleMap.setMapStyle(
//                        MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.mapstyle));
//
//                if (!success) {
//                }
//            } catch (Resources.NotFoundException e) {
//            }

                if (ActivityCompat.checkSelfPermission(HeadTowardsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HeadTowardsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                FirebaseFirestore.getInstance().collection("Trips").document(getIntent().getStringExtra("tripID"))
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String start_location[] = documentSnapshot.getString("StartLocation").split(",");
                        LatLng start_latLng = new LatLng(Double.parseDouble(start_location[0]), Double.parseDouble(start_location[1]));
                        mMap.addMarker(new MarkerOptions().position(start_latLng).title("Ride Holder"));
                    }
                });


                LocationManager lm = (LocationManager) HeadTowardsActivity.this.getSystemService(Context.LOCATION_SERVICE);
                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex) {
                }
                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {
                }
                if (!gps_enabled && !network_enabled) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HeadTowardsActivity.this);
                    dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
                    dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        }
                    });
                    dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            System.exit(0);
                        }
                    });
                    dialog.show();
                }

                if (HeadTowardsActivity.this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && HeadTowardsActivity.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(HeadTowardsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1901);
                } else {
                    ShowLocation();

                }

                mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                    @Override
                    public void onMyLocationClick(@NonNull Location location) {
                        Toast.makeText(HeadTowardsActivity.this, "Current location:\n" + location, Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1901: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation();
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
                return;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        LocationManager lm = (LocationManager) HeadTowardsActivity.this
                .getSystemService(Context.LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            ShowLocation();
        }

    }

    private void ShowLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HeadTowardsActivity.this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (ActivityCompat.checkSelfPermission(HeadTowardsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HeadTowardsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                    .zoom(12)                   // Sets the zoom
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
}