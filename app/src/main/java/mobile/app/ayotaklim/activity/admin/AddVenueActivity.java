package mobile.app.ayotaklim.activity.admin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.internal.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import mobile.app.ayotaklim.Manifest;
import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.performer.PerformerListActivity;
import mobile.app.ayotaklim.activity.venue.VenueListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.performer.Performer;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.ConvertImageBase64;

public class AddVenueActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    EditText eTextNama, eTextAlamat, eTextPhone,
            eTextDesc, eTextEmail, eTextDKM,
            eTextDKMPhone , eTextLongitude, eTextLatitude;
    ImageView imageVenue , iconMaps, iconBack;
    Button btnCancel, btnSubmit , btnUpload;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    Context context;
    private int GALLERY = 3, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/ayotaklim";
    String flag;
    Venue venue;
    JSONObject dataJson  = new JSONObject();
    JSONObject inputJson  = new JSONObject();
    String url;
    String imageBase64;
    Bitmap bitmapImage;
    private int PLACE_PICKER_REQUEST = 1;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue);
        context=AddVenueActivity.this;
        sessionManager = new SessionManager(AddVenueActivity.this);
        progressDialog = new ProgressDialog(this);
        requestMultiplePermissions();
        flag = getIntent().getStringExtra("flag");
        requestMultiplePermissions();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        if (flag.equalsIgnoreCase("EDIT")){
            venue = (Venue) getIntent().getSerializableExtra("Venue");
            initView();
            initViewEdit();
        }else {
            initView();
        }
        iconMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {

                    startActivityForResult(builder.build(AddVenueActivity.this), PLACE_PICKER_REQUEST);
                    // startActivityForResult(builder.build(AddVenueActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initView(){
        eTextNama = findViewById(R.id.edTextNama);
        eTextAlamat = findViewById(R.id.edTextAddress);
        eTextDesc = findViewById(R.id.edTextDesc);
        eTextPhone = findViewById(R.id.edTextPhone);
        eTextEmail = findViewById(R.id.edTextEmail);
        eTextDKM = findViewById(R.id.edTextDKM);
        eTextDKMPhone = findViewById(R.id.edTextPhoneDKM);
        eTextLongitude = findViewById(R.id.edTextLong);
        eTextLatitude = findViewById(R.id.edTextLat);
        eTextLongitude.setEnabled(false);
        eTextLatitude.setEnabled(false);
        eTextLongitude.setTextColor(Color.BLACK);
        eTextLatitude.setTextColor(Color.BLACK);
        imageVenue = findViewById(R.id.imageVenue);
        iconBack = findViewById(R.id.iconBack);
        iconMaps = findViewById(R.id.iconMaps);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnUpload = findViewById(R.id.btnUpload);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
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



    private void initViewEdit(){
        eTextNama.setText(venue.getNama());
        eTextAlamat.setText(venue.getAlamat());
        eTextDesc.setText(venue.getDeskripsi());
        eTextPhone.setText(venue.getNoTlp());
        eTextEmail.setText(venue.getEmail());
        eTextDKM.setText(venue.getDkm());
        eTextDKMPhone.setText(venue.getDkmPhone());
        eTextLongitude.setText(String.valueOf(venue.getLongitude()));
        eTextLatitude.setText(String.valueOf(venue.getLatitude()));
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
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == this.RESULT_CANCELED) {
//            return;
//        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmapImage);
                    Toast.makeText(AddVenueActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageVenue.setImageBitmap(bitmapImage);
                   // imageBase64 = ConvertImageBase64.convertToBase64(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddVenueActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            bitmapImage= (Bitmap) data.getExtras().get("data");
            imageVenue.setImageBitmap(bitmapImage);
            saveImage(bitmapImage);
           // imageBase64 = ConvertImageBase64.convertToBase64(thumbnail);
            Toast.makeText(AddVenueActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format(
                        "Place: %s \n" +
                                "Alamat: %s \n" +
                                "Latlng %s \n", place.getName(), place.getAddress(), place.getLatLng().latitude+" "+place.getLatLng().longitude);
                eTextAlamat.setText(String.valueOf(place.getAddress()));
                eTextLongitude.setText(String.valueOf(place.getLatLng().longitude));
                eTextLatitude.setText(String.valueOf(place.getLatLng().latitude));
            }
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


    private void submit(){
        if (flag.equalsIgnoreCase("EDIT")){
            progressDialog.setMessage("sedang mengubah data...");
            url = Config.EDIT_VENUE;
            try {
                inputJson.put("record_id", venue.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            progressDialog.setMessage("sedang menyimpan data...");
            url = Config.ADD_VENUE;
        }

        progressDialog.show();

        try {
            inputJson.put("nama", eTextNama.getText().toString());
            inputJson.put("alamat", eTextAlamat.getText().toString());
            inputJson.put("no_tlp", eTextPhone.getText().toString());
            inputJson.put("venue_phone", "");
            inputJson.put("deskripsi", eTextDesc.getText().toString());
            inputJson.put("phone", eTextPhone.getText().toString());
            inputJson.put("email", eTextEmail.getText().toString());
            inputJson.put("dkm", eTextDKM.getText().toString());
            inputJson.put("dkm_phone", eTextDKMPhone.getText().toString());
            inputJson.put("longitude", eTextLongitude.getText().toString());
            inputJson.put("latitude", eTextLatitude.getText().toString());
            inputJson.put("imagebase64", "test");
            dataJson.put("data", inputJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data req : ",dataJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, dataJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.d("TAG",response.toString());
                Log.d("TAG","====================== SUCCESS ========================");
                try {
                    Boolean success  = response.getBoolean("success");
                    String message   = response.getString("message");
                    JSONObject data  = response.getJSONObject("data");
                    int id = data.getInt("id");
                    if (success){
                        //uploadImage(id);
                        Intent intent = new Intent(AddVenueActivity.this, VenueListActivity.class);
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


    private void uploadImage(int record_id){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        Log.d("encode image : ",encodedImage);
            url = Config.EDIT_VENUE;
            try {
                inputJson.put("record_id", record_id);
                inputJson.put("imagebase64","tstasd");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, dataJson, new Response.Listener<JSONObject>() {
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
                        Intent intent = new Intent(AddVenueActivity.this, VenueListActivity.class);
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

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(jsonRequest);
        //MyApplication.getInstance().addToRequestQueue(jsonRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                        }


                        if (report.isAnyPermissionPermanentlyDenied()) {

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
