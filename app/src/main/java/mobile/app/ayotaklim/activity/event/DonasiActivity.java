package mobile.app.ayotaklim.activity.event;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.admin.AddDonasiActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Donasi;

public class DonasiActivity extends AppCompatActivity {

    Button  btnAdd;
    SessionManager sessionManager;
    Donasi donasi;
    ImageView bannerDonasi;
    TextView labelLembaga,labelDeskripsi,labelNoRek1,labelNoRek2,labelNamaBank1,labelNamaBank2,
            labelNamaRek1,labelNamaRek2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donasi);
        sessionManager = new SessionManager(DonasiActivity.this);
        btnAdd = findViewById(R.id.btnAdd);
        if (sessionManager.isAdmin()) {
            btnAdd.setVisibility(View.VISIBLE);
        }else{
            btnAdd.setVisibility(View.GONE);
        }
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DonasiActivity.this, AddDonasiActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        getData();
    }


    private void initView(){

        bannerDonasi = findViewById(R.id.bannerDonasi);
        labelDeskripsi = findViewById(R.id.labelDeskripsi);
        labelLembaga = findViewById(R.id.labelLembaga);
        labelNamaBank1 = findViewById(R.id.labelNamaBank1);
        labelNamaBank2 =findViewById(R.id.labelNamaBank2);
        labelNoRek1 = findViewById(R.id.labelNoRek1);
        labelNoRek2 = findViewById(R.id.labelNoRek2);
        labelNamaRek1 = findViewById(R.id.labelNamaRek1);
        labelNamaRek2 = findViewById(R.id.labelNamaRek2);
        labelLembaga.setText(donasi.getNamaLembaga());
        labelDeskripsi.setText(donasi.getDeskripsi());
        labelNamaRek1.setText("Nama Rekening "+donasi.getNamaRek1());
        labelNamaRek2.setText("Nama Rekening "+donasi.getNamaRek2());
        labelNoRek1.setText("No Rekening "+donasi.getNoRekBank1());
        labelNoRek2.setText("No Rekening "+donasi.getNoRekBank2());
        labelNamaBank1.setText("Bank "+donasi.getNamaBank1());
        labelNamaBank2.setText("Bank "+donasi.getNamaBank2());
        Picasso.get()
                .load(Config.IMAGE_URL+donasi.getImageDonasi())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(bannerDonasi);

    }

    private void getData(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url= Config.GET_DONASI;
        Log.d("API : ",url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("response donasi : " , response);
                if (!response.equals(null)) {
                    progressDialog.hide();
                    Log.e("TAG", "donasi response: " + response.toString());
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject responseObj = jsonResponse.getJSONObject("data");
                        JSONArray vResponse=responseObj.getJSONArray("items");
                        if(vResponse.length()>0) {

                                try {
                                    JSONObject jsonObject = vResponse.getJSONObject(vResponse.length()-1);
                                    donasi = new Donasi();
                                    donasi.setImageDonasi(jsonObject.getString("image_donasi"));
                                    donasi.setNamaLembaga(jsonObject.getString("nama_lembaga"));
                                    donasi.setDeskripsi(jsonObject.getString("deskripsi"));
                                    donasi.setNoRekBank1(jsonObject.getString("no_rek_1"));
                                    donasi.setNoRekBank2(jsonObject.getString("no_rek_2"));
                                    donasi.setNamaRek1(jsonObject.getString("nama_rek_1"));
                                    donasi.setNamaRek2(jsonObject.getString("nama_rek_2"));
                                    donasi.setNamaBank1(jsonObject.getString("nama_bank_1"));
                                    donasi.setNamaBank2(jsonObject.getString("nama_bank_2"));
                                    initView();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
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
