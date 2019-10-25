package mobile.app.ayotaklim.activity.admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.performer.PerformerListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.performer.Performer;
import mobile.app.ayotaklim.utils.ConvertImageBase64;
import mobile.app.ayotaklim.utils.VolleyMultipartRequest;

public class AddPerformerActivity  extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    EditText eTextNama, eTextAlamat, eTextTglLahir, eTextPendidikan,
             eTextDesc, eTextPhone, eTextEmail, eTextFB, eTextIG , eTextYoutube;
    Button btnCancel, btnSubmit , btnUpload;
    ImageView imageUstadz , iconBack;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String flag;
    Performer performer;
    Context context;
    JSONObject dataJson  = new JSONObject();
    JSONObject inputJson  = new JSONObject();
    String url="";
    private int GALLERY = 3, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/ayotaklim";
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    ImageView iconMaps, iconCalender;
    private  int MY_SOCKET_TIMEOUT_MS= 10000;
    String fileName;
    String fileUrl;
    String [] months = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    String [] dates = {"01","02","03","04","05","06","07","08","09","10","11","12","13","14",
                       "15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_performer);
        context=AddPerformerActivity.this;
        sessionManager = new SessionManager(AddPerformerActivity.this);
        progressDialog = new ProgressDialog(this);
        flag = getIntent().getStringExtra("flag");
        requestMultiplePermissions();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCt96emScioK47Vcqx5rZgwDz5mtHmUs0U");
        }
        PlacesClient placesClient = Places.createClient(AddPerformerActivity.this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Info place", "Place: " + place.getName() + ", " + place.getId());
                eTextAlamat.setText(String.valueOf(place.getAddress()));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error place", "An error occurred: " + status);
            }
        });

        if (flag.equalsIgnoreCase("EDIT")){
            performer = (Performer) getIntent().getSerializableExtra("Performer");
            initView();
            initViewEdit();
        }else {
            initView();
        }


    }


private void initView(){
    eTextNama = findViewById(R.id.edTextNama);
    eTextAlamat = findViewById(R.id.edTextAddress);
    eTextTglLahir = findViewById(R.id.edTextTglLahir);
    eTextPendidikan = findViewById(R.id.edTextPendidikan);
    eTextDesc = findViewById(R.id.edTextDesc);
    eTextPhone = findViewById(R.id.edTextPhone);
    eTextEmail = findViewById(R.id.edTextEmail);
    eTextFB = findViewById(R.id.edTextFB);
    eTextIG = findViewById(R.id.edTextIG);
    eTextYoutube = findViewById(R.id.edTextYoutube);
    imageUstadz = findViewById(R.id.imagePerformer);
    iconBack = findViewById(R.id.iconBack);
    iconMaps = findViewById(R.id.iconMaps);
    iconCalender = findViewById(R.id.iconDateBirth);
    btnCancel = findViewById(R.id.btnCancel);
    btnUpload = findViewById(R.id.btnUpload);
    btnSubmit = findViewById(R.id.btnSubmit);
    eTextTglLahir.setEnabled(false);
    eTextTglLahir.setTextColor(Color.BLACK);
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
    iconCalender.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddPerformerActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            eTextTglLahir.setText(year+ "-" + months[monthOfYear] + "-" + dayOfMonth);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    });
    iconMaps.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(AddPerformerActivity.this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
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
        eTextNama.setText(performer.getNama());
        eTextAlamat.setText(performer.getAlamat());
        eTextPendidikan.setText(performer.getPendidikan());
        eTextDesc.setText(performer.getDeskripsi());
        eTextTglLahir.setText(performer.getTglLahir());
        eTextPhone.setText(performer.getPhone());
        eTextEmail.setText(performer.getEmail());
        eTextFB.setText(performer.getFacebook());
        eTextIG.setText(performer.getInstagram());
        eTextYoutube.setText(performer.getYoutube());
}

    private void submit(){
        if (flag.equalsIgnoreCase("EDIT")){
            progressDialog.setMessage("sedang mengubah data...");
            progressDialog.show();
               url = Config.EDIT_USTADZ;
               try {
                inputJson.put("record_id",performer.getRecord_id());
               } catch (JSONException e) {
                  e.printStackTrace();
               }
        }else{
            progressDialog.setMessage("sedang menyimpan data...");
            progressDialog.show();
            url = Config.ADD_USTADZ;
        }
        try {
            inputJson.put("nama", eTextNama.getText().toString());
            inputJson.put("alamat", eTextAlamat.getText().toString());
            inputJson.put("tgl_lahir", eTextTglLahir.getText().toString());
            inputJson.put("pendidikan", eTextPendidikan.getText().toString());
            inputJson.put("deskripsi", eTextDesc.getText().toString());
            inputJson.put("phone", eTextPhone.getText().toString());
            inputJson.put("email", eTextEmail.getText().toString());
            inputJson.put("instagram", eTextIG.getText().toString());
            inputJson.put("facebook", eTextFB.getText().toString());
            inputJson.put("youtube", eTextYoutube.getText().toString());
            inputJson.put("imagebase64", fileName);
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
                        Intent intent = new Intent(AddPerformerActivity.this, PerformerListActivity.class);
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
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(AddPerformerActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageUstadz.setImageBitmap(bitmap);
                    uploadBitmap(ConvertImageBase64.getResizedBitmap(bitmap,1000,1000));

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddPerformerActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageUstadz.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            uploadBitmap(ConvertImageBase64.getResizedBitmap(thumbnail,1000,1000));
            Toast.makeText(AddPerformerActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("Place baru : ", "Place: " + place.getName() + ", " + place.getId());
                eTextAlamat.setText(String.valueOf(place.getAddress()));
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Error place : ", status.getStatusMessage());
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
                            fileUrl = dataObj.getString("file_url");
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

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }



    public  byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
