package mobile.app.ayotaklim.activity.admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.event.EventListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;

public class AddJadwalEventActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView iconSearch, iconCalender1 , iconCalender2, iconBack;
    EditText edTextNama , edTextStartTime, edTextEndTime, editTextPemateri;
    String nama,sTime,eTime,pemateri,flag;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int recordId , eventId;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    Context context;
    JSONObject dataJson  = new JSONObject();
    JSONObject inputJson  = new JSONObject();
    String url="";
    Event event;
    Button btnCancel, btnSubmit , btnUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jadwal_event);
        context=AddJadwalEventActivity.this;
        sessionManager = new SessionManager(AddJadwalEventActivity.this);
        progressDialog = new ProgressDialog(this);
        flag = getIntent().getStringExtra("flag");
        eventId = getIntent().getIntExtra("eventId",0);
        if (flag.equalsIgnoreCase("SEARCH")){
            nama = getIntent().getStringExtra("nama");
            sTime = getIntent().getStringExtra("startTime");
            eTime = getIntent().getStringExtra("endTime");
            pemateri = getIntent().getStringExtra("pemateri");
            recordId = getIntent().getIntExtra("id",0);
            eventId = getIntent().getIntExtra("eventId",0);
            initView();
            initEventEdit();
        }else{
            initView();
        }

        iconCalender1.setOnClickListener(this);
        iconCalender2.setOnClickListener(this);
    }

    private void  initView(){
        iconSearch = findViewById(R.id.iconSearch);
        iconCalender1 = findViewById(R.id.iconStartTime);
        iconCalender2 = findViewById(R.id.iconEndTime);
        iconBack = findViewById(R.id.iconBack);
        edTextNama = findViewById(R.id.edTextNama);
        edTextStartTime = findViewById(R.id.edTextStartTime);
        edTextEndTime = findViewById(R.id.edTextEndTime);
        edTextStartTime.setEnabled(false);
        edTextEndTime.setEnabled(false);
        edTextStartTime.setTextColor(Color.BLACK);
        edTextEndTime.setTextColor(Color.BLACK);
        editTextPemateri = findViewById(R.id.edTextPemateri);
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddJadwalEventActivity.this,ListSearchPerformerActivity.class);
                intent.putExtra("nama",edTextNama.getText().toString());
                intent.putExtra("startTime",edTextStartTime.getText().toString());
                intent.putExtra("endTime",edTextEndTime.getText().toString());
                intent.putExtra("pemateri",editTextPemateri.getText().toString());
                intent.putExtra("eventId",eventId);
                intent.putExtra("flag","SEARCH");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initEventEdit(){
        edTextNama.setText(nama);
        edTextStartTime.setText(sTime);
        edTextEndTime.setText(eTime);
        editTextPemateri.setText(pemateri);
    }

    private void submit(){
        if (flag.equalsIgnoreCase("EDIT")){
            progressDialog.setMessage("sedang mengubah data...");
            progressDialog.show();
            url = Config.EDIT_USTADZ;
            try {
                inputJson.put("record_id",event.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            progressDialog.setMessage("sedang menyimpan data...");
            progressDialog.show();
            url = Config.ADD_EVENT_JADWAL;
        }
        try {
            inputJson.put("kegiatan", edTextNama.getText().toString());
            inputJson.put("waktu_mulai", edTextStartTime.getText().toString());
            inputJson.put("waktu_selesai", edTextEndTime.getText().toString());
            inputJson.put("sampai", "");
            inputJson.put("c_pembicara_id", recordId);
            inputJson.put("c_event_id", eventId);
            inputJson.put("dari", "");
            dataJson.put("data", inputJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data req : ",dataJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url , dataJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG",response.toString());
                Log.d("TAG","====================== SUCCESS ========================");
                try {
                    Boolean success  = response.getBoolean("success");
                    String message   = response.getString("message");
                    JSONObject data  = response.getJSONObject("data");
                    if (success){
                        addPembicara();
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

    private void addPembicara(){
        Log.d("pembicara id : ",String.valueOf(recordId));
        Log.d("event id : ",String.valueOf(eventId));
            JSONObject pemateriJson  = new JSONObject();
            JSONObject inputPemateriJson  = new JSONObject();
        try {
            inputPemateriJson.put("c_pembicara_id", recordId);
            inputPemateriJson.put("c_event_id", eventId);
            pemateriJson.put("data", inputPemateriJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data req : ",dataJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,  Config.ADD_EVENT_PEMBICARA , pemateriJson, new Response.Listener<JSONObject>() {
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
                        Intent intent = new Intent(AddJadwalEventActivity.this, EventListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == iconCalender1) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            edTextStartTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }


        if (v == iconCalender2) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            edTextEndTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }


}
