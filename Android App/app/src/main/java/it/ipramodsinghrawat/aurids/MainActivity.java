package it.ipramodsinghrawat.aurids;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends SuperDrawerActivity{

    //FirebaseStorage storage;// = FirebaseStorage.getInstance();
    //ImageView fGImageView;

    private List<IntrusionNotification> intrusionNotificationList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotificationAdaptor nAdapter;

    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        nAdapter =  new NotificationAdaptor(intrusionNotificationList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(nAdapter);

        //prepareNotificationListDataFromFireBase();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            authenticateUser();
        }else{
            Log.v("FirebaseDataUser", "FB User is: " + user.toString());
            prepareNotificationListDataFromFireBase();
        }
        //*/
        //Log.v("FirebaseDataUser", "FB User is: " + user.toString());


        //prepareNotificationListDataFromFireBase();

        //fGImageView = findViewById(R.id.fGImageView);

        //storage = FirebaseStorage.getInstance();

        //getImageFromFireBase(); //working
        //getFCMTOken(); //working
        //getFireBaseData();
    }

    private void authenticateUser() {

        List<AuthUI.IdpConfig> providers = new ArrayList<>();

        providers.add(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());


        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                REQUEST_CODE);
    }

    public void prepareNotificationListDataFromFireBase(){
        Toast.makeText(this, "Loading ... ", Toast.LENGTH_SHORT).show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("notifications");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {//.orderByKey()
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                intrusionNotificationList.clear();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                //Log.d("FirebaseData", "Value is: " + value);

                Log.d("FirebaseData", "Value is: " + dataSnapshot.toString());

                long countDataSnapshot = dataSnapshot.getChildrenCount();

                Log.d("FirebaseDataTotal", "Total countdataSnapshot: " + countDataSnapshot);

                for (DataSnapshot alert: dataSnapshot.getChildren()) {

                    Log.d("FirebaseData2", "Value is: " + alert.toString());
                    //Log.d("FirebaseData3", "Key is: " + alert.getKey());

                    //Log.d("FirebaseData3", "Ref is: " + alert.getRef());
                    String nKey = alert.getKey().toString();
                    String fileRef = alert.child("face_file").getValue().toString();
                    String fileFrameRef = alert.child("face_frame_file").getValue().toString();
                    String detailVal = alert.child("details").getValue().toString();
                    String notificationVal = alert.child("notification").getValue().toString();
                    String timesampVal = alert.child("timestamp").getValue().toString();

                    //long timesampValLng = Long.valueOf(timesampVal);

                    //long timesampValLng = 123456789;//testing

                    double timesampValLng = Double.valueOf(alert.child("timestamp").getValue(Double.class));

                    //Log.d("FirebaseData3", "fileRef is: " + fileRef);
                    //Log.d("FirebaseData3", "detailVal is: " + detailVal);
                    //Log.d("FirebaseData3", "notificationVal is: " + notificationVal);
                    //Log.d("FirebaseData3", "timesampVal is: " + timesampValLng);

                    /*
                    if (alert.child("timestamp").exists()) {
                        // run some code
                        String timesampVal = alert.child("timestamp").getValue().toString();
                        Log.d("FirebaseData3", "timestamp is: " + timesampVal);

                        //long timesampValLng = Long.valueOf(timesampVal);
                        //Log.d("FirebaseData3", "Java timestamp is: " + (timesampValLng*1000));
                    }*/

                    //getImageFromFireBaseV2(fileRef);

                    IntrusionNotification intrusionNotification = new IntrusionNotification(nKey,
                                                                                    detailVal,
                                                                                    fileRef,
                                                                                    fileFrameRef,
                                                                                    notificationVal,
                            getDateCurrentTimeZone((long) (timesampValLng)));

                    intrusionNotificationList.add(intrusionNotification);
                    //intrusionNotificationList.
                    //Collections.reverse(intrusionNotificationList);
                }
                Collections.reverse(intrusionNotificationList);

                nAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FirebaseDataError", "Failed to read value.", error.toException());
            }
        });

    }

    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    public void getFCMTOken(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        String fcmToken= pref.getString("regId", "0");
        Log.v("fcmToken",fcmToken);
    }

    // working
/*    public void getImageFromFireBase(){

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        StorageReference islandRef = storageRef.child("image_rec/unknown_image.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Log.e("onSuccess","onSuccess Called ");

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                fGImageView.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("onFailure",exception.toString());
            }
        });

    }
    */
/*
    // working
    public void getImageFromFireBaseV2(String filerefs){

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        StorageReference islandRef = storageRef.child(filerefs);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Log.e("onSuccess","onSuccess Called ");

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                fGImageView.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("onFailure",exception.toString());
            }
        });

    }
*/

    public void getFireBaseData(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("notifications");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                //Log.d("FirebaseData", "Value is: " + value);

                Log.d("FirebaseData", "Value is: " + dataSnapshot.toString());

                long countDataSnapshot = dataSnapshot.getChildrenCount();

                Log.d("FirebaseDataTotal", "Total countdataSnapshot: " + countDataSnapshot);

                for (DataSnapshot alert: dataSnapshot.getChildren()) {

                    Log.d("FirebaseData2", "Value is: " + alert.toString());
                    Log.d("FirebaseData3", "Key is: " + alert.getKey());

                    //Log.d("FirebaseData3", "Ref is: " + alert.getRef());

                    String fileRef = alert.child("face_file").getValue().toString();
                    String detailVal = alert.child("details").getValue().toString();
                    String notificationVal = alert.child("notification").getValue().toString();

                    Log.d("FirebaseData3", "fileRef is: " + fileRef);
                    Log.d("FirebaseData3", "detailVal is: " + detailVal);
                    Log.d("FirebaseData3", "notificationVal is: " + notificationVal);

                    if (alert.child("timestamp").exists()) {
                        // run some code
                        String timesampVal = alert.child("timestamp").getValue().toString();
                        Log.d("FirebaseData3", "timestamp is: " + timesampVal);

                        //long timesampValLng = Long.valueOf(timesampVal);
                        //Log.d("FirebaseData3", "Java timestamp is: " + (timesampValLng*1000));
                    }

                    //getImageFromFireBaseV2(fileRef);

                    /*
                    System.out.println(alert.child("date").getValue());
                    System.out.println(alert.child("message").getValue());

                    for (DataSnapshot recipient: alert.child("recipients").getChildren()) {
                        System.out.println(recipient.child("name").getValue());
                    }
                    */

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FirebaseDataError", "Failed to read value.", error.toException());
            }
        });

    }
}
