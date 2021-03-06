package mobile.app.ayotaklim.activity.performer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.admin.AddPerformerActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.performer.Performer;

public class PerformerListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private PerformerListAdapter adapter;
    private ArrayList<Performer> performerArrayList;
    private Context mContext;
    SessionManager sessionManager;
    SliderLayout sliderLayout;
    Button btnAdd;
    ProgressBar progressBar;
    EditText edTextSearch;
    ImageView iconSearch;
    boolean isValidName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_list);
        sessionManager = new SessionManager(PerformerListActivity.this);
        progressBar = findViewById(R.id.progressBar);
        edTextSearch = findViewById(R.id.edTextSearch);
        iconSearch = findViewById(R.id.iconSearch);
        setProgressBarIndeterminateVisibility(true);
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = edTextSearch.getText().toString();
                getData("SEARCH",key);
            }
        });
        initSlider();
        if (sessionManager.isAdmin()){
            btnAdd = findViewById(R.id.btnAdd);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(PerformerListActivity.this,AddPerformerActivity.class);
                    intent.putExtra("flag","ADD");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            btnAdd.setVisibility(View.VISIBLE);
        }
    }
    void initSlider(){
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(1); //set scroll delay in seconds :
        setSliderViews();
        getData("ALL","");
    }
    void initView(){

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_performer);
        adapter = new PerformerListAdapter(PerformerListActivity.this,performerArrayList, new PerformerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Performer performer) {
                Intent intent=new Intent(PerformerListActivity.this,PerformerDetailActivity.class);
                intent.putExtra("flag", "FROM_LIST");
                intent.putExtra("c_performer_id", "");
                intent.putExtra("Performer", performer);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Performer performer) {
                delete(performer.getRecord_id());
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PerformerListActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }


    private void getData(final String filter, final String key){
        progressBar.setVisibility(View.VISIBLE);
        String url=Config.GET_USTADZ;
        Log.d("API : ",url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               progressBar.setVisibility(View.GONE);
                Log.d("response pemateri : " , response);
                if (!response.equals(null)) {
                    Log.e("TAG", "produk response: " + response.toString());
                    try {
                        performerArrayList=new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("items");
                        if(vResponse.length()>0) {
                            for (int i = 0; i < vResponse.length(); i++) {
                                isValidName = false;
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

                                    if (filter.toLowerCase().equalsIgnoreCase("search")){

                                        if(ustadz.getNama().replace(" ","").toLowerCase().indexOf(key.replace(" ","").toLowerCase()) != -1) {
                                            isValidName = true;
                                        }

                                        else if(ustadz.getNama().toLowerCase().equalsIgnoreCase(key.toLowerCase())){
                                            isValidName = true;
                                        }

                                        if (isValidName){
                                            performerArrayList.add(ustadz);
                                        }

                                    }else{
                                        performerArrayList.add(ustadz);

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.GONE);

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
                progressBar.setVisibility(View.GONE);
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

    private void setSliderViews() {

        for (int i = 0; i <= 3; i++) {

            SliderView sliderView = new SliderView(PerformerListActivity.this);

            switch (i) {
                case 0:
                    sliderView.setImageDrawable(R.drawable.ustadz_hanan);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/547114/pexels-photo-547114.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 1:
                    sliderView.setImageDrawable(R.drawable.ustadz_uas);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.ustadz_zaki);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
                    break;
                case 3:
                    sliderView.setImageDrawable(R.drawable.image_ustadz);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
//            sliderView.setDescription("Ustadz yang sedang tranding " + (i + 1));
            final int finalI = i;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    Toast.makeText(PerformerListActivity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();
                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }

    }

    private void delete(int record_id){
        JSONObject dataJson  = new JSONObject();
        JSONObject inputJson  = new JSONObject();
        try {
            inputJson.put("record_id", record_id);
            dataJson.put("data", inputJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Config.DELETE_USTADZ, dataJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG",response.toString());
                Log.d("TAG","====================== SUCCESS ========================");
                try {
                    Boolean success  = response.getBoolean("success");
                    if (success){
                        getData("ALL","");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
    }
