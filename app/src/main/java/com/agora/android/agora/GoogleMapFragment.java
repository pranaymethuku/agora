package com.agora.android.agora;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class GoogleMapFragment extends Fragment {

    private static String ARG_LAST_LOCATION = "com.agora.android.agora.last_location";

    private String TAG;
    private SupportMapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private Location mLastLocation;
    AutoCompleteTextView addressText;
    private PlaceAutocompleteAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    static LatLng placeLtG;
    static LatLng finalLatLng;
    private Marker originalMarker;
    private Button confirmButton;


    public static Fragment newInstance (Location lastLocation) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_LAST_LOCATION, lastLocation);

        GoogleMapFragment fragment = new GoogleMapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastLocation = getArguments().getParcelable(ARG_LAST_LOCATION);

        // don't recreate fragment everytime ensure last map location/state are maintained
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;

                    LatLng newLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    originalMarker=mGoogleMap.addMarker(new MarkerOptions().position(newLocation).title("You are here currently"));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation,19),1000,null);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called for GoogleMapFragment");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Log.d(TAG, "mGoogleApiClient.connect() called for in onStart()");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called for GoogleMapFragment");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
            Log.d(TAG, "mGoogleApiClient.disconnect() called for in onStart()");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach() called for GoogleMapFragment");
        mGoogleApiClient = ((BaseActivity) activity).getGoogleApiClient();
    }

    @Override
    public  void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach() called for GoogleMapFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_map, container, false);

        confirmButton= (Button) view.findViewById(R.id.confirmButton);
        addressText= (AutoCompleteTextView) view.findViewById(R.id.addressText);

        addressText.setOnItemClickListener(autoCompleteListen);

        adapter=new PlaceAutocompleteAdapter(getActivity(),mGoogleApiClient, LAT_LNG_BOUNDS,null);
        addressText.setAdapter(adapter);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placeLtG!=null)
                {
                    finalLatLng=placeLtG;
                }
                else
                {
                    finalLatLng=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                }
            }
        });

        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.sub_fragment_map, mapFragment).commit();
        return view;
    }

    private void hideSoftKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*
        --------------------------- google places API autocomplete suggestions@Credit- Youtube Channel: CodingWithMitch -----------------
     */

    private AdapterView.OnItemClickListener autoCompleteListen = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();
            originalMarker.remove();
            Log.d("DARN"," CONTROL GOT HERE");
            final AutocompletePrediction item = adapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){

                places.release();
                return;
            }
            final Place place = places.get(0);
            placeLtG=place.getLatLng();
            mGoogleMap.addMarker(new MarkerOptions().position(placeLtG).title("You are here currently"));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLtG,19),2000,null);
            places.release();
        }
    };

}