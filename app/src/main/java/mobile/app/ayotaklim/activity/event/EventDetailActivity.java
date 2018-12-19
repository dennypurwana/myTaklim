package mobile.app.ayotaklim.activity.event;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.CheckDistanceLocations;

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
    ImageView imageVenue;
    SessionManager sessionManager ;
    private int venueId;
    Venue venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        sessionManager = new SessionManager(EventDetailActivity.this);
        eventVenue= findViewById(R.id.eventVenue);
        eventVenueAddress = findViewById(R.id.eventVenueAddress);
        imageVenue = findViewById(R.id.imageEvent);
       // venue = (Venue) getIntent().getSerializableExtra("Venue");
        Toast.makeText(getApplicationContext(),getIntent().getStringExtra("nama"),Toast.LENGTH_LONG).show();

        //eventVenue.setText(venue.getNama());
        //eventVenueAddress.setText(venue.getAlamat());
//        Picasso.get()
//                .load(Config.IMAGE_URL+venue.getImageVenue())
//                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.placeholder_image)
//                .fit()
//                .into(imageVenue);
//        venueId = venue.getId();
        /*
        event= (Event) getIntent().getSerializableExtra("Event");

        eventName.setText(event.getTitle());
        eventTopic.setText(event.getTopic());
        eventPerformer.setText(event.getPerformer());
        eventDate.setText(event.getDate());
        eventDesc.setText(event.getDescription());
        eventVenue.setText(event.getVenue());
        eventVenueAddress.setText(event.getVenueAddress());
        eventTime.setText(event.getStartTime()+"-"+event.getEndTime());

      applyButton = (AppCompatButton)  findViewById(R.id.btnApply);
      applyButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(EventDetailActivity.this,RegisterEventActivity.class);
              intent.putExtra("Event", event);
              startActivity(intent);
          }
      });
*/
    getDataEvent();
    }

    void getDataEvent(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url=Config.GET_EVENT;
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
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("items");
                        if(vResponse.length()>0) {
                            for (int i = 0; i < vResponse.length(); i++) {
                                try {

                                    JSONObject jsonObject = vResponse.getJSONObject(i);
                                    Event event = new Event();
                                    event.setId(jsonObject.getInt("id"));
                                    event.setNamaEvent(jsonObject.getString("nama_event"));
                                    event.setVenueId(jsonObject.getInt("c_venue_id"));
                                    event.setTglMulai(jsonObject.getString("tgl_mulai"));
                                    event.setTglBerakhir(jsonObject.getString("tgl_berakhir"));

                                    if (event.getVenueId()==venueId){
                                        Log.d("kajian : ",event.getNamaEvent());
                                    }

                                    progressDialog.dismiss();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
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


}
