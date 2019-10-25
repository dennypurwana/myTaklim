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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.admin.AdminLoginActivity;
import mobile.app.ayotaklim.activity.performer.PerformerListActivity;
import mobile.app.ayotaklim.activity.reminder.ReminderListActivity;
import mobile.app.ayotaklim.activity.venue.VenueListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.venue.Venue;

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
    LinearLayout menuEvent, menuVenue, menuUstadz, menuReminder, menuDonasi, menuLogin;
    RelativeLayout adminLogin, adminLogout;
    MarkerOptions markerOptions;
    SliderView sliderView;
    SessionManager sessionManager;
    Marker markers;
    boolean isValidName = false;
    boolean isValidAddress = false;
    ArrayList<Venue> venueArrayList;
    private static final int PERMISSION_REQUEST_CODE = 1;
    EditText edTextSearch;
    ImageView iconSearch;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        edTextSearch = findViewById(R.id.edTextSearch);
        iconSearch = findViewById(R.id.iconSearch);
        menuEvent = findViewById(R.id.menuEvent);
        menuVenue = findViewById(R.id.menuVenue);
        menuUstadz = findViewById(R.id.menuUstadz);
        menuReminder = findViewById(R.id.menuReminder);
        menuDonasi = findViewById(R.id.menuDonasi);
        menuLogin = findViewById(R.id.menuLogin);
        adminLogin = findViewById(R.id.adminLogin);
        adminLogout = findViewById(R.id.adminLogout);
        markerOptions = new MarkerOptions();
        sessionManager = new SessionManager(EventActivity.this);
        // sliderView = new SliderView(EventActivity.this);
        progressBar = findViewById(R.id.progressBar);
        setProgressBarIndeterminateVisibility(true);

        requestPermission();

        /*int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermission();
        }else{
            statusCheck();
            if(isGooglePlayServicesAvailable()){
                loadMaps();
            }
            else {
                Toast.makeText(getApplicationContext(),"google play services is not available",Toast.LENGTH_LONG).show();
            }
        }
        */

        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = edTextSearch.getText().toString();
                getDataVenue("search",key);
            }
        });
        // initSlider();
        menuEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, EventListActivity.class);
                startActivity(intent);
            }
        });

        menuVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, VenueListActivity.class);
                startActivity(intent);
            }
        });

        menuUstadz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, PerformerListActivity.class);
                startActivity(intent);
            }
        });

        menuReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sessionManager.isAdmin()) {
                    Intent intent = new Intent(EventActivity.this, ReminderListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Menu hanya bisa di akses oleh user.", Toast.LENGTH_LONG).show();
                }
            }
        });

        menuDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventActivity.this,DonasiActivity.class);
                startActivity(intent);
                     }
        });

        if (sessionManager.isAdmin()) {
            adminLogin.setVisibility(View.GONE);
            adminLogout.setVisibility(View.VISIBLE);
            adminLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionManager.logoutAdminSession();
                    finish();
                    System.exit(0);
                }
            });

        } else {
            adminLogin.setVisibility(View.VISIBLE);
            adminLogout.setVisibility(View.GONE);
            adminLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventActivity.this, AdminLoginActivity.class);
                    startActivity(intent);
                }
            });
        }


    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.d("cek", "true");
            return true;
        } else {
            Log.d("cek", "false");
            return false;
        }
    }

    private void addMarker(LatLng latlng, final String title, final Venue venue) {
        markerOptions.position(latlng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.masjid_ijo));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(10.0f);
        mMap.getUiSettings().isCompassEnabled();
        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        markers = mMap.addMarker(markerOptions);
        markers.setTag(venue);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Venue venueTag = ((Venue) markers.getTag());
                Intent intent = new Intent(EventActivity.this, EventDetailActivity.class);
                intent.putExtra("Venue", venueTag);
                startActivity(intent);
            }
        });
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EventActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getApplicationContext(), "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            showAlert();
        } else {
            ActivityCompat.requestPermissions(EventActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

        }
    }

    void getDataVenue(final String filter,final String key) {
        progressBar.setVisibility(View.VISIBLE);
        String url = Config.GET_DATA_EVENT_HOME;
        Log.d("API : ", url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                if (!response.equals(null)) {
                    Log.e("TAG", "ALL EVENT: " + response.toString());
                    try {
                        venueArrayList = new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray venueResponse = responseObj.getJSONArray("venue");
                        if (venueResponse.length() > 0) {

                            for (int i = 0; i < venueResponse.length(); i++) {
                                isValidAddress = false;
                                isValidName = false;
                                try {
                                    JSONObject jsonObject = venueResponse.getJSONObject(i);
                                    Venue venue = new Venue();
                                    venue.setId(jsonObject.getInt("id"));
                                    venue.setNama(jsonObject.getString("nama"));
                                    venue.setAlamat(jsonObject.getString("alamat"));
                                    venue.setLongitude(jsonObject.getDouble("longitude"));
                                    venue.setLatitude(jsonObject.getDouble("latitude"));
                                    venue.setImageVenue(jsonObject.getString("imagebase64"));
                                    if (filter.toLowerCase().equalsIgnoreCase("search")){

                                        if(venue.getNama().replace(" ","").toLowerCase().indexOf(key.replace(" ","").toLowerCase()) != -1) {
                                            isValidName = true;
                                        }
                                        else if(venue.getAlamat().replace(" ","").toLowerCase().indexOf(key.replace(" ","").toLowerCase()) != -1) {
                                            isValidAddress = true;
                                        }
                                        else if(venue.getNama().toLowerCase().equalsIgnoreCase(key.toLowerCase())){
                                            isValidName = true;
                                        }
                                        else if(venue.getAlamat().toLowerCase().equalsIgnoreCase(key.toLowerCase())){
                                            isValidAddress = true;
                                        }

                                        if (isValidName||isValidAddress){
                                            venueArrayList.add(venue);
                                        }

                                    }else{
                                        venueArrayList.add(venue);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                            if (filter.toLowerCase().equalsIgnoreCase("search")){
                                initMarker("search");
                            }else{
                                initMarker("");
                            }

                        } else {

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
                progressBar.setVisibility(View.GONE);
                Log.e("error is ", "" + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "bearer " + sessionManager.getAksesToken());
                return params;
            }

        };
        MyApplication.getInstance().addToRequestQueue(request);
    }


    void initMarker(String filter) {
        mMap.clear();
        for (final Venue venue : venueArrayList) {
            markerOptions.position(new LatLng(venue.getLatitude(), venue.getLongitude()));
            markerOptions.title(venue.getNama());
            markerOptions.snippet(venue.getAlamat());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_loc_image));
            if (filter.toLowerCase().equalsIgnoreCase("search")){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(venue.getLatitude(), venue.getLongitude())));
            }
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMinZoomPreference(10.0f);
            mMap.getUiSettings().isCompassEnabled();
            mMap.getUiSettings().isZoomControlsEnabled();
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            markers = mMap.addMarker(markerOptions);
            markers.setTag(venue);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Venue venueTag = ((Venue) marker.getTag());
                    Intent intent = new Intent(EventActivity.this, EventDetailActivity.class);
                    intent.putExtra("Venue", venueTag);
                    startActivity(intent);

                }
            });
        }
    }

    void loadMaps() {
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
        startLocationUpdates();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {


        if (requestCode == PERMISSION_REQUEST_CODE) {
            loadMaps();
        } else {
            loadMaps();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                    getDataVenue("ALL","");
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
        try {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }catch (Exception e){

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
                        startActivityForResult(myIntent,2);
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




    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
                }
            });

            sliderLayout.addSliderView(sliderView);
        }

    }


}
