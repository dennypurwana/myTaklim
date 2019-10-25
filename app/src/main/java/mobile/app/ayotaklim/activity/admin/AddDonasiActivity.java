package mobile.app.ayotaklim.activity.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.event.DonasiActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.utils.ConvertImageBase64;
import mobile.app.ayotaklim.utils.VolleyMultipartRequest;

public class AddDonasiActivity extends AppCompatActivity implements
        View.OnClickListener{


    SessionManager sessionManager;
    ProgressDialog progressDialog;
    Context context;
    JSONObject dataJson  = new JSONObject();
    JSONObject inputJson  = new JSONObject();
    String url="";
    private int GALLERY = 3, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/ayotaklim";
    private int PLACE_PICKER_REQUEST = 1;
    String fileName, filePath, flag;
    ImageView bannerDonasi,iconBack;
    EditText edTextNamaLembaga, edTextDeskripsi , edTextNamaBank, edTextNamaBank2, edTextNoRek ,edTextNoRek2, edTextNamaRek, edTextNamaRek2;
    Button btnCancel, btnSubmit , btnUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donasi);
        context= AddDonasiActivity.this;
        sessionManager = new SessionManager(AddDonasiActivity.this);
        progressDialog = new ProgressDialog(this);
        initView();

//            Picasso.get()
//                    .load(Config.IMAGE_URL+fileName)
//                    .placeholder(R.drawable.placeholder_image)
//                    .error(R.drawable.placeholder_image)
//                    .fit()
//                    .into(bannerEvent);

    }

    private void  initView(){
        bannerDonasi = findViewById(R.id.imageDonasi);
        iconBack = findViewById(R.id.iconBack);

        edTextDeskripsi = findViewById(R.id.edTextDesc);
        edTextNamaLembaga = findViewById(R.id.edTextNamaLembaga);
        edTextNamaBank = findViewById(R.id.edTextNamaBank);
        edTextNamaBank2= findViewById(R.id.edTextNamaBank2);
        edTextNoRek= findViewById(R.id.edTextNoRek1);
        edTextNoRek2 = findViewById(R.id.edTextNoRek2);
        edTextNamaRek = findViewById(R.id.edTextNamaRek1);
        edTextNamaRek2  = findViewById(R.id.edTextNamaRek2);
        btnCancel = findViewById(R.id.btnCancel);
        btnUpload = findViewById(R.id.btnUpload);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
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


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(AddDonasiActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    bannerDonasi.setImageBitmap(bitmap);
                    uploadBitmap(ConvertImageBase64.getResizedBitmap(bitmap,400,400));

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddDonasiActivity.this, "Save Image Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            bannerDonasi.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            uploadBitmap(ConvertImageBase64.getResizedBitmap(thumbnail,1000,1000));
            Toast.makeText(AddDonasiActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }

    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void initEventEdit(){
    }

    private void submit(){
       /* if (flag.equalsIgnoreCase("EDIT")){
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
        */
        progressDialog.setMessage("sedang menyimpan data...");
        progressDialog.show();
        url = Config.CREATE_DONASI;
        try {
            inputJson.put("deskripsi", edTextDeskripsi.getText().toString());
            inputJson.put("nama_bank_2", edTextNamaBank2.getText().toString());
            inputJson.put("nama_bank_1", edTextNamaBank.getText().toString());
            inputJson.put("nama_rek_2", edTextNamaRek2.getText().toString());
            inputJson.put("nama_rek_1", edTextNamaRek.getText().toString());
            inputJson.put("no_rek_2", edTextNoRek2.getText().toString());
            inputJson.put("no_rek_1", edTextNoRek.getText().toString());
            inputJson.put("logo_bank_1", "");
            inputJson.put("logo_bank_2", "");
            inputJson.put("nama_lembaga", edTextNamaLembaga.getText().toString());
            inputJson.put("image_donasi", fileName);
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
                        Intent intent = new Intent(AddDonasiActivity.this, DonasiActivity.class);
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


    private void uploadBitmap(final Bitmap bitmap) {
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Config.UPLOAD_FILE,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.d("response ", obj.toString());
                            JSONObject dataObj = obj.getJSONObject("data");
                            fileName = dataObj.getString("file_name");
                            filePath = dataObj.getString("file_url");
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload Gagal, Silahkan upload ulang.", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "bearer "+sessionManager.getTokenUpload());
                //headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                Log.d("data part : ",getFileDataFromDrawable(bitmap).toString());
                Log.d("data : ","bitmap");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }



    public  byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    @Override
    public void onClick(View v) {

    }
}
