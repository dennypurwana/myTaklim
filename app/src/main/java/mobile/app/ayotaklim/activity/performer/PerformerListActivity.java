package mobile.app.ayotaklim.activity.performer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.venue.VenueDetailActivity;
import mobile.app.ayotaklim.activity.venue.VenueListAdapter;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.models.performer.Performer;
import mobile.app.ayotaklim.models.venue.Venue;

public class PerformerListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private PerformerListAdapter adapter;
    private ArrayList<Performer> performerArrayList;
    private Context mContext;

    SliderLayout sliderLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_list);
        initSlider();
    }
    void initSlider(){
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(1); //set scroll delay in seconds :
        setSliderViews();
        getData();
    }
    void initView(){

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_performer);
        adapter = new PerformerListAdapter(performerArrayList, new PerformerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Performer performer) {
                Intent intent=new Intent(PerformerListActivity.this,PerformerDetailActivity.class);
                intent.putExtra("Performer", performer);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PerformerListActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    private void getData(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url=Config.GET_USTADZ;
        Log.d("API : ",url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                        Log.e("TAG", "ustadz response: " + response.toString());
                        try {
                            performerArrayList=new ArrayList<>();
                            JSONArray vResponse=response.getJSONArray("ustadz");
                            if(vResponse.length()>0) {
                                for (int i = 0; i < vResponse.length(); i++) {
                                    try {

                                        JSONObject jsonObject = vResponse.getJSONObject(i);
                                        Performer ustadz = new Performer();
                                        ustadz.setNama(jsonObject.getString("nama"));
                                        ustadz.setAlamat(jsonObject.getString("alamat"));
                                        ustadz.setPhone(jsonObject.getString("phone"));
                                        ustadz.setTglLahir(jsonObject.getString("tgl_lahir"));
                                        ustadz.setEmail(jsonObject.getString("email"));
                                        ustadz.setInstagram(jsonObject.getString("instagram"));
                                        ustadz.setFacebook(jsonObject.getString("facebook"));
                                        ustadz.setImageUstadz(jsonObject.getString("imageBase64"));
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


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.getMessage());

            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);

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
            sliderView.setDescription("Ustadz yang sedang tranding " + (i + 1));
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


    }