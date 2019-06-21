package mobile.app.ayotaklim.activity.event;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.MainActivity;
import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.reminder.ReminderListActivity;
import mobile.app.ayotaklim.activity.splashscreen.SplashscreenActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;

public class RegisterEventActivity extends AppCompatActivity {

    Button btnSubmit, btnCancel;
    Context context;
    ImageView _bannerImage, _checkIconNama, _closeIconNama,
              _checkIconAddress, _closeIconAddress, _checkIconIdCard,_closeIconIdCard,
              _checkIconPhone,_closeIconPhone,_checkIconEmail,_closeIconEmail;
    EditText  _edTextNama,_edTextAddress,_edTextIdCard,_edTextPhone,_edTextEmail;
    Boolean errorInputName =false,
            errorInputAddress =false,
            errorInputKtp =false,
            errorInputPhone =false,
            errorInputEmail =false
    ;
     SessionManager sessionManager;
     ProgressDialog progressDialog;
     boolean isMember = false;
     boolean isAlreadyFollowEvent = false;
     int memberId;
    int flagEvent, flagMember;
     Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        context=RegisterEventActivity.this;
        sessionManager = new SessionManager(RegisterEventActivity.this);
        progressDialog = new ProgressDialog(this);
        event = (Event) getIntent().getSerializableExtra("Event");
        _bannerImage = findViewById(R.id.imageEvent);
        /*checklist*/
        _checkIconNama = findViewById(R.id.checkIconNama);
        _checkIconAddress = findViewById(R.id.checkIconAddress);
        _checkIconIdCard = findViewById(R.id.checkIconIdCard);
        _checkIconPhone  = findViewById(R.id.checkIconPhone);
        _checkIconEmail = findViewById(R.id.checkIconEmail);
        /*close*/
        _closeIconNama = findViewById(R.id.closeIconNama);
        _closeIconAddress = findViewById(R.id.closeIconAddress);
        _closeIconIdCard = findViewById(R.id.closeIconIdCard);
        _closeIconPhone  = findViewById(R.id.closeIconPhone);
        _closeIconEmail = findViewById(R.id.closeIconEmail);
        /*input*/
        _edTextNama = findViewById(R.id.edTextNama);
        _edTextAddress = findViewById(R.id.edTextAddress);
        _edTextIdCard = findViewById(R.id.edTextIdCard);
        _edTextPhone  = findViewById(R.id.edTextPhone);
        _edTextEmail = findViewById(R.id.edTextEmail);
        Picasso.get()
                .load(Config.IMAGE_URL+event.getBannerImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(_bannerImage);;

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });


        _edTextNama.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                   if(_edTextNama.getText().toString().length()>0){
                       _checkIconNama.setVisibility(View.VISIBLE);
                       _closeIconNama.setVisibility(View.GONE);
                   }
                   else{
                       _checkIconNama.setVisibility(View.GONE);
                       _closeIconNama.setVisibility(View.VISIBLE);
                   }

                }
                else{
                    if(errorInputName){
                        _closeIconNama.setVisibility(View.GONE);
                    }else{
                        _closeIconNama.setVisibility(View.GONE);
                        _checkIconNama.setVisibility(View.GONE);
                    }
                }

            }
        });


        _edTextAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if(_edTextAddress.getText().toString().length()>0){
                        _checkIconAddress.setVisibility(View.VISIBLE);
                        _closeIconAddress.setVisibility(View.GONE);
                    }
                    else{
                        _checkIconAddress.setVisibility(View.GONE);
                        _closeIconAddress.setVisibility(View.VISIBLE);
                    }

                }
                else{
                    if(errorInputAddress){
                        _closeIconAddress.setVisibility(View.GONE);
                        _checkIconAddress.setVisibility(View.GONE);
                    }else{
                        _closeIconAddress.setVisibility(View.GONE);
                        _checkIconAddress.setVisibility(View.GONE);
                    }
                }

            }
        });


        _edTextIdCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if(_edTextIdCard.getText().toString().length()>0){
                        _checkIconIdCard.setVisibility(View.VISIBLE);
                        _closeIconIdCard.setVisibility(View.GONE);
                    }
                    else{
                        _checkIconIdCard.setVisibility(View.GONE);
                        _closeIconIdCard.setVisibility(View.VISIBLE);
                    }

                }
                else{
                    if(errorInputKtp){
                        _closeIconIdCard.setVisibility(View.GONE);
                        _checkIconIdCard.setVisibility(View.GONE);
                    }else{
                        _closeIconIdCard.setVisibility(View.GONE);
                        _checkIconIdCard.setVisibility(View.GONE);
                    }
                }

            }
        });

        _edTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if(_edTextPhone.getText().toString().length()>0){
                        _checkIconPhone.setVisibility(View.VISIBLE);
                        _closeIconPhone.setVisibility(View.GONE);
                    }
                    else{
                        _checkIconPhone.setVisibility(View.GONE);
                        _closeIconPhone.setVisibility(View.VISIBLE);
                    }

                }
                else{
                    if(errorInputPhone){
                        _closeIconPhone.setVisibility(View.GONE);
                        _checkIconPhone.setVisibility(View.GONE);
                    }else{
                        _closeIconPhone.setVisibility(View.GONE);
                        _checkIconPhone.setVisibility(View.GONE);
                    }
                }

            }
        });

        _edTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if(_edTextEmail.getText().toString().length()>0){
                        _checkIconEmail.setVisibility(View.VISIBLE);
                        _closeIconEmail.setVisibility(View.GONE);
                    }
                    else{
                        _checkIconEmail.setVisibility(View.GONE);
                        _closeIconEmail.setVisibility(View.VISIBLE);
                    }

                }
                else{
                    if(errorInputEmail){
                        _closeIconEmail.setVisibility(View.GONE);
                        _checkIconEmail.setVisibility(View.GONE);
                    }else{
                        _closeIconEmail.setVisibility(View.GONE);
                        _checkIconEmail.setVisibility(View.GONE);
                    }
                }

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }

    private  void checkExistingMember(){
        String url=Config.CHECK_EXISTING_MEMBER;
        Log.d("API : ",url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals(null)) {
                    Log.e("TAG", "CHECK MEMBER EXIST: " + response.toString());
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray venueResponse=responseObj.getJSONArray("items");
                        if(venueResponse.length()>0) {

                            for (int i = 0; i < venueResponse.length(); i++) {
                                try {
                                    JSONObject jsonObject = venueResponse.getJSONObject(i);
                                    String phone = jsonObject.getString("no_telepon");
                                    String email = jsonObject.getString("email");
                                    memberId = jsonObject.getInt("id");

                                    if (phone.equals(_edTextPhone.getText().toString())){
                                        isMember = true;
                                        sessionManager.createMemberSession();
                                        sessionManager.createMemberId(memberId);
                                        sessionManager.createMemberPhone(phone);
                                        sessionManager.createMemberEmail(email);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }
                        Log.d("isMember" ,String.valueOf(isMember));
                            if (isMember){
                                checkMember();
                                //registerToEvent(event.getId(),memberId);
                            }else{
                               submit();
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
               submit();
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
    void checkMember(){
        String url=Config.CHECK_EVENT_MEMBER;
        Log.d("API : ",url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals(null)) {
                    try {
                        progressDialog.dismiss();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("items");
                        if(vResponse.length()>0) {
                            for (int i = 0; i < vResponse.length(); i++) {
                                try {
                                    JSONObject jsonObject = vResponse.getJSONObject(i);
                                    int eventId = jsonObject.getInt("c_event_id");
                                    int memberId = jsonObject.getInt("c_member_id");
                                    if (event.getId()==eventId&&sessionManager.getMemberId()== memberId){
                                       isAlreadyFollowEvent = true;
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }
                            if (isAlreadyFollowEvent){
                                dialogMessage("Anda telah terdaftar pada event ini sebelumnya.");
                            }else{
                                registerToEvent(event.getId(),memberId);
                            }

                        }else{

                        }
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }

                } else {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    private void submit(){
        JSONObject dataJson  = new JSONObject();
            JSONObject memberJson  = new JSONObject();

            try {
                memberJson.put("nama", _edTextNama.getText().toString());
                memberJson.put("alamat", _edTextAddress.getText().toString());
                memberJson.put("no_ktp", _edTextIdCard.getText().toString());
                memberJson.put("no_telepon", _edTextPhone.getText().toString());
                memberJson.put("email", _edTextEmail.getText().toString());
                memberJson.put("image_profile", "dadasd");
                dataJson.put("data", memberJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("data req : ",dataJson.toString());
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Config.REGISTER_MEMBER, dataJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("TAG REGISTER RESPONSE",response.toString());
                    Log.d("TAG","====================== SUCCESS ========================");
                    try {
                        Boolean success  = response.getBoolean("success");
                        String message   = response.getString("message");
                        JSONObject data  = response.getJSONObject("data");
                        int member_id    = data.getInt("id");
                        String member_phone = data.getString("no_telepon");
                        String member_email = data.getString("email");
                        if (success){
                            sessionManager.createMemberSession();
                            sessionManager.createMemberId(member_id);
                            sessionManager.createMemberPhone(member_phone);
                            sessionManager.createMemberEmail(member_email);
                            registerToEvent(event.getId(),member_id);
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


    private void registerToEvent(int eventId , int memberId){
        JSONObject dataJson  = new JSONObject();
        JSONObject eventMemberJson  = new JSONObject();

        try {
            eventMemberJson.put("c_event_id", eventId);
            eventMemberJson.put("c_member_id", memberId);
            dataJson.put("data", eventMemberJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data member req : ",dataJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Config.REGISTER_EVENT_MEMBER, dataJson, new Response.Listener<JSONObject>() {
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
                        dialogMessage("Anda telah berhasil mendaftar pada event ini. Silahkan lihat menu reminder pada menu aplikasi.");
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

    private void validate(){

        if (_edTextNama.getText().toString().equals("")){
            errorInputName = true;
            _closeIconNama.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"Input nama tidak boleh kosong.",Toast.LENGTH_LONG).show();
        }else if (_edTextAddress.getText().toString().equals("")){
            _closeIconAddress.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"Input alamat tidak boleh kosong.",Toast.LENGTH_LONG).show();
        } else if (_edTextIdCard.getText().toString().equals("")){
            _closeIconIdCard.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"Input no ktp tidak boleh kosong.",Toast.LENGTH_LONG).show();
        } else if (_edTextPhone.getText().toString().equals("")){
            _closeIconPhone.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"Input no hp tidak boleh kosong.",Toast.LENGTH_LONG).show();
        } else if (_edTextEmail.getText().toString().equals("")){
            _closeIconEmail.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"Input email tidak boleh kosong.",Toast.LENGTH_LONG).show();
        }else{
            //submit();
            //dialogMessage();
            checkExistingMember();
        }

    }

    private void dialogMessage(String message) {
        new AlertDialog.Builder(context,R.style.CustomDialogTheme)
                .setTitle("Daftar Sukses")
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RegisterEventActivity.this, ReminderListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
