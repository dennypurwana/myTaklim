package mobile.app.ayotaklim.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.event.EventActivity;
import mobile.app.ayotaklim.activity.splashscreen.SplashscreenActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;

public class AdminLoginActivity extends AppCompatActivity {
    SessionManager sessionManager;
    EditText username,password;
    Button btnLogin;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        sessionManager = new SessionManager(AdminLoginActivity.this);
        progressDialog = new ProgressDialog(this);
        initView();
    }
    private void initView(){
        username = findViewById(R.id.edTextEmail);
        password = findViewById(R.id.edTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){

        if (username.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Username tidak boleh kosong.",Toast.LENGTH_LONG).show();
        }else if(password.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Password tidak boleh kosong.",Toast.LENGTH_LONG).show();
        }else{
            submit();
        }
    }


    private void submit() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest sr = new StringRequest(Request.Method.POST,Config.LOGIN_ADMIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("RESPONSE , ",response.toString());
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");
                    String tokenAdmin = jsonObject.getString("data");

                    if (success){
                        sessionManager.createAdminSession();
                        sessionManager.createAccessTokenUpload(tokenAdmin);
                        Intent intent = new Intent(AdminLoginActivity.this, EventActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"username dan password tidak sesuai.",Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"username dan password tidak sesuai.",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",username.getText().toString());
                params.put("password",password.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(sr);

    }
}
