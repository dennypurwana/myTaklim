package mobile.app.ayotaklim.activity.event;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.event.Jadwal;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.FormatTanggalIDN;

public class EventDetailActivity  extends AppCompatActivity {

    Event event;
    AppCompatButton applyButton;
    TextView eventName,
            eventTopic,
            eventPerformer,
            eventDate,
            eventDesc,
            eventVenue,
            eventVenueAddress,eventTime
    ;
    ImageView imageVenue ,iconBack;
    SessionManager sessionManager ;
    private int venueId;
    Venue venue;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewJadwal;
    private EventListAdapter adapter;
    private EventJadwalListAdapter jadwalAdapter;
    private ArrayList<Event> eventArrayList;
    private ArrayList<Jadwal> jadwalArrayList;
    TextView labelEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        sessionManager = new SessionManager(EventDetailActivity.this);
        eventVenue= findViewById(R.id.eventVenue);
        eventVenueAddress = findViewById(R.id.eventVenueAddress);
        imageVenue = findViewById(R.id.imageEvent);

        iconBack = findViewById(R.id.iconBack);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        venue = (Venue) getIntent().getSerializableExtra("Venue");
       // Toast.makeText(getApplicationContext(),getIntent().getStringExtra("nama"),Toast.LENGTH_LONG).show();

        eventVenue.setText(venue.getNama());
        eventVenueAddress.setText(venue.getAlamat());
        Picasso.get()
                .load(Config.IMAGE_URL+venue.getImageVenue())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(imageVenue);
        venueId = venue.getId();
        getDataEvent();
    }

    void getDataEvent(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url=Config.GET_DATA_EVENT_HOME;
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
                        eventArrayList=new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("event");
                        if(vResponse.length()>0) {
                            for (int i = 0; i < vResponse.length(); i++) {
                                try {

                                    JSONObject jsonObject = vResponse.getJSONObject(i);
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
                                    progressDialog.dismiss();

                                    if (event.getVenueId()==venueId){
                                        Log.d("kajian : ",event.getNamaEvent());
                                        FormatTanggalIDN formatTanggalIDN = new FormatTanggalIDN();
                                        final long dateDiff =formatTanggalIDN.dateDiff(event.getTglMulai());
                                        if (dateDiff>=0){
                                            eventArrayList.add(event);
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                            }

                            if (eventArrayList.size()>0){
                                initEvent();
                                adapter.notifyDataSetChanged();
                            }

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


    void initEvent(){
        labelEvent = findViewById(R.id.labelEvent);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_event);
        if (eventArrayList.size()>0) {
            adapter = new EventListAdapter("eventDetail",eventArrayList, new EventListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Event event) {
                    Intent intent=new Intent(EventDetailActivity.this,EventJadwalActivity.class);
                    intent.putExtra("Event", event);
                    startActivity(intent);

                }
            });
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventDetailActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            labelEvent.setVisibility(View.VISIBLE);
        }
    }

    /*
    void initEventJadwal(){

        recyclerViewJadwal = (RecyclerView) findViewById(R.id.recycler_view_jadwal);

        jadwalAdapter = new EventJadwalListAdapter(jadwalArrayList, new EventJadwalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Jadwal jadwal) {

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventDetailActivity.this);

        recyclerViewJadwal.setLayoutManager(layoutManager);

        recyclerViewJadwal.setAdapter(jadwalAdapter);
    }
*/
    void getDataEventJadwal(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading jadwal...");
        progressDialog.show();
        String url=Config.GET_EVENT_PEMATERI;
        Log.d("API : ",url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("response jadwal : " , response);
                if (!response.equals(null)) {
                    progressDialog.hide();
                    Log.e("TAG", "produk response: " + response.toString());
                    try {
                        jadwalArrayList=new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("items");
                        if(vResponse.length()>0) {
                            for (int i = 0; i < vResponse.length(); i++) {
                                try {

                                    JSONObject jsonObject = vResponse.getJSONObject(i);
                                    Jadwal jadwal = new Jadwal();
                                    jadwal.setKegiatan(jsonObject.getString("kegiatan"));
//                                    JSONObject objLabel=jsonObject.getJSONObject("data_labels");
  //                                  jadwal.setNamaUstadz(objLabel.getString("c_pembicara_id"));
                                    jadwalArrayList.add(jadwal);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                            }
                            progressDialog.dismiss();
                           // initEventJadwal();
                            jadwalAdapter.notifyDataSetChanged();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
