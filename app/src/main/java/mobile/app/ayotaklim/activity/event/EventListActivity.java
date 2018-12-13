package mobile.app.ayotaklim.activity.event;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.models.event.Event;

public class EventListActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private ArrayList<Event> eventArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                .permitDiskWrites()
                .build());
        StrictMode.setThreadPolicy(old);
        addDataEvent();
        initEvent();

    }


    void initEvent(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_event);

        adapter = new EventListAdapter(eventArrayList, new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Intent intent=new Intent(EventListActivity.this,EventDetailActivity.class);
                intent.putExtra("Event", event);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventListActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }
    void addDataEvent(){
        eventArrayList = new ArrayList<>();
        eventArrayList.add(new Event("Bila Anda Dijauhi Manusia", "Masjid Al-Hidayah", "Alamat: no, Jl. Tomang Tinggi No.3B, RT.13/RW.6, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440", "topic 1", "addars", "dasdsa", "20-11-2019", "20.00", "22.00"));
        eventArrayList.add(new Event("Ghibah yang Dibolehkan", "Masjid Al-Mumin", "Alamat: no, Jl. Tomang Tinggi No.3B, RT.13/RW.6, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440", "topic 1", "addars", "dasdsa", "20-11-2019", "20.00", "22.00"));
        eventArrayList.add(new Event("#2019 Pemilihan Presiden: Dalil Bolehnya Mencoblos dalam Pemilu – DR Firanda Andirja MA", "Masjid Al-Mughni", "Alamat: no, Jl. Tomang Tinggi No.3B, RT.13/RW.6, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440", "topic 1", "addars", "dasdsa", "20-11-2019", "20.00", "22.00"));
        eventArrayList.add(new Event("Maha Benar Netizen dengan Segala Komentarnya”, Ucapan Kufur?", "Jakarta Convention Center", "Alamat: no, Jl. Tomang Tinggi No.3B, RT.13/RW.6, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440", "topic 1", "addars", "dasdsa", "20-11-2019", "20.00", "22.00"));
    }


}