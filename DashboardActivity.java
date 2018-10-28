package com.agora.android.agora;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private TextView mWelcomeMessage;
    private Button mSignOutButton, mCheckButton;
    private String name;
    private String uniqueId;
    private String birthday;
    private static double currentLatitude=40.0057224;
    private static double currentLongitude=-83.01066909999997;

    private static double radius=5000;

    private LatLng location=new LatLng(20.0017814,-14.0172652);

    private static FirebaseFirestore db;

    private static final String EXTRA_GOOGLE_ACCOUNT = "com.agora.android.agora.google_account";
    private static final String EXTRA_SIGN_OUT = "com.agora.android.agora.sign_out";

    static ArrayList<String> idMatch=new ArrayList<>();
    static ArrayList<String> nameMatch=new ArrayList<>();

    static String interestToCheckFor="Eminem";

    public static boolean isSignOutRequested(Intent intent) {
        if (intent == null) {
            return false;
        }
        return intent.getBooleanExtra(EXTRA_SIGN_OUT, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bottom_nav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });

        GoogleSignInAccount gsiAccount = getIntent()
                .getParcelableExtra(EXTRA_GOOGLE_ACCOUNT);

        mWelcomeMessage = (TextView) findViewById(R.id.welcome_message);
        name=gsiAccount.getDisplayName()+"";
        uniqueId=gsiAccount.getId();
        mWelcomeMessage.setText(uniqueId+"");

        mCheckButton =(Button) findViewById(R.id.checkResult);

        mCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryFromDatabase();





            }
        });

        mSignOutButton = (Button) findViewById(R.id.button_sign_out);
        mSignOutButton.setOnClickListener(

                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
               //queryFromDatabase();
                ArrayList<String> Interests =new ArrayList<>();
                Interests.add("Lions");
                Interests.add("Gorilla");
                Interests.add("Harambe");
                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("Location",location.latitude+","+location.longitude);
                user.put("Interests", Interests);

                // Add a new document with a generated ID
                db.collection("users").document(uniqueId).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("LOL", "DocumentSnapshot added with ID: " + uniqueId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("LOL", "Error adding document", e);
                            }
                        });





                setSignOutRequested(true);
            }
        });



    }

    public static Intent newIntent(Context context, GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra(EXTRA_GOOGLE_ACCOUNT, googleSignInAccount);
        return intent;
    }


//    public void getQuery(String interestToCheckFor)
//    {
//        db = FirebaseFirestore.getInstance();
//
//        CollectionReference uses = db.collection("users");
//
//       Query queryInterests=uses.whereArrayContains("Interests",interestToCheckFor);
//       Log.d("BLAH BLAH",queryInterests.toString());
//
//       queryInterests.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//           @Override
//           public void onSuccess(@NonNull Task<QuerySnapshot> task) {
//               if (task.isSuccessful()) {
//                   Log.w("RAMBO","...");
//                   for (QueryDocumentSnapshot document : task.getResult()) {
//                       // Log.d("HAHA", document.getId() + " => " + document.getData());
//                       String Location=document.get("Location").toString();
//                       double getLatitude=Double.parseDouble(Location.substring(0,Location.indexOf(",")).trim());
//                       double getLongitude=Double.parseDouble(Location.substring(Location.indexOf(",")+1).trim());
//
//
//
//                       Location iterationU =new Location("");
//                       Location user=new Location("");
//                       iterationU.setLatitude(getLatitude);
//                       iterationU.setLongitude(getLongitude);
//                       user.setLatitude(currentLatitude);
//                       user.setLongitude(currentLongitude);
//
//                       double distance=user.distanceTo(iterationU);
//
//                       if(distance<=radius)
//                       {
//                           String documentId=document.getId();
//                           idMatch.add(documentId);
//                           String documentName=document.get("name").toString();
//                           nameMatch.add(documentName);
//                       }
//
//
//                   }
//               } else {
//                   Log.w("HAHA", "Error getting documents.", task.getException());
//               }
//           }
//       })    ;
//
//
//
//    }


    public void queryFromDatabase()
    {
        db = FirebaseFirestore.getInstance();



        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                Log.d("BARRY WAS HERE","..");

                                String Location=document.get("Location").toString();
                                double getLatitude=Double.parseDouble(Location.substring(0,Location.indexOf(",")).trim());
                                double getLongitude=Double.parseDouble(Location.substring(Location.indexOf(",")+1).trim());



                                Location iterationU =new Location("");
                                Location user=new Location("");
                                iterationU.setLatitude(getLatitude);
                                iterationU.setLongitude(getLongitude);
                                user.setLatitude(currentLatitude);
                                user.setLongitude(currentLongitude);

                                double distance=user.distanceTo(iterationU);
                                distance/=1000;
                                Log.d("HAHA", distance +"");
                                if(distance<=radius)
                                {

                                    ArrayList<String> InterestList=(ArrayList<String>)document.get("Interests");

                                    if(InterestList.contains(interestToCheckFor)) {


                                        String documentId = document.getId();
                                        Log.d("FOUND", documentId);
                                        DashboardActivity.idMatch.add(documentId);
                                        String documentName = document.get("name").toString();
                                        DashboardActivity.nameMatch.add(documentName);
                                    }
                                }



                                Log.d("HAHA", document.getId() + " => " + document.getData());

                                String stringLocation= document.get("Location")+"";

                                Log.d("LOCATION_ZOMBIE", stringLocation);


                            }

                            Intent result=new Intent(getApplicationContext(),Result.class);

                            startActivity(result);

                        } else {
                            Log.w("HAHA", "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void setSignOutRequested(boolean isSignOutRequested) {
        Intent data = new Intent();
        data.putExtra(EXTRA_SIGN_OUT, isSignOutRequested);
        setResult(RESULT_OK, data);
    }
}
