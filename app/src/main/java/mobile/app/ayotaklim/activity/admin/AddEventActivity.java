package mobile.app.ayotaklim.activity.admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import mobile.app.ayotaklim.activity.performer.PerformerListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.performer.Performer;

public class AddEventActivity extends AppCompatActivity implements
        View.OnClickListener{


    ImageView iconSearch, iconCalender1 , iconCalender2, iconBack;
    EditText edTextNama, edTextTopic , edTextStartDate, edTextEndDate, editTextVenue;
    String nama,topik,sDate,eDate,venue,flag;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int recordId;
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
        setContentView(R.layout.activity_add_event);
        context=AddEventActivity.this;
        sessionManager = new SessionManager(AddEventActivity.this);
        progressDialog = new ProgressDialog(this);
        flag = getIntent().getStringExtra("flag");
        if (flag.equalsIgnoreCase("SEARCH")){
            nama = getIntent().getStringExtra("nama");
            topik = getIntent().getStringExtra("topik");
            sDate = getIntent().getStringExtra("startDate");
            eDate = getIntent().getStringExtra("endDate");
            venue = getIntent().getStringExtra("venue");
            recordId = getIntent().getIntExtra("id",0);
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
        iconBack = findViewById(R.id.iconBack);
        iconCalender1 = findViewById(R.id.iconStartDate);
        iconCalender2 = findViewById(R.id.iconEndDate);
        edTextNama = findViewById(R.id.edTextNama);
        edTextTopic = findViewById(R.id.edTextTopic);
        edTextStartDate = findViewById(R.id.edTextStartDate);
        edTextEndDate = findViewById(R.id.edTextEndDate);
        edTextStartDate.setEnabled(false);
        edTextEndDate.setEnabled(false);
        edTextStartDate.setTextColor(Color.BLACK);
        edTextEndDate.setTextColor(Color.BLACK);
        editTextVenue = findViewById(R.id.edTextVenue);
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddEventActivity.this,ListSearchVenueActivity.class);
                intent.putExtra("nama",edTextNama.getText().toString());
                intent.putExtra("topik",edTextTopic.getText().toString());
                intent.putExtra("startDate",edTextStartDate.getText().toString());
                intent.putExtra("endDate",edTextEndDate.getText().toString());
                intent.putExtra("venue",editTextVenue.getText().toString());
                intent.putExtra("flag","SEARCH");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        btnCancel = findViewById(R.id.btnCancel);
        btnUpload = findViewById(R.id.btnUpload);
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
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initEventEdit(){
        edTextNama.setText(nama);
        edTextTopic.setText(topik);
        edTextStartDate.setText(sDate);
        edTextEndDate.setText(eDate);
        editTextVenue.setText(venue);
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
            url = Config.ADD_EVENT;
        }
        try {
            inputJson.put("nama_event", edTextNama.getText().toString());
            inputJson.put("topik", edTextTopic.getText().toString());
            inputJson.put("tgl_mulai", edTextStartDate.getText().toString());
            inputJson.put("tgl_berakhir", edTextEndDate.getText().toString());
            inputJson.put("c_venue_id", recordId);
            inputJson.put("banner_image", "kosong");
            dataJson.put("data", inputJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data req : ",dataJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url , dataJson, new Response.Listener<JSONObject>() {
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
                        Intent intent = new Intent(AddEventActivity.this, EventListActivity.class);
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
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            edTextStartDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (v == iconCalender2) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            edTextEndDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }
}
