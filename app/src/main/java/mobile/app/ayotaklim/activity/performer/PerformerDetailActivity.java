package mobile.app.ayotaklim.activity.performer;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.admin.AddPerformerActivity;
import mobile.app.ayotaklim.activity.event.EventJadwalActivity;
import mobile.app.ayotaklim.activity.event.EventListAdapter;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.performer.Performer;
import mobile.app.ayotaklim.utils.FormatTanggalIDN;

public class PerformerDetailActivity extends AppCompatActivity {
    Performer performer;
    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private ArrayList<Event> eventArrayList;
    ImageView imageUstadz, btnEdit, iconBack;
    TextView nama,
            alamat,
            dob,
            pendidikan,
            phone,
            deskripsi,
            email,
            ig,
            fb,
            youtube;
        SessionManager sessionManager;
        int record_id;
        String flag;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_detail);
        sessionManager = new SessionManager(PerformerDetailActivity.this);
        progressBar = findViewById(R.id.progressBar);
        nama = findViewById(R.id.performerName);
        alamat = findViewById(R.id.address);
        phone= findViewById(R.id.phone1);
        deskripsi = findViewById(R.id.desc);
        pendidikan = findViewById(R.id.pendidikan);
        fb = findViewById(R.id.fb);
        ig = findViewById(R.id.ig);
        youtube = findViewById(R.id.youtube);
        imageUstadz =findViewById(R.id.imageUstadz);
        iconBack = findViewById(R.id.iconBack);
        setProgressBarIndeterminateVisibility(true);

        flag = getIntent().getStringExtra("flag");
        if (flag.equalsIgnoreCase("FROM_EVENT")){
            record_id = getIntent().getIntExtra("c_pemateri_id",0);
            getDataById();
            getDataEvent(record_id);
        }else {
            performer = (Performer) getIntent().getSerializableExtra("Performer");
            getDataEvent(performer.getRecord_id());
            initView();
        }

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (sessionManager.isAdmin()){
            btnEdit =findViewById(R.id.iconEdit);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PerformerDetailActivity.this, AddPerformerActivity.class);
                    intent.putExtra("flag","EDIT");
                    intent.putExtra("Performer", performer);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            btnEdit.setVisibility(View.VISIBLE);
        }

    }



private void initView(){
        progressBar.setVisibility(View.GONE);
    nama.setText(performer.getNama());
    alamat.setText(performer.getAlamat());
    phone.setText(performer.getPhone());
    deskripsi.setText(performer.getDeskripsi());
    pendidikan.setText(performer.getPendidikan());
    fb.setText(performer.getFacebook());
    ig.setText(performer.getInstagram());
    youtube.setText(performer.getYoutube());
    Picasso.get()
            .load(Config.IMAGE_URL+performer.getImageUstadz())
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .fit()
            .into(imageUstadz);
}

    private void getDataById(){
        progressBar.setVisibility(View.VISIBLE);
        String url=Config.GET_USTADZ_BY_ID;
        JSONObject dataJson  = new JSONObject();
        JSONObject memberJson  = new JSONObject();

        try {
            memberJson.put("record_id",record_id);
            dataJson.put("data", memberJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data req : ",dataJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Config.GET_USTADZ_BY_ID, dataJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                Log.d("TAG PEMATERI",response.toString());
                Log.d("TAG","====================== SUCCESS ========================");
                try {
                    Boolean success  = response.getBoolean("success");
                    String message   = response.getString("message");
                    JSONObject data  = response.getJSONObject("data");
                    performer = new Performer();
                    performer.setRecord_id(data.getInt("id"));
                    performer.setNama(data.getString("nama"));
                    performer.setAlamat(data.getString("alamat"));
                    performer.setDeskripsi(data.getString("deskripsi"));
                    performer.setEmail(data.getString("email"));
                    performer.setFacebook(data.getString("facebook"));
                    performer.setYoutube(data.getString("youtube"));
                    performer.setInstagram(data.getString("instagram"));
                    performer.setImageUstadz(data.getString("imagebase64"));
                    performer.setPhone(data.getString("phone"));
                    performer.setPendidikan(data.getString("pendidikan"));
                    performer.setTglLahir(data.getString("tgl_lahir"));
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


    void initEvent(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_event);
        recyclerView.setVisibility(View.VISIBLE);
        adapter = new EventListAdapter("eventPerformer",eventArrayList, new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {

                Intent intent = new Intent(PerformerDetailActivity.this, EventJadwalActivity.class);
                intent.putExtra("Event", event);
                startActivity(intent);
            }

        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PerformerDetailActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    void getDataEvent(final int dataId){
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
                                        if (String.valueOf(event.getPerformerId()).equalsIgnoreCase(String.valueOf(dataId))){
                                            eventArrayList.add(event);
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }
                            initEvent();
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