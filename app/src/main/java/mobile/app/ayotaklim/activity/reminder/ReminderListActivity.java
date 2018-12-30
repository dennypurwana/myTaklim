package mobile.app.ayotaklim.activity.reminder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import mobile.app.ayotaklim.MainActivity;
import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.event.RegisterEventActivity;
import mobile.app.ayotaklim.activity.venue.VenueDetailActivity;
import mobile.app.ayotaklim.activity.venue.VenueListAdapter;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.reminder.Reminder;
import mobile.app.ayotaklim.models.venue.Venue;

public class ReminderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderListAdapter adapter;
    private ArrayList<Reminder> reminderArrayList;
    private Context mContext;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);
        sessionManager = new SessionManager(ReminderListActivity.this);
        getData();
    }

    void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_reminder);
        adapter = new ReminderListAdapter(reminderArrayList, new ReminderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Reminder reminder) {
                  dialogMessage();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ReminderListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void getData(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url=Config.GET_REMINDER;
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
                        reminderArrayList=new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("reminder");

                        if(vResponse.length()>0) {
                            for (int i = 0; i < vResponse.length(); i++) {
                                try {

                                    JSONObject jsonObject = vResponse.getJSONObject(i);
                                    Reminder reminder = new Reminder();
                                    reminder.setEventName(jsonObject.getString("nama_event"));
                                    reminder.setEventLocation(jsonObject.getString("nama"));
                                    reminder.setEventAddress(jsonObject.getString("alamat"));
                                    reminder.setEventDate(jsonObject.getString("tgl_mulai"));
                                    reminder.setEventId(jsonObject.getInt("c_event_id"));
                                    reminder.setMemberId(jsonObject.getInt("c_member_id"));

                                    if (sessionManager.getMemberId()==reminder.getMemberId()){
                                        reminderArrayList.add(reminder);
                                    }

                                    progressDialog.dismiss();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                            }
                            initView();
                            adapter.notifyDataSetChanged();
                        }else{
                           // recyclerView.setVisibility(View.GONE);
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


    private void dialogMessage() {
        new AlertDialog.Builder(ReminderListActivity.this,R.style.CustomDialogTheme)
                .setTitle("Reminder")
                .setMessage("Kajian yang anda ikuti akan berlangsung dalam 2 hari lagi.")
                .setPositiveButton("OK", null)
                .show();

    }
}
