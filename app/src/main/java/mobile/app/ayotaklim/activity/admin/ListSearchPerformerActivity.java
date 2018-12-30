package mobile.app.ayotaklim.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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
import mobile.app.ayotaklim.models.venue.Venue;

public class ListSearchPerformerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListPerformerSearchAdapter adapter;
    private ArrayList<Performer> performerArrayList;
    SessionManager sessionManager;
    String nama,sTime,eTime,pemateri,flag;
    int eventId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_search_performer);
        sessionManager = new SessionManager(ListSearchPerformerActivity.this);
        flag = getIntent().getStringExtra("flag");
        nama = getIntent().getStringExtra("nama");
        sTime = getIntent().getStringExtra("startTime");
        eTime = getIntent().getStringExtra("endTime");
        pemateri = getIntent().getStringExtra("pemateri");
        eventId = getIntent().getIntExtra("eventId",0);
        getData();
    }

    void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_performer);
        adapter = new ListPerformerSearchAdapter(ListSearchPerformerActivity.this,performerArrayList, new ListPerformerSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Performer performer) {
                Intent intent=new Intent(ListSearchPerformerActivity.this,AddJadwalEventActivity.class);
                intent.putExtra("nama",nama);
                intent.putExtra("startTime",sTime);
                intent.putExtra("endTime",eTime);
                intent.putExtra("pemateri",performer.getNama());
                intent.putExtra("id",performer.getRecord_id());
                intent.putExtra("eventId",eventId);
                intent.putExtra("flag","SEARCH");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListSearchPerformerActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
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
