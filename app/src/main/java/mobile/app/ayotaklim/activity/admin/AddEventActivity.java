package mobile.app.ayotaklim.activity.admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

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
import mobile.app.ayotaklim.activity.event.EventListActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.MyApplication;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.utils.ConvertImageBase64;
import mobile.app.ayotaklim.utils.VolleyMultipartRequest;

public class AddEventActivity extends AppCompatActivity implements
        View.OnClickListener{


    ImageView iconSearch,iconUstadz, iconCalender1 , iconCalender3, iconCalender4, iconCalender2, iconBack ,bannerEvent;
    EditText edTextNama, edTextTopic , edTextStartDate, edTextEndDate, editTextVenue ,edTextStartTime, edTextEndTime, editTextPemateri;
    String nama,topik,sDate,eDate,venue,flag,ustadz, eTime, sTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int cVenueId,cPemateriId;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    Context context;
    JSONObject dataJson  = new JSONObject();
    JSONObject inputJson  = new JSONObject();
    String url="";
    private int GALLERY = 3, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/ayotaklim";
    private int PLACE_PICKER_REQUEST = 1;
    Event event;
    String fileName,fileUrl, dateRequest;
    Button btnCancel, btnSubmit , btnUpload;
    String [] months = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    String [] monthsInIDN = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        context=AddEventActivity.this;
        sessionManager = new SessionManager(AddEventActivity.this);
        progressDialog = new ProgressDialog(this);
        flag = getIntent().getStringExtra("flag");

        if (flag.equalsIgnoreCase("FROM_EVENT")){

            nama = getIntent().getStringExtra("nama");
            topik = getIntent().getStringExtra("topik");
            sDate = getIntent().getStringExtra("startDate");
            eDate = getIntent().getStringExtra("endDate");
            eDate = getIntent().getStringExtra("endDate");
            sTime = getIntent().getStringExtra("startTime");
            eTime = getIntent().getStringExtra("endTime");
            venue = getIntent().getStringExtra("venue");
            ustadz = getIntent().getStringExtra("pemateri");
            fileName = getIntent().getStringExtra("fileName");
            fileUrl = getIntent().getStringExtra("fileUrl");
            cVenueId = getIntent().getIntExtra("venueId",0);
            cPemateriId = getIntent().getIntExtra("pemateriId",0);

            initView();
            Picasso.get()
                    .load(Config.IMAGE_URL+fileName)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .fit()
                    .into(bannerEvent);
            initEventEdit();
        }else{
            initView();
        }

        iconCalender1.setOnClickListener(this);
        iconCalender2.setOnClickListener(this);
        iconCalender3.setOnClickListener(this);
        iconCalender4.setOnClickListener(this);
    }

    private void  initView(){
        iconSearch = findViewById(R.id.iconSearch);
        iconUstadz = findViewById(R.id.iconUstadz);
        iconBack = findViewById(R.id.iconBack);
        iconCalender1 = findViewById(R.id.iconStartDate);
        iconCalender2 = findViewById(R.id.iconEndDate);
        iconCalender3 = findViewById(R.id.iconStartTime);
        iconCalender4 = findViewById(R.id.iconEndTime);
        bannerEvent = findViewById(R.id.imageEvent);
        edTextNama = findViewById(R.id.edTextNama);
        edTextTopic = findViewById(R.id.edTextTopic);
        edTextStartDate = findViewById(R.id.edTextStartDate);
        edTextEndDate = findViewById(R.id.edTextEndDate);
        edTextStartTime = findViewById(R.id.edTextStartTime);
        edTextEndTime = findViewById(R.id.edTextEndTime);
        editTextPemateri  = findViewById(R.id.edTextPemateri);
        editTextVenue = findViewById(R.id.edTextVenue);
        edTextStartDate.setEnabled(false);
        edTextEndDate.setEnabled(false);
        edTextStartTime.setEnabled(false);
        editTextPemateri.setEnabled(false);
        editTextVenue.setEnabled(false);
        edTextStartDate.setEnabled(false);
        edTextEndDate.setEnabled(false);
        edTextStartDate.setTextColor(Color.BLACK);
        edTextStartTime.setTextColor(Color.BLACK);
        edTextEndTime.setTextColor(Color.BLACK);
        edTextEndDate.setTextColor(Color.BLACK);
        edTextStartDate.setTextColor(Color.BLACK);
        edTextEndDate.setTextColor(Color.BLACK);
        editTextPemateri.setTextColor(Color.BLACK);
        editTextVenue.setTextColor(Color.BLACK);

        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddEventActivity.this,ListSearchVenueActivity.class);

                intent.putExtra("nama",edTextNama.getText().toString());
                intent.putExtra("topik",edTextTopic.getText().toString());
                intent.putExtra("startDate",edTextStartDate.getText().toString());
                intent.putExtra("endDate",edTextEndDate.getText().toString());
                intent.putExtra("startTime",edTextStartTime.getText().toString());
                intent.putExtra("endTime",edTextEndTime.getText().toString());
                intent.putExtra("venue",editTextVenue.getText().toString());
                intent.putExtra("pemateri",editTextPemateri.getText().toString());
                intent.putExtra("fileName",fileName);
                intent.putExtra("fileUrl",fileUrl);
                intent.putExtra("venueId",cVenueId);
                intent.putExtra("pemateriId",cPemateriId);
                intent.putExtra("flag","FROM_EVENT");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        iconUstadz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddEventActivity.this,ListSearchPerformerActivity.class);


                intent.putExtra("nama",edTextNama.getText().toString());
                intent.putExtra("topik",edTextTopic.getText().toString());
                intent.putExtra("startDate",edTextStartDate.getText().toString());
                intent.putExtra("endDate",edTextEndDate.getText().toString());
                intent.putExtra("startTime",edTextStartTime.getText().toString());
                intent.putExtra("endTime",edTextEndTime.getText().toString());
                intent.putExtra("venue",editTextVenue.getText().toString());
                intent.putExtra("pemateri",editTextPemateri.getText().toString());
                intent.putExtra("fileName",fileName);
                intent.putExtra("fileUrl",fileUrl);
                intent.putExtra("venueId",cVenueId);
                intent.putExtra("pemateriId",cPemateriId);
                intent.putExtra("flag","FROM_EVENT");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

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
                    Toast.makeText(AddEventActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    bannerEvent.setImageBitmap(bitmap);
                    uploadBitmap(ConvertImageBase64.getResizedBitmap(bitmap,400,400));

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddEventActivity.this, "Save Image Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            bannerEvent.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            uploadBitmap(ConvertImageBase64.getResizedBitmap(thumbnail,1000,1000));
            Toast.makeText(AddEventActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
        edTextNama.setText(nama);
        edTextTopic.setText(topik);
        edTextStartDate.setText(sDate);
        edTextEndDate.setText(eDate);
        edTextStartTime.setText(sTime);
        edTextEndTime.setText(eTime);
        editTextVenue.setText(venue);
        editTextPemateri.setText(ustadz);
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
            inputJson.put("jam_mulai", edTextStartTime.getText().toString());
            inputJson.put("jam_selesai", edTextEndTime.getText().toString());
            inputJson.put("c_venue_id", cVenueId);
            inputJson.put("c_pemateri_id", cPemateriId);
            inputJson.put("banner_image", fileName);
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

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }



    public  byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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
                            edTextStartDate.setText(year+ "-" + months[monthOfYear] + "-" + dayOfMonth);
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
                            edTextEndDate.setText(year+ "-" + months[monthOfYear] + "-" + dayOfMonth );
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (v == iconCalender3) {
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


        if (v == iconCalender4) {

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
