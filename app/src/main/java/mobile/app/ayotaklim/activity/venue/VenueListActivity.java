package mobile.app.ayotaklim.activity.venue;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import mobile.app.ayotaklim.activity.admin.AddVenueActivity;
import mobile.app.ayotaklim.activity.event.EventListAdapter;
import mobile.app.ayotaklim.activity.performer.PerformerListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.venue.Venue;

public class VenueListActivity  extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VenueListAdapter adapter;
    private ArrayList<Venue> venueArrayList;
    SliderLayout sliderLayout;
    SessionManager sessionManager;
    Button btnAdd;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_list);
        sessionManager = new SessionManager(VenueListActivity.this);
        progressBar = findViewById(R.id.progressBar);
        setProgressBarIndeterminateVisibility(true);
        if (sessionManager.isAdmin()) {
            btnAdd = findViewById(R.id.btnAdd);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VenueListActivity.this, AddVenueActivity.class);
                    intent.putExtra("flag", "ADD");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            btnAdd.setVisibility(View.VISIBLE);
        }

        initSlider();
        getData();

    }

    void initSlider(){
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(1); //set scroll delay in seconds :
        setSliderViews();
    }

    void initVenue(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_venue);
        adapter = new VenueListAdapter(VenueListActivity.this,venueArrayList, new VenueListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Venue venue) {
                Intent intent=new Intent(VenueListActivity.this,VenueDetailActivity.class);
                intent.putExtra("Venue", venue);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Venue venue) {
                delete(venue.getId());
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VenueListActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }


    private void setSliderViews() {

        for (int i = 0; i <= 3; i++) {

            SliderView sliderView = new SliderView(VenueListActivity.this);

            switch (i) {
                case 0:
                    sliderView.setImageDrawable(R.drawable.masjid_istiqlal);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/547114/pexels-photo-547114.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 1:
                    sliderView.setImageDrawable(R.drawable.masjid_bandung);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.masjid_bandung_dua);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
                    break;
                case 3:
                    sliderView.setImageDrawable(R.drawable.masjid_depok);
//                    sliderView.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
//            sliderView.setDescription("Masjid Jami Istiqlal Jakarta " + (i + 1));
            final int finalI = i;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    Toast.makeText(getApplicationContext(), "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();
                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }

    }


    private void getData(){
       progressBar.setVisibility(View.VISIBLE);
        String url=Config.GET_VENUE;
        Log.d("API : ",url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               progressBar.setVisibility(View.GONE);
                Log.d("response venus : " , response);
                if (!response.equals(null)) {
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
                                    venue.setNamaImamRutin(jsonObject.getString("nama_imam_rutin"));
                                    venue.setJmlJamaah(jsonObject.getString("jumlah_jamaah_subuh"));
                                    venue.setNoRek(jsonObject.getString("no_rek_bank"));
                                    venue.setNoPln(jsonObject.getString("no_rek_pln"));
                                    venue.setNoPam(jsonObject.getString("no_rek_pam"));
                                    venue.setDkm(jsonObject.getString("dkm"));
                                    venue.setEmail(jsonObject.getString("email"));
                                    venue.setDkmPhone(jsonObject.getString("dkm_phone"));
                                    venue.setImageVenue(jsonObject.getString("imagebase64"));
                                    venue.setDeskripsi(jsonObject.getString("deskripsi"));
                                    venueArrayList.add(venue);

                                } catch (JSONException e) {
                                    e.printStackTrace();
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



    private void delete(int record_id){
        JSONObject dataJson  = new JSONObject();
        JSONObject inputJson  = new JSONObject();
            try {
                inputJson.put("record_id", record_id);
                dataJson.put("data", inputJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Config.DELETE_VENUE, dataJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG",response.toString());
                Log.d("TAG","====================== SUCCESS ========================");
                try {
                    Boolean success  = response.getBoolean("success");
                    if (success){
                       getData();
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
