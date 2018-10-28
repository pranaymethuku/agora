package com.agora.android.agora;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import java.util.List;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Button mSignOutButton;
    private BottomNavigationView mBottomNavigationView;
    private int mCurrentItemID;
    private GoogleApiClient mGoogleApiClient;


    private static final String EXTRA_GOOGLE_ACCOUNT = "com.agora.android.agora.google_account";
    private static final String EXTRA_SIGN_OUT = "com.agora.android.agora.sign_out";

    public static boolean isSignOutRequested(Intent intent) {
        if (intent == null) {
            return false;
        }
        return intent.getBooleanExtra(EXTRA_SIGN_OUT, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        final GoogleSignInAccount googleSignInAccount = getIntent()
                .getParcelableExtra(EXTRA_GOOGLE_ACCOUNT);

        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        mCurrentItemID = Integer.MIN_VALUE;

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (mCurrentItemID != item.getItemId()) {
                    Fragment itemFragment = getItemFragment(item, googleSignInAccount);
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
                .build();
//
//
//        mSignOutButton = (Button) findViewById(R.id.button_sign_out);
//        mSignOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setSignOutRequested(true);
//            }
//        });
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    private Fragment getItemFragment(MenuItem item, GoogleSignInAccount googleSignInAccount) {
        Fragment itemFragment = null;
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                itemFragment = DashboardFragment.newInstance(googleSignInAccount);
                break;
            case R.id.nav_chat:
                itemFragment = ChatFragment.newInstance(googleSignInAccount);
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

    public static Intent newIntent(Context context, GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(context, BaseActivity.class);
        intent.putExtra(EXTRA_GOOGLE_ACCOUNT, googleSignInAccount);
        return intent;
    }

    private void setSignOutRequested(boolean isSignOutRequested) {
        Intent data = new Intent();
        data.putExtra(EXTRA_SIGN_OUT, isSignOutRequested);
        setResult(RESULT_OK, data);
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

    }
}
