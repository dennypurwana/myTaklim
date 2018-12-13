package mobile.app.ayotaklim.activity.venue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.ConvertImageBase64;

public class VenueDetailActivity extends AppCompatActivity {

    Venue venue;
    ImageView imageVenue;
    TextView venueName,
             venueAddress,
             dkmName,
             dkmPhone,
             phone,
             deskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);
        venue= (Venue) getIntent().getSerializableExtra("Venue");
        venueName = findViewById(R.id.venueName);
        venueAddress = findViewById(R.id.venueAddress);
        dkmPhone = findViewById(R.id.phoneTakmir);
        dkmName =findViewById(R.id.takmirName);
        phone= findViewById(R.id.phone);
        deskripsi = findViewById(R.id.deskripsi);
        imageVenue = findViewById(R.id.imageVenue);

        venueName.setText(venue.getNama());
        venueAddress.setText(venue.getAlamat());
        dkmName.setText(venue.getDkm());
        dkmPhone.setText(venue.getDkmPhone());
        phone.setText(venue.getNoTlp());
        deskripsi.setText(venue.getDeskripsi());
        ConvertImageBase64.setImageFromBase64(imageVenue,venue.getImageVenue());
    }

}
