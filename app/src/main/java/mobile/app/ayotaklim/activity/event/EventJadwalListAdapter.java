package mobile.app.ayotaklim.activity.event;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.event.Jadwal;

public class EventJadwalListAdapter extends RecyclerView.Adapter<EventJadwalListAdapter.EventListViewHolder> {


    private ArrayList<Jadwal> dataList;
    private OnItemClickListener listener;

    public EventJadwalListAdapter(ArrayList<Jadwal> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener=listener;
    }

    @Override
    public EventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.event_jadwal_items, parent, false);
        return new EventListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventListViewHolder holder, final int position) {
        holder.txtEventTitle.setText(dataList.get(position).getKegiatan());
        holder.txtEventPerformer.setText(dataList.get(position).getNamaUstadz());
        holder.txtTimeEvent.setText(dataList.get(position).getDari()+"-"+dataList.get(position).getSampai());
        Picasso.get()
                .load(Config.IMAGE_URL+dataList.get(position).getImagebase64())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(holder.imageUstadz);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(dataList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class EventListViewHolder extends RecyclerView.ViewHolder{
        private TextView txtEventTitle, txtEventPerformer, txtTimeEvent;
        private RelativeLayout card;
        private ImageView imageUstadz;
        public EventListViewHolder(View itemView) {
            super(itemView);
            txtEventTitle = (TextView) itemView.findViewById(R.id.namaKegiatan);
            txtEventPerformer = (TextView) itemView.findViewById(R.id.pemateri);
            txtTimeEvent = (TextView) itemView.findViewById(R.id.waktuEvent);
            imageUstadz = itemView.findViewById(R.id.imagePerformer);
            card =(RelativeLayout) itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Jadwal jadwal);
    }
}
