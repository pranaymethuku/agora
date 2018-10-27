package com.agora.android.agora;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class DashboardFragment extends Fragment {

    private TextView mWelcomeMessage;
    private GoogleSignInAccount mGoogleSignInAccount;
    private static String ARG_GOOGLE_SIGN_IN_ACCOUNT = "com.agora.android.agora.google_sign_in_account";

    public static Fragment newInstance(GoogleSignInAccount googleSignInAccount) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_GOOGLE_SIGN_IN_ACCOUNT, googleSignInAccount);

        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleSignInAccount = (GoogleSignInAccount) getArguments().getParcelable(ARG_GOOGLE_SIGN_IN_ACCOUNT);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mWelcomeMessage = (TextView) view.findViewById(R.id.welcome_message);
        mWelcomeMessage.setText(mGoogleSignInAccount.getDisplayName());

        return view;
    }
}
