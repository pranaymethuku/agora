package com.agora.android.agora;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "BaseActivity";
    private BottomNavigationView mBottomNavigationView;
    private int mCurrentItemID;

    private SharedPreferences mSharedPreferences;

    private GoogleApiClient mGoogleApiClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Auth activity
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        mCurrentItemID = Integer.MIN_VALUE;

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (mCurrentItemID != item.getItemId()) {
                    Fragment itemFragment = getItemFragment(item);
                    if (itemFragment == null) {
                        Toast.makeText(BaseActivity.this, "ERROR: You cannot visit " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (mCurrentItemID == Integer.MIN_VALUE) {
                        fm.beginTransaction()
                                .add(R.id.fragment_container, itemFragment)
                                .commit();
                    } else {
                        fm.beginTransaction().replace(R.id.fragment_container, itemFragment).commit();
                    }
                    mCurrentItemID = item.getItemId();
                    return true;
                } else {
                    return false;
                }
            }

        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    private Fragment getItemFragment(MenuItem item) {
        Fragment itemFragment = null;
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                itemFragment = new DashboardFragment();
                break;
            case R.id.nav_chat:
                itemFragment = new ChatFragment();
                break;
            case R.id.nav_map:
                Location location = getLastLocation();
                if (location != null) {
                    itemFragment = GoogleMapFragment.newInstance(location);
                }
                break;
        }
        return itemFragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_sign_out:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, AuthActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private Location getLastLocation() {
        boolean permissionOn=false;
        Location returnLocation = null;
        //if at least Marshmallow, need to ask user's permission to get GPS data
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if permission is not yet granted, ask for it
            while (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            permissionOn=true;
        } else {
            permissionOn=true;
        }

        if(permissionOn) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = lm.getProviders(true);

            /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
            for (int i = providers.size() - 1; i >= 0; i--) {
                returnLocation = lm.getLastKnownLocation(providers.get(i));
                if (returnLocation != null) break;
            }
        }
        return returnLocation;

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
