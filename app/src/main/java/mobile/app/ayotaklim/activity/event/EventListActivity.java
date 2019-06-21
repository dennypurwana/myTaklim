package mobile.app.ayotaklim.activity.event;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.admin.AddEventActivity;
import mobile.app.ayotaklim.activity.admin.AddPerformerActivity;
import mobile.app.ayotaklim.activity.performer.PerformerListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.FormatTanggalIDN;

public class EventListActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private ArrayList<Event> eventArrayList;
    SessionManager sessionManager;
    ProgressBar progressBar;
    Button btnAdd;
    boolean isValidEvent = false;
    boolean isValidUstadz = false;
    boolean isValidAddress = false;
    boolean isValidVenue = false;
    EditText edTextSearch;
    ImageView iconSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        edTextSearch = findViewById(R.id.edTextSearch);
        iconSearch = findViewById(R.id.iconSearch);
        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                .permitDiskWrites()
                .build());
        StrictMode.setThreadPolicy(old);
        sessionManager= new SessionManager(EventListActivity.this);
        progressBar = findViewById(R.id.progressBar);
        setProgressBarIndeterminateVisibility(true);

        if (sessionManager.isAdmin()) {

            btnAdd = findViewById(R.id.btnAdd);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventListActivity.this, AddEventActivity.class);
                    intent.putExtra("flag", "ADD");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            btnAdd.setVisibility(View.VISIBLE);
        }

        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = edTextSearch.getText().toString();
                getDataEvent("SEARCH",key);
            }
        });
        getDataEvent("ALL","");
    }


    void initEvent(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_event);
        recyclerView.setVisibility(View.VISIBLE);
        adapter = new EventListAdapter("event",eventArrayList, new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {

                Intent intent = new Intent(EventListActivity.this, EventJadwalActivity.class);
                intent.putExtra("Event", event);
                startActivity(intent);
            }

        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    void getDataEvent(final String filter, final  String key){
        progressBar.setVisibility(View.VISIBLE);
        String url=Config.GET_DATA_EVENT_HOME;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                if (!response.equals(null)) {

                    try {
                        eventArrayList=new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray venueResponse=responseObj.getJSONArray("event");
                        if(venueResponse.length()>0) {

                            for (int i = 0; i < venueResponse.length(); i++) {
                                try {
                                     isValidEvent = false;
                                     isValidUstadz = false;
                                     isValidAddress = false;
                                     isValidVenue = false;
                                    JSONObject jsonObject = venueResponse.getJSONObject(i);

                                    Event event = new Event();
                                    event.setId(jsonObject.getInt("id"));
                                    event.setNamaEvent(jsonObject.getString("nama_event"));
                                    event.setAlamatVenue(jsonObject.getString("alamat"));
                                    event.setNamaVenue(jsonObject.getString("nama"));
                                    event.setVenueId(jsonObject.getInt("c_venue_id"));
                                    event.setPerformerId(jsonObject.getInt("c_pemateri_id"));
                                    event.setBannerImage(jsonObject.getString("banner_image"));
                                    event.setTglMulai(jsonObject.getString("tgl_mulai"));
                                    event.setTglBerakhir(jsonObject.getString("tgl_berakhir"));
                                    event.setJamMulai(jsonObject.getString("jam_mulai"));
                                    event.setJamSelesai(jsonObject.getString("jam_selesai"));
                                    event.setNamaUstadz(jsonObject.getString("nama_ustadz"));
                                    event.setImageUstadz(jsonObject.getString("image_ustadz"));
                                    FormatTanggalIDN formatTanggalIDN = new FormatTanggalIDN();

                                    final long dateDiff=formatTanggalIDN.dateDiff(event.getTglMulai());
                                    Log.d("dateDiff",String.valueOf(dateDiff));
                                    if (dateDiff>=0){


                                        if (filter.toLowerCase().equalsIgnoreCase("search")){

                                            if(event.getNamaEvent().replace(" ","").toLowerCase().indexOf(key.replace(" ","").toLowerCase()) != -1) {
                                                isValidEvent = true;
                                            }
                                            else if(event.getNamaUstadz().replace(" ","").toLowerCase().indexOf(key.replace(" ","").toLowerCase()) != -1) {
                                                isValidUstadz = true;
                                            }
                                            else if(event.getNamaVenue().replace(" ","").toLowerCase().indexOf(key.replace(" ","").toLowerCase()) != -1) {
                                                isValidVenue = true;
                                            }
                                            else if(event.getAlamatVenue().replace(" ","").toLowerCase().indexOf(key.replace(" ","").toLowerCase()) != -1) {
                                                isValidAddress = true;
                                            }
                                            else if(event.getNamaEvent().toLowerCase().equalsIgnoreCase(key.toLowerCase())){
                                                isValidEvent = true;
                                            }
                                            else if(event.getNamaUstadz().toLowerCase().equalsIgnoreCase(key.toLowerCase())){
                                                isValidUstadz = true;
                                            }
                                            else if(event.getNamaVenue().toLowerCase().equalsIgnoreCase(key.toLowerCase())){
                                                isValidVenue = true;
                                            }
                                            else if(event.getAlamatVenue().toLowerCase().equalsIgnoreCase(key.toLowerCase())){
                                                isValidAddress = true;
                                            }

                                            if (isValidEvent||isValidAddress||isValidUstadz||isValidVenue){
                                                eventArrayList.add(event);
                                            }

                                        }else{
                                            eventArrayList.add(event);
                                        }

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }
                            initEvent();
                            adapter.notifyDataSetChanged();
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
                progressBar.setVisibility(View.GONE);
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



}