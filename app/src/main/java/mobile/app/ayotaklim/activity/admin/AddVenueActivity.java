package mobile.app.ayotaklim.activity.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
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
import mobile.app.ayotaklim.activity.venue.VenueListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.ConvertImageBase64;
import mobile.app.ayotaklim.utils.VolleyMultipartRequest;

public class AddVenueActivity extends AppCompatActivity  {

    EditText eTextNama, eTextAlamat, eTextPhone,
            eTextNoRek, eTextNoPam, eTextNoPln, eTextJmlJamaah, eTextImamRutin,
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
    String fileName;
    String fileUrl;
    Bitmap bitmapImage;
    private int PLACE_PICKER_REQUEST = 1;


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
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCt96emScioK47Vcqx5rZgwDz5mtHmUs0U");
        }
        PlacesClient placesClient = Places.createClient(AddVenueActivity.this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Info place", "Place: " + place.getName() + ", " + place.getId());
                eTextAlamat.setText(String.valueOf(place.getAddress()));
                String latLang = place.getLatLng().toString();
                System.out.println(latLang.substring(latLang.indexOf("("),latLang.indexOf(",")));
                eTextLatitude.setText(latLang.substring(latLang.indexOf("("),latLang.indexOf(",")));
                eTextLongitude.setText(latLang.substring(latLang.indexOf(","),latLang.indexOf(")")));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error place", "An error occurred: " + status);
            }
        });

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
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME,Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(AddVenueActivity.this);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            }
        });

    }

    private void initView(){
        eTextNama = findViewById(R.id.edTextNama);
        eTextImamRutin = findViewById(R.id.edTextNamaImamRutin);
        eTextAlamat = findViewById(R.id.edTextAddress);
        eTextDesc = findViewById(R.id.edTextDesc);
        eTextPhone = findViewById(R.id.edTextPhone);
        eTextEmail = findViewById(R.id.edTextEmail);
        eTextDKM = findViewById(R.id.edTextDKM);
        eTextDKMPhone = findViewById(R.id.edTextPhoneDKM);
        eTextNoRek = findViewById(R.id.edTextNoRek);
        eTextNoPln = findViewById(R.id.edTextNoPLN);
        eTextNoPam = findViewById(R.id.edTextNoPAM);
        eTextJmlJamaah  = findViewById(R.id.edTextJmlJamaah);
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
        eTextImamRutin.setText(venue.getNamaImamRutin());
        eTextNoRek.setText(venue.getNoRek());
        eTextNoPln.setText(venue.getNoPln());
        eTextNoPam.setText(venue.getNoPam());
        eTextJmlJamaah.setText(venue.getJmlJamaah());

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
                    uploadBitmap(ConvertImageBase64.getResizedBitmap(bitmapImage,1000,1000));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddVenueActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            bitmapImage= (Bitmap) data.getExtras().get("data");
            imageVenue.setImageBitmap(bitmapImage);
            saveImage(bitmapImage);
            uploadBitmap(ConvertImageBase64.getResizedBitmap(bitmapImage,1000,1000));
            Toast.makeText(AddVenueActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("Place baru : ", "Place: " + place.getName() + ", " + place.getId());
                eTextAlamat.setText(String.valueOf(place.getAddress()));
                String latLang = place.getLatLng().toString();
                System.out.println(latLang);
                System.out.println(latLang.substring(latLang.indexOf("(")+1,latLang.indexOf(",")));
                eTextLatitude.setText(latLang.substring(latLang.indexOf("(")+1,latLang.indexOf(",")));
                eTextLongitude.setText(latLang.substring(latLang.indexOf(",")+1,latLang.indexOf(")")));
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
            inputJson.put("jumlah_jamaah_subuh", eTextJmlJamaah.getText().toString());
            inputJson.put("no_rek_bank", eTextNoRek.getText().toString());
            inputJson.put("no_rek_pln", eTextNoPln.getText().toString());
            inputJson.put("no_rek_pam", eTextNoPam.getText().toString());
            inputJson.put("nama_imam_rutin", eTextImamRutin.getText().toString());
            inputJson.put("dkm_phone", eTextDKMPhone.getText().toString());
            inputJson.put("longitude", eTextLongitude.getText().toString());
            inputJson.put("latitude", eTextLatitude.getText().toString());
            inputJson.put("imagebase64", fileName);
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


    public  byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
                        Log.d("error upload : ",error.toString());
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


}
