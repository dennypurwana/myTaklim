package mobile.app.ayotaklim.activity.reminder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mobile.app.ayotaklim.MainActivity;
import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.event.RegisterEventActivity;
import mobile.app.ayotaklim.activity.venue.VenueDetailActivity;
import mobile.app.ayotaklim.activity.venue.VenueListAdapter;
import mobile.app.ayotaklim.models.reminder.Reminder;
import mobile.app.ayotaklim.models.venue.Venue;

public class ReminderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderListAdapter adapter;
    private ArrayList<Reminder> reminderArrayList;
    private Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);
        addDataEvent();

        initVenue();

    }

    void initVenue(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_reminder);

        adapter = new ReminderListAdapter(reminderArrayList, new ReminderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Reminder reminder) {
                  dialogMessage();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ReminderListActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }
    void addDataEvent(){
        reminderArrayList = new ArrayList<>();
        reminderArrayList.add(new Reminder("Kajian Ustadz Adi Hidayat","Ciamis, Jalan Kawali Panjalu No 20","Selasa,21 Dec 2019","20.00-22.00 WIB"));
        reminderArrayList.add(new Reminder("Kajian Ustadz Adi Hidayat","Ciamis, Jalan Kawali Panjalu No 20","Selasa,21 Dec 2019","20.00-22.00 WIB"));

   }

    private void dialogMessage() {
        new AlertDialog.Builder(ReminderListActivity.this,R.style.CustomDialogTheme)
                .setTitle("Reminder")
                .setMessage("Kajian yang anda ikuti akan berlangsung dalam 2 hari lagi.")
                .setPositiveButton("OK", null)

                .show();

    }
}
