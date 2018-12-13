package mobile.app.ayotaklim.activity.event;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.performer.PerformerListActivity;
import mobile.app.ayotaklim.activity.reminder.ReminderListActivity;
import mobile.app.ayotaklim.activity.venue.VenueListActivity;
import mobile.app.ayotaklim.models.event.Event;

public class EventActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private Context mContext;
    private SupportMapFragment supportMapFragment;
    private MarkerOptions currentPositionMarker = null;
    private Marker currentLocationMarker;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 1* 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 20 sec */
    private LocationManager locationManager;
    private LatLng latLng;
    private boolean isPermission;
    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private ArrayList<Event> eventArrayList;
    private FusedLocationProviderClient mFusedLocationClient;
    SliderLayout sliderLayout;
    LinearLayout menuEvent, menuVenue , menuUstadz, menuReminder ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        menuEvent = findViewById(R.id.menuEvent);
        menuVenue = findViewById(R.id.menuVenue);
        menuUstadz = findViewById(R.id.menuUstadz);
        menuReminder = findViewById(R.id.menuReminder);
        loadMaps();
        initSlider();

        menuEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventActivity.this,EventListActivity.class);
                startActivity(intent);
            }
        });

        menuVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventActivity.this,VenueListActivity.class);
                startActivity(intent);
            }
        });

        menuUstadz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventActivity.this,PerformerListActivity.class);
                startActivity(intent);
            }
        });

        menuReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventActivity.this,ReminderListActivity.class);
                startActivity(intent);
            }
        });


        //addDataEvent();
        //initEvent();
    }
    void initSlider(){
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(1); //set scroll delay in seconds :

        setSliderViews();
    }

    private void setSliderViews() {

        for (int i = 0; i <= 3; i++) {

            SliderView sliderView = new SliderView(EventActivity.this);

            switch (i) {
                case 0:
                    sliderView.setImageDrawable(R.drawable.ustadz_hanan);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/547114/pexels-photo-547114.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 1:
                    sliderView.setImageDrawable(R.drawable.ustadz_uas);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.ustadz_zaki);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
                    break;
                case 3:
                    sliderView.setImageDrawable(R.drawable.image_ustadz);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderView.setDescription("Kajian Ustadz Abdul Somad " + (i + 1));
            final int finalI = i;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    Toast.makeText(getApplicationContext(), "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();
                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }

    }

    void loadMaps(){
        if (requestSinglePermission()) {

            FragmentManager fm = EventActivity.this.getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
            if (supportMapFragment == null) {
                supportMapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
            }

            supportMapFragment.getMapAsync(this);

            mGoogleApiClient = new GoogleApiClient.Builder(EventActivity.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationManager = (LocationManager) EventActivity.this.getSystemService(Context.LOCATION_SERVICE);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(EventActivity.this);
        }
    }
    /*
    void initEvent(){
        recyclerView = (RecyclerView) EventActivity.this.findViewById(R.id.recycler_view_event);

        adapter = new EventListAdapter(eventArrayList, new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Intent intent=new Intent(EventActivity.this,EventDetailActivity.class);
                intent.putExtra("Event", event);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }
    void addDataEvent(){
        eventArrayList = new ArrayList<>();
        eventArrayList.add(new Event("Bila Anda Dijauhi Manusia", "Masjid Al-Hidayah", "Alamat: no, Jl. Tomang Tinggi No.3B, RT.13/RW.6, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440", "topic 1", "addars", "dasdsa", "20-11-2019", "20.00", "22.00"));
        eventArrayList.add(new Event("Ghibah yang Dibolehkan", "Masjid Al-Mumin", "Alamat: no, Jl. Tomang Tinggi No.3B, RT.13/RW.6, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440", "topic 1", "addars", "dasdsa", "20-11-2019", "20.00", "22.00"));
        eventArrayList.add(new Event("#2019 Pemilihan Presiden: Dalil Bolehnya Mencoblos dalam Pemilu – DR Firanda Andirja MA", "Masjid Al-Mughni", "Alamat: no, Jl. Tomang Tinggi No.3B, RT.13/RW.6, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440", "topic 1", "addars", "dasdsa", "20-11-2019", "20.00", "22.00"));
        eventArrayList.add(new Event("Maha Benar Netizen dengan Segala Komentarnya”, Ucapan Kufur?", "Jakarta Convention Center", "Alamat: no, Jl. Tomang Tinggi No.3B, RT.13/RW.6, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440", "topic 1", "addars", "dasdsa", "20-11-2019", "20.00", "22.00"));
    }
    */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(EventActivity.this,"on map ready",Toast.LENGTH_LONG).show();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        /*if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }



        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(getActivity(), "Location not Detected", Toast.LENGTH_SHORT).show();
        }
        */
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("tag", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("tasg", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {

       /*
        saat user pindah lokasi

       String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        FragmentManager fm = getActivity().getSupportFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            Toast.makeText(getActivity(), "masuk fragment null", Toast.LENGTH_LONG).show();
            //Log.d("mas","masuk");
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
        */
    }

    protected void startLocationUpdates() {
/*
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("request", "--->>>>");
                */
        if (ActivityCompat.checkSelfPermission(EventActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EventActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(EventActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            Toast.makeText(EventActivity.this,"Fused Location enable, Location : "+String.valueOf(location.getLatitude()+ " , "+location.getLongitude()),Toast.LENGTH_LONG).show();

                            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                @Override
                                public void onMapLoaded() {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_masjid)).position(latLng).title("Kajian Ustadz Adi Hidayat"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.getUiSettings().setZoomControlsEnabled(true);
                                    mMap.setMinZoomPreference(15.0f);
                                }
                            });
                        }
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(EventActivity.this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) EventActivity.this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean requestSinglePermission() {

        Dexter.withActivity(EventActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //Single Permission is granted
                        Toast.makeText(EventActivity.this, "Permission is granted!", Toast.LENGTH_SHORT).show();
                        isPermission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            isPermission = false;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return isPermission;

    }

}
