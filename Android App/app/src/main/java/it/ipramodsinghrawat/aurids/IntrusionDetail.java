package it.ipramodsinghrawat.aurids;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class IntrusionDetail extends SuperDrawerActivity{

    ImageView fGImageView,fGImageView2;
    FirebaseStorage storage;// = FirebaseStorage.getInstance();

    TextView ntfcTV,ntfcDtlTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrusion_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fGImageView = findViewById(R.id.fGImageView);
        fGImageView2 = findViewById(R.id.fGImageView2);

        ntfcTV = findViewById(R.id.ntfcTV);
        ntfcDtlTV = findViewById(R.id.ntfcDtlTV);

        String key = getIntent().getStringExtra("key");
        String details = getIntent().getStringExtra("details");
        String faceFile = getIntent().getStringExtra("faceFile");
        String faceFrameFile = getIntent().getStringExtra("faceFrameFile");
        String notification = getIntent().getStringExtra("notification");
        String timestamp = getIntent().getStringExtra("timestamp");

        Toast.makeText(this, "details = " + details, Toast.LENGTH_SHORT).show();
        storage = FirebaseStorage.getInstance();

        ntfcTV.setText(notification);
        ntfcDtlTV.setText(details+" \n - "+timestamp);

        getImageFromFireBaseV2(faceFile,fGImageView);
        getImageFromFireBaseV2(faceFrameFile,fGImageView2);

    }

    // working
    public void getImageFromFireBaseV2(String filerefs, final ImageView imageView){

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
                imageView.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("onFailure",exception.toString());
            }
        });

    }
}
