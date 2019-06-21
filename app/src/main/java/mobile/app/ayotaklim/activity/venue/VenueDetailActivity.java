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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.admin.AddPerformerActivity;
import mobile.app.ayotaklim.activity.admin.AddVenueActivity;
import mobile.app.ayotaklim.activity.event.EventActivity;
import mobile.app.ayotaklim.activity.event.EventDetailActivity;
import mobile.app.ayotaklim.activity.performer.PerformerDetailActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.performer.Performer;
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
    RelativeLayout lihatKajianLayout,contentLocation,contentImamRutin,
            contentTakmir,contentJmlJamaah,contentPhone,contentDeskripsi,
            contentNoRek,contentNoPLN,contentNoPAM,contentMaps
    ;
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
    String flag;
    int record_id;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);
        progressBar = findViewById(R.id.progressBar);
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
        contentLocation = findViewById(R.id.contentLocation);
        contentImamRutin = findViewById(R.id.contentImamRutin);
        contentTakmir = findViewById(R.id.contentTakmir);
        contentJmlJamaah = findViewById(R.id.contentJmlJamaah);
        contentPhone = findViewById(R.id.contentPhone);
        contentDeskripsi = findViewById(R.id.contentDeskripsi);
        contentNoRek = findViewById(R.id.contentNoRek);
        contentNoPLN = findViewById(R.id.contentNoPLN);
        contentNoPAM = findViewById(R.id.contentNoPAM);
        contentMaps = findViewById(R.id.contentMaps);
        noRekBank = findViewById(R.id.noRek);
        noPln = findViewById(R.id.noPLN);
        noPam = findViewById(R.id.noPAM);
        imageVenue = findViewById(R.id.imageVenue);
        iconBack = findViewById(R.id.iconBack);

        flag = getIntent().getStringExtra("flag");
        if (flag.equalsIgnoreCase("FROM_EVENT")){
            record_id = getIntent().getIntExtra("c_venue_id",0);
            getDataById();
        }else {
            venue= (Venue) getIntent().getSerializableExtra("Venue");
            initView();
        }

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

    }

    private void initView(){
        if (flag.equalsIgnoreCase("FROM_EVENT")){
          lihatKajianLayout.setVisibility(View.GONE);
            lihatKajianLayout.setVisibility(View.GONE);
            contentImamRutin.setVisibility(View.GONE);
            contentJmlJamaah.setVisibility(View.GONE);
            contentNoPAM.setVisibility(View.GONE);
            contentNoPLN.setVisibility(View.GONE);
            contentNoRek.setVisibility(View.GONE);
            contentTakmir.setVisibility(View.GONE);
            contentPhone.setVisibility(View.GONE);
        }
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

    private void getDataById(){
        progressBar.setVisibility(View.VISIBLE);
        JSONObject dataJson  = new JSONObject();
        JSONObject memberJson  = new JSONObject();

        try {
            memberJson.put("record_id",record_id);
            dataJson.put("data", memberJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data req : ",dataJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Config.GET_VENUE_BY_ID, dataJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                Log.d("TAG VANUE",response.toString());
                Log.d("TAG","====================== SUCCESS ========================");
                try {
                    Boolean success  = response.getBoolean("success");
                    String message   = response.getString("message");
                    JSONObject data  = response.getJSONObject("data");
                    venue = new Venue();
                    venue.setId(data.getInt("id"));
                    venue.setNama(data.getString("nama"));
                    venue.setAlamat(data.getString("alamat"));
                    venue.setNoTlp(data.getString("no_tlp"));
                    venue.setLongitude(data.getDouble("longitude"));
                    venue.setLatitude(data.getDouble("latitude"));
                    venue.setNamaImamRutin(data.getString("nama_imam_rutin"));
                    venue.setJmlJamaah(data.getString("jumlah_jamaah_subuh"));
                    venue.setNoRek(data.getString("no_rek_bank"));
                    venue.setNoPln(data.getString("no_rek_pln"));
                    venue.setNoPam(data.getString("no_rek_pam"));
                    venue.setDkm(data.getString("dkm"));
                    venue.setEmail(data.getString("email"));
                    venue.setDkmPhone(data.getString("dkm_phone"));
                    venue.setImageVenue(data.getString("imagebase64"));
                    venue.setDeskripsi(data.getString("deskripsi"));
                    initView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "bearer "+sessionManager.getAksesToken());
                return headers;
            }
        };

        MyApplication.getInstance().addToRequestQueue(jsonRequest);

    }

    private void addMarker(LatLng latlng, final String title) {
        markerOptions.position(latlng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_loc_image));
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
