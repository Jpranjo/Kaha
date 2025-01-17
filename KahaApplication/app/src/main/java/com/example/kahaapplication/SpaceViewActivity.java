package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SpaceViewActivity extends ToolBarActivity implements OnMapReadyCallback{
    private ImageView ivThumbnail;

    private TextView tvSize;
    private TextView tvPrice;
    private TextView tvHost;
    private TextView tvType;
    private TextView tvTitle;
    private MapView mapView;
    private AppCompatButton btnReserve;
    private AppCompatButton btnEdit;
    private AppCompatButton btnDelete;
    private TextView tvPerMonth;
    private TextView tvProfileHeader;
    private ImageView ivHostImage;
    private LinearLayout llPrice;
    private View vPriceDivider;

    private AppCompatButton btnContact;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    //Firebase Vars
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_view);

        initToolbar();
        this.initFirebase();

        this.ivThumbnail = findViewById(R.id.iv_thumb);

        this.tvSize = findViewById(R.id.tv_show_size);
        this.tvPrice = findViewById(R.id.tv_show_price);
        this.tvHost = findViewById(R.id.tv_show_hoster_name);
        this.tvType = findViewById(R.id.tv_show_type);
        this.tvTitle = findViewById(R.id.tv_title);
        this.tvPerMonth = findViewById(R.id.tv_show_price_month);
        this.tvProfileHeader = findViewById(R.id.tv_profile_header);

        this.ivHostImage = findViewById(R.id.iv_space_hoster);

        this.llPrice = findViewById(R.id.ll_price);

        this.vPriceDivider = findViewById(R.id.divider_price);

        this.mapView = findViewById(R.id.mv_show_location);

        this.btnContact = findViewById(R.id.btn_space_contact);
        this.btnReserve = findViewById(R.id.btn_reserve);
        this.btnEdit = findViewById(R.id.btn_edit);
        this.btnDelete = findViewById(R.id.btn_delete);


        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpaceViewActivity.this, PrivateUserActivity.class);
                startActivity(intent);
            }
        });


        initMap(savedInstanceState);
        retrieveData();

    }

    private void initMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if(savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        this.mapView = findViewById(R.id.mv_show_location);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }

    private void retrieveData() {
        Intent i = getIntent();
        int iThumbnail = i.getIntExtra(Keys.KEY_SPACE_THUMBNAIL.name(), 0);
        this.ivThumbnail.setImageResource(iThumbnail);

        float sLength = i.getFloatExtra(Keys.KEY_SPACE_LENGTH.name(), 0);
        float sWidth = i.getFloatExtra(Keys.KEY_SPACE_WIDTH.name(), 0);
        float sHeight = i.getFloatExtra(Keys.KEY_SPACE_HEIGHT.name(), 0);

        this.tvSize.setText(sLength + " x " + sWidth + " x " + sHeight);

        float sPrice = i.getFloatExtra(Keys.KEY_SPACE_PRICE.name(), 0);
        this.tvPrice.setText("₱" + sPrice);

        String sHost = i.getStringExtra(Keys.KEY_SPACE_HOST.name());
        this.tvHost.setText(sHost);
        this.btnContact.setText("Contact " + sHost);

        String sType = i.getStringExtra(Keys.KEY_SPACE_TYPE.name());
        this.tvType.setText(sType);
        String sLocation = i.getStringExtra(Keys.KEY_SPACE_LOCATION.name());
        this.tvTitle.setText(sType + " in " + sLocation);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    private void setViews(String isFinder) {
        this.btnDelete.setVisibility(View.GONE);
        this.btnEdit.setVisibility(View.GONE);
        this.llPrice.setVisibility(View.GONE);
        this.vPriceDivider.setVisibility(View.GONE);

        if(isFinder.equalsIgnoreCase("false")) {
            this.tvPerMonth.setVisibility(View.GONE);
            this.tvPrice.setVisibility(View.GONE);
            this.tvHost.setVisibility(View.GONE);
            this.tvProfileHeader.setVisibility(View.GONE);
            this.ivHostImage.setVisibility(View.GONE);
            this.btnContact.setVisibility(View.GONE);
            this.btnReserve.setVisibility(View.GONE);

            this.btnDelete.setVisibility(View.VISIBLE);
            this.btnEdit.setVisibility(View.VISIBLE);
            this.llPrice.setVisibility(View.VISIBLE);
            this.vPriceDivider.setVisibility(View.VISIBLE);
        }

    }


    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name());

        //this.pbProfile.setVisibility(View.VISIBLE);
        reference.child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                setViews(snapshot.child("userIsFinder").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //pbProfile.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}