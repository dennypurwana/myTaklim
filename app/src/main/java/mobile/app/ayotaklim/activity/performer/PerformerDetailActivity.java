package mobile.app.ayotaklim.activity.performer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.models.performer.Performer;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.ConvertImageBase64;

public class PerformerDetailActivity extends AppCompatActivity {
    Performer performer;
    ImageView imageUstadz;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_detail);
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

        nama.setText(performer.getNama());
        alamat.setText(performer.getAlamat());
        phone.setText(performer.getPhone());
        deskripsi.setText(performer.getDeskripsi());
        pendidikan.setText(performer.getPendidikan());
        fb.setText(performer.getFacebook());
        ig.setText(performer.getInstagram());
        youtube.setText(performer.getYoutube());
        ConvertImageBase64.setImageFromBase64(imageUstadz,performer.getImageUstadz());
    }

}