package mobile.app.ayotaklim.activity.splashscreen;

import android.Manifest;
import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.MainActivity;
import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.MapsActivity;
import mobile.app.ayotaklim.activity.event.EventActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;

public class SplashscreenActivity extends AppCompatActivity {

    Handler handler;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        handler=new Handler();
        sessionManager = new SessionManager(SplashscreenActivity.this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    createToken();
            }
        },3000);
    }

private void createToken(){

    JSONObject dataJson  = new JSONObject();
    try {
        dataJson.put("username", "taklim");
        dataJson.put("password", "t4kl1m345");

    } catch (JSONException e) {
        e.printStackTrace();
    }

    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Config.AUTH_LOGIN, dataJson, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.d("TAG",jsonObject.toString());
            Log.d("TAG","====================== SUCCESS ========================");
            try {
                boolean success = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                JSONObject data = jsonObject.getJSONObject("data");
                String token = data.getString("token");
                if (success){
                    sessionManager.createAccessToken(token);
                    Intent intent = new Intent(SplashscreenActivity.this, EventActivity.class);
                    startActivity(intent);
                    finish();
                }else {

                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                }

            }catch (JSONException e){

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
            headers.put("Content-Type", "application/json; charset=UTF-8");
            return headers;
        }
    };

    MyApplication.getInstance().addToRequestQueue(jsonRequest);

}
/*private void createToken() {
    StringRequest sr = new StringRequest(Request.Method.POST,Config.AUTH_LOGIN, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            try {

                JSONObject jsonObject = new JSONObject(response);
                boolean success = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                JSONObject data = jsonObject.getJSONObject("data");
                String token = data.getString("token");

                if (success){
                    sessionManager.createAccessToken(token);
                    Intent intent = new Intent(SplashscreenActivity.this, EventActivity.class);
                    startActivity(intent);
                    finish();
                }else {

                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                }

            }catch (JSONException e){

            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }){
        @Override
        protected Map<String,String> getParams(){
            Map<String,String> params = new HashMap<String, String>();
            params.put("username","taklim");
            params.put("password","t4kl1m345");


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
*/
}
