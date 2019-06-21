package mobile.app.ayotaklim.activity.reminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.reminder.Reminder;
import mobile.app.ayotaklim.services.reminder.AlarmNotificationService;
import mobile.app.ayotaklim.services.reminder.AlarmReceiver;
import mobile.app.ayotaklim.services.reminder.AlarmSoundService;
import mobile.app.ayotaklim.utils.FormatTanggalIDN;

public class ReminderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderListAdapter adapter;
    private ArrayList<Reminder> reminderArrayList;
    private Context mContext;
    private SessionManager sessionManager;
    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 133;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);
        sessionManager = new SessionManager(ReminderListActivity.this);
        Intent alarmIntent = new Intent(ReminderListActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ReminderListActivity.this, ALARM_REQUEST_CODE, alarmIntent, 0);
        getData();

    }


    void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_reminder);
        adapter = new ReminderListAdapter(reminderArrayList, new ReminderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Reminder reminder, String dateDiff) {
                  if (Integer.parseInt(dateDiff)==1){
                      triggerAlarmManager(getTimeInterval("1"));
                      stopAlarm();
                  }else{
                      dialogMessage(dateDiff);
                  }
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
                                        FormatTanggalIDN formatTanggalIDN = new FormatTanggalIDN();
                                        final long dateDiff =formatTanggalIDN.dateDiff(reminder.getEventDate());

                                        if (dateDiff>0){

                                            reminderArrayList.add(reminder);

                                        }

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


    private void dialogMessage(String dateDiff) {
        new AlertDialog.Builder(ReminderListActivity.this,R.style.CustomDialogTheme)
                .setTitle("Reminder")
                .setMessage("Kajian yang anda ikuti akan berlangsung dalam "+ dateDiff +" hari lagi.")
                .setPositiveButton("OK", null)
                .show();

    }

    private void stopAlarm() {
        new AlertDialog.Builder(ReminderListActivity.this,R.style.CustomDialogTheme)
                .setTitle("Reminder")
                .setCancelable(false)
                .setMessage("Event yang anda ikuti akan segera dimulai.")
                .setPositiveButton("STOP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopAlarmManager();
                    }
                })
                .show();

    }


    private int getTimeInterval(String getInterval) {
        int interval = Integer.parseInt(getInterval);
        return interval;
    }

    public void triggerAlarmManager(int alarmTriggerTime) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, alarmTriggerTime);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);//get instance of alarm manager
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);//set alarm manager with entered timer by converting into milliseconds
    }

    public void stopAlarmManager() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        stopService(new Intent(ReminderListActivity.this, AlarmSoundService.class));
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmNotificationService.NOTIFICATION_ID);
    }
}
