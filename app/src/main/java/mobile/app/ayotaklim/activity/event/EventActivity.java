package mobile.app.ayotaklim.activity.event;

import android.Manifest;
import android.app.ProgressDialog;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import com.google.android.gms.maps.model.CameraPosition;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.performer.PerformerListActivity;
import mobile.app.ayotaklim.activity.reminder.ReminderListActivity;
import mobile.app.ayotaklim.activity.venue.VenueListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.CheckDistanceLocations;

public class EventActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationManager locationManager;
    private LatLng currentLocation, eventLocation;
    private boolean isPermission;
    private FusedLocationProviderClient mFusedLocationClient;
    SliderLayout sliderLayout;
    CameraPosition cameraPosition;
    LinearLayout menuEvent, menuVenue , menuUstadz, menuReminder ;
    MarkerOptions markerOptions;
    SliderView sliderView;
    SessionManager sessionManager;
    Marker markers;
   ArrayList<Venue> venueArrayList;
    Venue venue ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        menuEvent = findViewById(R.id.menuEvent);
        menuVenue = findViewById(R.id.menuVenue);
        menuUstadz = findViewById(R.id.menuUstadz);
        menuReminder = findViewById(R.id.menuReminder);
        markerOptions = new MarkerOptions();
        venue = new Venue();
        sessionManager = new SessionManager(EventActivity.this);
        sliderView = new SliderView(EventActivity.this);
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


    }

    private void addMarker(LatLng latlng, final String title, final Venue venue ) {


        markerOptions.position(latlng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_masjid));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(10.0f);
        mMap.getUiSettings().isCompassEnabled();
        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
         markers =  mMap.addMarker(markerOptions);
         markers.setTag(venue);

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Venue venueTag = ((Venue) markers.getTag());
                Intent intent=new Intent(EventActivity.this,EventDetailActivity.class);
                intent.putExtra("Venue", venueTag);
                startActivity(intent);
            }
        });
    }

    private void getEvent() {
       /* final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url=Config.GET_EVENT;
        Log.d("API : ",url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                        try {
                            JSONArray vResponse=response.getJSONArray("event");
                            if(vResponse.length()>0) {
                                for (int i = 0; i < vResponse.length(); i++) {
                                    try {

                                        JSONObject jsonObject = vResponse.getJSONObject(i);
                                        Event event = new Event();
                                        event.setTitle(jsonObject.getString("nama"));
                                        event.setTopic(jsonObject.getString("topik"));
                                        event.setPerformer(jsonObject.getString("pemateri"));
                                        event.setDate(jsonObject.getString("tanggal"));
                                        event.setStartTime(jsonObject.getString("waktu_mulai"));
                                        event.setEndTime(jsonObject.getString("waktu_selesai"));
                                        event.setVenue(jsonObject.getString("venue"));
                                        event.setVenueAddress(jsonObject.getString("alamat_venue"));
                                        event.setLongitude(jsonObject.getDouble("longitude"));
                                        event.setLatitude(jsonObject.getDouble("latitude"));
                                        event.setImageBase64(jsonObject.getString("imageBase64"));
                                        event.setDescription(jsonObject.getString("deskripsi"));
                                        eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
                                       if((CheckDistanceLocations.CalculationByDistance(currentLocation,eventLocation))<30.0){
                                           addMarker(eventLocation, event.getTitle(), event);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                    }
                                }

                            }else{
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
        */

    }

    void getDataVenue(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url=Config.GET_VENUE;
        Log.d("API : ",url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("response venus : " , response);
                if (!response.equals(null)) {
                    progressDialog.hide();
                    Log.e("TAG", "produk response: " + response.toString());
                    try {
                        venueArrayList=new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("items");
                        if(vResponse.length()>0) {

                            for (int i = 0; i < vResponse.length(); i++) {
                                try {

                                    JSONObject jsonObject = vResponse.getJSONObject(i);

                                    venue.setId(jsonObject.getInt("id"));
                                    venue.setNama(jsonObject.getString("nama"));
                                    venue.setAlamat(jsonObject.getString("alamat"));
                                    venue.setNoTlp(jsonObject.getString("venue_phone"));
                                    venue.setLongitude(jsonObject.getDouble("longitude"));
                                    venue.setLatitude(jsonObject.getDouble("latitude"));
                                    venue.setDkm(jsonObject.getString("dkm"));
                                    venue.setDkmPhone(jsonObject.getString("dkm_phone"));
                                    venue.setImageVenue(jsonObject.getString("imagebase64"));
                                    venue.setDeskripsi(jsonObject.getString("deskripsi"));
//                                    eventLocation = new LatLng(venue.getLatitude(), venue.getLongitude());
//                                    if((CheckDistanceLocations.CalculationByDistance(currentLocation,eventLocation))<100.0){
//                                        addMarker(eventLocation, venue.getNama(), venue );
//                                    }

                                    progressDialog.dismiss();
                                    venueArrayList.add(venue);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                            }
                            initMarker();

                        }else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e("error is ", "" + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "bearer "+sessionManager.getAksesToken());
                return params;
            }

        };
        MyApplication.getInstance().addToRequestQueue(request);
    }


    void initMarker(){
        Log.d("size venue",String.valueOf(venueArrayList.size()));
        for (final Venue venue : venueArrayList) {
            Log.d("venue nama ", venue.getNama());
            markerOptions.position(new LatLng(venue.getLatitude(),venue.getLongitude()));
            markerOptions.title(venue.getNama());
            markerOptions.snippet(venue.getAlamat());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_masjid));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMinZoomPreference(10.0f);
            mMap.getUiSettings().isCompassEnabled();
            mMap.getUiSettings().isZoomControlsEnabled();
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            markers =  mMap.addMarker(markerOptions);
            markers.setTag(venue);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Venue venueTag = ((Venue) marker.getTag());
                    Intent intent = new Intent(EventActivity.this, EventDetailActivity.class);
                    intent.putExtra("nama", venue.getNama());
                    startActivity(intent);

                }
            });
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

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(EventActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EventActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(EventActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(10).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                @Override
                                public void onMapLoaded() {
                                    getDataVenue();
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
                break;
                case 1:
                    sliderView.setImageDrawable(R.drawable.ustadz_uas);
                 break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.ustadz_zaki);
            break;
                case 3:
                    sliderView.setImageDrawable(R.drawable.image_ustadz);
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

            sliderLayout.addSliderView(sliderView);
        }

    }

}
