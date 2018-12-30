package mobile.app.ayotaklim.activity.performer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.admin.AddPerformerActivity;
import mobile.app.ayotaklim.activity.event.EventJadwalActivity;
import mobile.app.ayotaklim.activity.event.RegisterEventActivity;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.config.SessionManager;
import mobile.app.ayotaklim.models.performer.Performer;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.ConvertImageBase64;

public class PerformerDetailActivity extends AppCompatActivity {
    Performer performer;
    ImageView imageUstadz, btnEdit, iconBack;
    TextView nama,
            alamat,
            dob,
            pendidikan,
            phone,
            deskripsi,
            email,
            ig,
            fb,
            youtube;
        SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_detail);
        sessionManager = new SessionManager(PerformerDetailActivity.this);
        performer= (Performer) getIntent().getSerializableExtra("Performer");
        nama = findViewById(R.id.performerName);
        alamat = findViewById(R.id.address);
        phone= findViewById(R.id.phone1);
        deskripsi = findViewById(R.id.desc);
        pendidikan = findViewById(R.id.pendidikan);
        fb = findViewById(R.id.fb);
        ig = findViewById(R.id.ig);
        youtube = findViewById(R.id.youtube);
        imageUstadz =findViewById(R.id.imageUstadz);
        iconBack = findViewById(R.id.iconBack);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        nama.setText(performer.getNama());
        alamat.setText(performer.getAlamat());
        phone.setText(performer.getPhone());
        deskripsi.setText(performer.getDeskripsi());
        pendidikan.setText(performer.getPendidikan());
        fb.setText(performer.getFacebook());
        ig.setText(performer.getInstagram());
        youtube.setText(performer.getYoutube());
        Picasso.get()
                .load(Config.IMAGE_URL+performer.getImageUstadz())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(imageUstadz);

        if (sessionManager.isAdmin()){

            btnEdit =findViewById(R.id.iconEdit);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PerformerDetailActivity.this, AddPerformerActivity.class);
                    intent.putExtra("flag","EDIT");
                    intent.putExtra("Performer", performer);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            btnEdit.setVisibility(View.VISIBLE);
        }


        //ConvertImageBase64.setImageFromBase64(imageUstadz,performer.getImageUstadz());
    }

}