package mobile.app.ayotaklim.activity.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import mobile.app.ayotaklim.activity.admin.AddJadwalEventActivity;
import mobile.app.ayotaklim.activity.performer.PerformerDetailActivity;
import mobile.app.ayotaklim.activity.reminder.ReminderListActivity;
import mobile.app.ayotaklim.activity.venue.VenueDetailActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.event.Jadwal;
import mobile.app.ayotaklim.utils.FormatTanggalIDN;

public class EventJadwalActivity extends AppCompatActivity {
    Context context;
    private ArrayList<Jadwal> jadwalArrayList;
    private int c_event_id;
    private EventJadwalListAdapter jadwalListAdapter;
    RelativeLayout lihatDetailUstadz, lihatDetailMasjid, lihatPoster;
    TextView eventName,eventDate,eventVenue,namaUstadz,venueAddress,waktuEvent;
    ImageView bannerImage, iconBack;
    RecyclerView recyclerViewJadwal;
    SessionManager sessionManager;
    FormatTanggalIDN formatTanggalIDN;
    Event event;
    Button btnApply , btnAdd;
     ProgressDialog progressDialog;
    int flagEvent, flagMember;
    Dialog dialog;
    ImageView imagePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_jadwal);
        progressDialog = new ProgressDialog(this);
        context =EventJadwalActivity.this;
        sessionManager = new SessionManager(EventJadwalActivity.this);
        formatTanggalIDN = new FormatTanggalIDN();
        event = (Event) getIntent().getSerializableExtra("Event");
        eventName = findViewById(R.id.namaEvent);
        namaUstadz = findViewById(R.id.namaUstadz);
        venueAddress = findViewById(R.id.venueAddress);
        waktuEvent = findViewById(R.id.waktuEvent);
        eventDate = findViewById(R.id.tanggalEvent);
        eventVenue = findViewById(R.id.venueEvent);
        bannerImage = findViewById(R.id.bannerEvent);
        iconBack = findViewById(R.id.iconBack);
        btnApply = findViewById(R.id.btnApply);
        lihatDetailUstadz = findViewById(R.id.lihatDetailUstadz);
        lihatDetailMasjid = findViewById(R.id.lihatDetailMasjid);
        lihatPoster = findViewById(R.id.lihatBanner);
        btnAdd = findViewById(R.id.btnAdd);
        eventName.setText(event.getNamaEvent());
        eventVenue.setText(event.getNamaVenue());
        namaUstadz.setText(event.getNamaUstadz());
        venueAddress.setText(event.getAlamatVenue());
        waktuEvent.setText("   "+event.getJamMulai()+" - "+event.getJamSelesai());
        eventDate.setText(formatTanggalIDN.formatDate(event.getTglMulai()));
        Picasso.get()
                .load(Config.IMAGE_URL+event.getBannerImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(bannerImage);;
        getDataEventJadwal();
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("check member : ",String.valueOf(sessionManager.checkMember()));
                if(!sessionManager.checkMember()) {
                    Intent intent = new Intent(EventJadwalActivity.this, RegisterEventActivity.class);
                    intent.putExtra("Event", event);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    dialogMessageEvent();
                }

            }

        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventJadwalActivity.this,AddJadwalEventActivity.class);
                Log.d("event id sebelum masuk",String.valueOf(event.getId()));
                intent.putExtra("eventId", event.getId());
                intent.putExtra("flag", "ADD");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lihatDetailUstadz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(EventJadwalActivity.this,PerformerDetailActivity.class);
                intent.putExtra("flag", "FROM_EVENT");
                intent.putExtra("c_pemateri_id", event.getPerformerId());
                startActivity(intent);

            }
        });

        lihatDetailMasjid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(EventJadwalActivity.this,VenueDetailActivity.class);
                intent.putExtra("flag", "FROM_EVENT");
                intent.putExtra("c_venue_id", event.getVenueId());
                startActivity(intent);

            }
        });

        lihatPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
    }

    private void showPoster(){
        AlertDialog.Builder alertadd = new AlertDialog.Builder(EventJadwalActivity.this);
        LayoutInflater factory = LayoutInflater.from(EventJadwalActivity.this);
        final View view = factory.inflate(R.layout.dialog_image, null);
        alertadd.setView(view);
        alertadd.setNeutralButton("CLOSE", null);
        alertadd.show();
    }
    private void createDialog()
    {
        //android.R.style.Theme_Black_NoTitleBar_Fullscreen
        dialog = new Dialog(EventJadwalActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setTitle("Poster Kajian");
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_image);
        imagePoster= (ImageView) dialog.findViewById(R.id.dialog_imageview);
        Picasso.get()
                .load(Config.IMAGE_URL+event.getBannerImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(imagePoster);;
        dialog.show();
    }
    private void registerToEvent(int eventId , int memberId){
        progressDialog.setTitle("sedang mendaftarkan..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject dataJson  = new JSONObject();
        JSONObject eventMemberJson  = new JSONObject();

        try {
            eventMemberJson.put("c_event_id", eventId);
            eventMemberJson.put("c_member_id", memberId);
            dataJson.put("data", eventMemberJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data member req : ",dataJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Config.REGISTER_EVENT_MEMBER, dataJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.d("TAG",response.toString());
                Log.d("TAG","====================== SUCCESS ========================");
                try {
                    Boolean success  = response.getBoolean("success");
                    String message   = response.getString("message");
                    JSONObject data  = response.getJSONObject("data");
                    if (success){
                        dialogMessage();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
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

    private void dialogMessageEvent() {
        new AlertDialog.Builder(context,R.style.CustomDialogTheme)
                .setTitle("Informasi")
                .setMessage("Anda sudah mengikuti event sebelumnya, apakah anda yakin mengikuti event ini?")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        registerToEvent(event.getId(),sessionManager.getMemberId());
                    }
                })
                .show();

    }

    private void dialogMessage() {
        new AlertDialog.Builder(context,R.style.CustomDialogTheme)
                .setTitle("Daftar Sukses")
                .setCancelable(false)
                .setMessage("Anda telah berhasil mendaftar pada event ini. Silahkan lihat menu reminder pada menu aplikasi.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EventJadwalActivity.this, ReminderListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .show();

    }

    void checkMember(){
        String url=Config.CHECK_EVENT_MEMBER;
        Log.d("API : ",url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response venus : " , response);

                if (!response.equals(null)) {
                    Log.e("TAG", "produk response: " + response.toString());
                    try {
                        progressDialog.dismiss();
                        jadwalArrayList=new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("items");
                        if(vResponse.length()>0) {
                            for (int i = 0; i < vResponse.length(); i++) {
                                try {

                                    JSONObject jsonObject = vResponse.getJSONObject(i);
                                    int eventId = jsonObject.getInt("c_event_id");
                                    int memberId = jsonObject.getInt("c_member_id");
                                    if (event.getId()==eventId&&sessionManager.getMemberId()== memberId){
                                        flagEvent = eventId;
                                        flagMember = memberId;
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }
                            if (!sessionManager.isAdmin()) {
                                if (flagEvent == event.getId()) {
                                    btnApply.setVisibility(View.GONE);
                                } else {
                                    btnApply.setVisibility(View.VISIBLE);
                                }
                            }else {
                                btnAdd.setVisibility(View.VISIBLE);
                                btnApply.setVisibility(View.GONE);
                            }
                        }else{

                        }
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        if (!sessionManager.isAdmin()){
                        btnApply.setVisibility(View.VISIBLE);
                        btnAdd.setVisibility(View.GONE);
                        }else {
                         btnAdd.setVisibility(View.VISIBLE);
                         btnApply.setVisibility(View.GONE);
                        }
                    }

                } else {
                    if (!sessionManager.isAdmin()){
                        btnApply.setVisibility(View.VISIBLE);
                        btnAdd.setVisibility(View.GONE);
                    }else {
                        btnAdd.setVisibility(View.VISIBLE);
                        btnApply.setVisibility(View.GONE);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (!sessionManager.isAdmin()){
                    btnApply.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.GONE);
                }else {
                    btnAdd.setVisibility(View.VISIBLE);
                    btnApply.setVisibility(View.GONE);
                }

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

    void getDataEventJadwal(){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url=Config.GET_DATA_EVENT_JADWAL;
        Log.d("API : ",url);
        Log.d("event Id : ", String.valueOf(event.getId()));
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response venus : " , response);
                if (!response.equals(null)) {
                    checkMember();
                    Log.e("TAG", "produk response: " + response.toString());
                    try {
                        jadwalArrayList=new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("jadwal");

                        if(vResponse.length()>0) {
                            for (int i = 0; i < vResponse.length(); i++) {
                                try {

                                    JSONObject jsonObject = vResponse.getJSONObject(i);
                                    Jadwal jadwal = new Jadwal();
                                    jadwal.setcEventId(jsonObject.getInt("c_event_id"));
                                    jadwal.setKegiatan(jsonObject.getString("kegiatan"));
                                    jadwal.setNamaUstadz(jsonObject.getString("nama"));
                                    jadwal.setDari(jsonObject.getString("waktu_mulai"));
                                    jadwal.setSampai(jsonObject.getString("waktu_selesai"));
                                    jadwal.setImagebase64(jsonObject.getString("imagebase64"));
                                    jadwal.setNama_event(jsonObject.getString("nama_event"));
                                    jadwal.setPemateriId(jsonObject.getInt("c_pemateri_id"));
                                    progressDialog.dismiss();
                                    Log.d("jadwal id : ",String.valueOf(jadwal.getcEventId()));
                                    if (jadwal.getcEventId()==event.getId()){
                                        jadwalArrayList.add(jadwal);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                            }

                            initEventJadwal();


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


    void initEventJadwal(){
        recyclerViewJadwal = (RecyclerView) findViewById(R.id.recycler_view_jadwal);
        if (jadwalArrayList.size()>0) {
            jadwalListAdapter = new EventJadwalListAdapter(jadwalArrayList, new EventJadwalListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Jadwal jadwal) {
                    Intent intent=new Intent(EventJadwalActivity.this,PerformerDetailActivity.class);
                    intent.putExtra("flag", "FROM_EVENT");
                    intent.putExtra("c_pemateri_id", jadwal.getPemateriId());
                    startActivity(intent);
                }
            });
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventJadwalActivity.this);
            recyclerViewJadwal.setLayoutManager(layoutManager);
            recyclerViewJadwal.setAdapter(jadwalListAdapter);
            recyclerViewJadwal.setVisibility(View.VISIBLE);
            jadwalListAdapter.notifyDataSetChanged();
        }
    }
}
