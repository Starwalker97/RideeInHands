package com.example.rideeinhands.adminactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rideeinhands.LoginActivity;
import com.example.rideeinhands.R;
import com.example.rideeinhands.adminfragments.LicensesFragment;
import com.example.rideeinhands.fragments.MainFragment;
import com.example.rideeinhands.fragments.MyAccountFragment;
import com.example.rideeinhands.fragments.MyLicenseFragment;
import com.example.rideeinhands.fragments.MyTripsFragment;
import com.example.rideeinhands.fragments.MyVehiclesFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivityAdmin extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseFirestore db;
    String username;
    String profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivityAdmin.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        String fragToLoad = getIntent().getStringExtra("fragToLoad");
        if (fragToLoad != null) {
            if (fragToLoad.equals("ActiveTrips")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new MyTripsFragment()).commit();

            }
        }

        DocumentReference documentReference = db.collection("Users")
                .document(firebaseAuth.getCurrentUser().getUid());

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                username = documentSnapshot.getString("Name");
                profilePicture = documentSnapshot.getString("ProfilePicture");

                TextView textView = navigationView.getHeaderView(0).findViewById(R.id.username);
                textView.setText(username);
                CircleImageView circleImageView = navigationView.getHeaderView(0).findViewById(R.id.img);
                Picasso.get().load(Uri.parse(profilePicture)).placeholder(R.drawable.ic_account_circle_black_24dp).into(circleImageView);

            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new LicensesFragment()).commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.userLicenses:
                        drawerLayout.closeDrawer(GravityCompat.START, false);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new LicensesFragment()).commit();
                        break;
                    case R.id.log_out:
                        drawerLayout.closeDrawer(GravityCompat.START, false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityAdmin.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to log out?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                drawerLayout.closeDrawer(GravityCompat.START, false);
                                Intent intent = new Intent(MainActivityAdmin.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                firebaseAuth.signOut();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivityAdmin.this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                        break;
                }

                return true;
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
