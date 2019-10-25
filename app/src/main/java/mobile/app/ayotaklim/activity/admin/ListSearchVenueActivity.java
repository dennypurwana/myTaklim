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
import mobile.app.ayotaklim.models.venue.Venue;

public class ListSearchVenueActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListVenueSearchAdapter adapter;
    private ArrayList<Venue> venueArrayList;
    SessionManager sessionManager;
    Button btnAdd;
    String nama,topik,sDate,eDate,sTime,eTime,pemateriId,pemateri,venue,flag,fileName,fileUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_search_venue);
        sessionManager = new SessionManager(ListSearchVenueActivity.this);
            flag = getIntent().getStringExtra("flag");
            nama = getIntent().getStringExtra("nama");
            topik = getIntent().getStringExtra("topik");
            sDate = getIntent().getStringExtra("startDate");
            eDate = getIntent().getStringExtra("endDate");
            sTime = getIntent().getStringExtra("startTime");
            eTime = getIntent().getStringExtra("endTime");
            venue = getIntent().getStringExtra("venue");
            pemateri = getIntent().getStringExtra("pemateri");
            fileName = getIntent().getStringExtra("fileName");
            pemateriId = getIntent().getStringExtra("pemateriId");
            fileUrl=getIntent().getStringExtra("fileUrl");
        getData();
    }

    void initVenue(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_venue);
        adapter = new ListVenueSearchAdapter(ListSearchVenueActivity.this,venueArrayList, new ListVenueSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Venue venue) {
                Intent intent=new Intent(ListSearchVenueActivity.this,AddEventActivity.class);

                intent.putExtra("nama",nama);
                intent.putExtra("topik",topik);
                intent.putExtra("startDate",sDate);
                intent.putExtra("endDate",eDate);
                intent.putExtra("startTime",sTime);
                intent.putExtra("endTime",eTime);
                intent.putExtra("venue",venue.getNama());
                intent.putExtra("venueId",venue.getId());
                intent.putExtra("pemateri",pemateri);
                intent.putExtra("pemateriId",pemateriId);
                intent.putExtra("fileName",fileName);
                intent.putExtra("fileUrl",fileUrl);
                intent.putExtra("flag","FROM_EVENT");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListSearchVenueActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ListSearchVenueActivity.this,AddVenueActivity.class);
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
                                    Venue venue = new Venue();
                                    venue.setId(jsonObject.getInt("id"));
                                    venue.setNama(jsonObject.getString("nama"));
                                    venue.setAlamat(jsonObject.getString("alamat"));
                                    venue.setNoTlp(jsonObject.getString("no_tlp"));
                                    venue.setLongitude(jsonObject.getDouble("longitude"));
                                    venue.setLatitude(jsonObject.getDouble("latitude"));
                                    venue.setDkm(jsonObject.getString("dkm"));
                                    venue.setEmail(jsonObject.getString("email"));
                                    venue.setDkmPhone(jsonObject.getString("dkm_phone"));
                                    venue.setImageVenue(jsonObject.getString("imagebase64"));
                                    venue.setDeskripsi(jsonObject.getString("deskripsi"));
                                    progressDialog.dismiss();
                                    venueArrayList.add(venue);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                            }
                            initVenue();
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
