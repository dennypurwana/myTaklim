package mobile.app.ayotaklim.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
import mobile.app.ayotaklim.models.performer.Performer;

public class ListSearchPerformerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListPerformerSearchAdapter adapter;
    private ArrayList<Performer> performerArrayList;
    SessionManager sessionManager;
    String sTime,eTime,pemateri,flag;
    String nama,topik,sDate,eDate,pemateriId,venue,fileName,fileUrl;
    int eventId,venueId;
    Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_search_performer);
        sessionManager = new SessionManager(ListSearchPerformerActivity.this);
        flag = getIntent().getStringExtra("flag");
        if (flag.equalsIgnoreCase("FROM_EVENT")){
            flag = getIntent().getStringExtra("flag");
            nama = getIntent().getStringExtra("nama");
            topik = getIntent().getStringExtra("topik");
            sDate = getIntent().getStringExtra("startDate");
            eDate = getIntent().getStringExtra("endDate");
            sTime = getIntent().getStringExtra("startTime");
            eTime = getIntent().getStringExtra("endTime");
            venue = getIntent().getStringExtra("venue");
            venueId = getIntent().getIntExtra("venueId",0);
            pemateri = getIntent().getStringExtra("pemateri");
            fileName = getIntent().getStringExtra("fileName");
            pemateriId = getIntent().getStringExtra("pemateriId");
            fileUrl=getIntent().getStringExtra("fileUrl");

        }else {

            nama = getIntent().getStringExtra("nama");
            sTime = getIntent().getStringExtra("startTime");
            eTime = getIntent().getStringExtra("endTime");
            pemateri = getIntent().getStringExtra("pemateri");
            eventId = getIntent().getIntExtra("eventId", 0);

        }
        getData();
    }

    void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_performer);
        adapter = new ListPerformerSearchAdapter(ListSearchPerformerActivity.this,performerArrayList, new ListPerformerSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Performer performer) {
               if (flag.equalsIgnoreCase("FROM_EVENT")){

                   Intent intent = new Intent(ListSearchPerformerActivity.this, AddEventActivity.class);

                   intent.putExtra("pemateri", performer.getNama());
                   intent.putExtra("pemateriId", performer.getRecord_id());
                   intent.putExtra("nama",nama);
                   intent.putExtra("topik",topik);
                   intent.putExtra("startDate",sDate);
                   intent.putExtra("endDate",eDate);
                   intent.putExtra("startTime",sTime);
                   intent.putExtra("endTime",eTime);
                   intent.putExtra("venue",venue);
                   intent.putExtra("venueId",venueId);
                   intent.putExtra("fileName",fileName);
                   intent.putExtra("fileUrl",fileUrl);
                   intent.putExtra("flag","FROM_EVENT");
                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
                   finish();

               }else {

                   Intent intent = new Intent(ListSearchPerformerActivity.this, AddJadwalEventActivity.class);
                   intent.putExtra("nama", nama);
                   intent.putExtra("startTime", sTime);
                   intent.putExtra("endTime", eTime);
                   intent.putExtra("pemateri", performer.getNama());
                   intent.putExtra("id", performer.getRecord_id());
                   intent.putExtra("eventId", eventId);
                   intent.putExtra("flag", "SEARCH");
                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
                   finish();

               }
            }

        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListSearchPerformerActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ListSearchPerformerActivity.this,AddPerformerActivity.class);
                intent.putExtra("flag","ADD");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void getData(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url=Config.GET_USTADZ;
        Log.d("API : ",url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("response pemateri : " , response);
                if (!response.equals(null)) {
                    progressDialog.hide();
                    Log.e("TAG", "produk response: " + response.toString());
                    try {
                        performerArrayList=new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("items");
                        if(vResponse.length()>0) {
                            for (int i = 0; i < vResponse.length(); i++) {
                                try {

                                    JSONObject jsonObject = vResponse.getJSONObject(i);
                                    Performer ustadz = new Performer();
                                    ustadz.setRecord_id(jsonObject.getInt("id"));
                                    ustadz.setNama(jsonObject.getString("nama"));
                                    ustadz.setAlamat(jsonObject.getString("alamat"));
                                    ustadz.setPhone(jsonObject.getString("phone"));
                                    ustadz.setTglLahir(jsonObject.getString("tgl_lahir"));
                                    ustadz.setEmail(jsonObject.getString("email"));
                                    ustadz.setInstagram(jsonObject.getString("instagram"));
                                    ustadz.setFacebook(jsonObject.getString("facebook"));
                                    ustadz.setImageUstadz(jsonObject.getString("imagebase64"));
                                    ustadz.setDeskripsi(jsonObject.getString("deskripsi"));
                                    ustadz.setYoutube(jsonObject.getString("youtube"));
                                    ustadz.setPendidikan(jsonObject.getString("pendidikan"));


                                    progressDialog.dismiss();
                                    performerArrayList.add(ustadz);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                            }
                            initView();
                            adapter.notifyDataSetChanged();
                        }else{
                            recyclerView.setVisibility(View.GONE);
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


}
