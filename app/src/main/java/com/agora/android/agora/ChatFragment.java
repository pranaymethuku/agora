package com.agora.android.agora;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;


public class ChatFragment extends Fragment {
    private static String ARG_GOOGLE_SIGN_IN_ACCOUNT = "com.agora.android.agora.google_sign_in_account";

    private GoogleSignInAccount mGoogleSignInAccount;

    private String mChannelID = "com.agora.android.agora.channel_id";
    private String mRoomName = "com.agora.android.agora.observable_room";
    private EditText mMessageInput;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGoogleSignInAccount = getArguments().getParcelable(ARG_GOOGLE_SIGN_IN_ACCOUNT);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mMessageInput = (EditText) view.findViewById(R.id.message_input);

        return view;
    }
}
