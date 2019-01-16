package mobile.app.ayotaklim.activity.venue;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.admin.AddPerformerActivity;
import mobile.app.ayotaklim.activity.admin.AddVenueActivity;
import mobile.app.ayotaklim.activity.event.EventActivity;
import mobile.app.ayotaklim.activity.event.EventDetailActivity;
import mobile.app.ayotaklim.activity.performer.PerformerDetailActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.ConvertImageBase64;

public class VenueDetailActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    Venue venue;
    ImageView imageVenue , btnEdit, iconBack;
    TextView venueName,
             venueAddress,
             dkmName,
             dkmPhone,
             phone,
             deskripsi,
             namaImamRutin,
             jumlahJamaahSubuh,
             noRekBank,
             noPln,
             noPam;
    RelativeLayout lihatKajianLayout;
    SessionManager sessionManager;
    Marker markers;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationManager locationManager;
    CameraPosition cameraPosition;
    MarkerOptions markerOptions;
    LatLng locationVenue;
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);
        venue= (Venue) getIntent().getSerializableExtra("Venue");
        sessionManager = new SessionManager(VenueDetailActivity.this);
        markerOptions = new MarkerOptions();
        venueName = findViewById(R.id.venueName);
        venueAddress = findViewById(R.id.venueAddress);
        dkmPhone = findViewById(R.id.phoneTakmir);
        dkmName =findViewById(R.id.takmirName);
        phone= findViewById(R.id.phone);
        deskripsi = findViewById(R.id.deskripsi);
        namaImamRutin = findViewById(R.id.namaImamRutin);
        jumlahJamaahSubuh = findViewById(R.id.jumlahJamaah);
        lihatKajianLayout = findViewById(R.id.lihatKajian);
        noRekBank = findViewById(R.id.noRek);
        noPln = findViewById(R.id.noPLN);
        noPam = findViewById(R.id.noPAM);
        imageVenue = findViewById(R.id.imageVenue);
        iconBack = findViewById(R.id.iconBack);
        venueName.setText(venue.getNama());
        venueAddress.setText(venue.getAlamat());
        dkmName.setText(venue.getDkm());
        dkmPhone.setText(venue.getDkmPhone());
        phone.setText(venue.getNoTlp());
        deskripsi.setText(venue.getDeskripsi());
        namaImamRutin.setText(venue.getNamaImamRutin());
        jumlahJamaahSubuh.setText(venue.getJmlJamaah()+" orang");
        noRekBank.setText(venue.getNoRek());
        noPln.setText(venue.getNoPln());
        noPam.setText(venue.getNoPam());
        Picasso.get()
                .load(Config.IMAGE_URL+venue.getImageVenue())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(imageVenue);
        if (sessionManager.isAdmin()) {
            btnEdit = findViewById(R.id.iconEdit);
            btnEdit.setVisibility(View.VISIBLE);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VenueDetailActivity.this, AddVenueActivity.class);
                    intent.putExtra("flag", "EDIT");
                    intent.putExtra("Venue", venue);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lihatKajianLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(VenueDetailActivity.this,EventDetailActivity.class);
                intent.putExtra("Venue", venue);
                startActivity(intent);
            }
        });
        loadMaps();
    }


    private void addMarker(LatLng latlng, final String title) {
        markerOptions.position(latlng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.masjid_ijo));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(10.0f);
        mMap.getUiSettings().isCompassEnabled();
        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        markers =  mMap.addMarker(markerOptions);
        markers.setTag(venue);

    }

    void  loadMaps(){
        FragmentManager fm = VenueDetailActivity.this.getSupportFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(VenueDetailActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager) VenueDetailActivity.this.getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(VenueDetailActivity.this);
       startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(VenueDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(VenueDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(VenueDetailActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            locationVenue = new LatLng(venue.getLatitude(), venue.getLongitude());
                            cameraPosition = new CameraPosition.Builder().target(locationVenue).zoom(10).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            addMarker(locationVenue,venue.getNama());
                        }
                    }
                });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        startLocationUpdates();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
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

}
