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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.venue.Venue;

public class EventListActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private ArrayList<Event> eventArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                .permitDiskWrites()
                .build());
        StrictMode.setThreadPolicy(old);
        getData();
    }


    void initEvent(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_event);

        adapter = new EventListAdapter(eventArrayList, new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Intent intent=new Intent(EventListActivity.this,EventDetailActivity.class);
                intent.putExtra("Event", event);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventListActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }


    private void getData(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
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
                        Log.e("TAG", "produk response: " + response.toString());
                        try {
                            eventArrayList=new ArrayList<>();
                            JSONArray vResponse=response.getJSONArray("event");
                            if(vResponse.length()>0) {
                                for (int i = 0; i < vResponse.length(); i++) {
                                    try {

                                        JSONObject jsonObject = vResponse.getJSONObject(i);
                                        Event event = new Event();
                                       /* event.setTitle(jsonObject.getString("nama"));
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
                                        */
                                        progressDialog.dismiss();
                                        eventArrayList.add(event);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                    }
                                }
                                initEvent();
                                adapter.notifyDataSetChanged();
                            }else{
                                recyclerView.setVisibility(View.GONE);
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

    }



}