package mobile.app.ayotaklim.activity.event;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import mobile.app.ayotaklim.utils.ConvertImageBase64;
import mobile.app.ayotaklim.utils.FormatTanggalIDN;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventListViewHolder> {


    private ArrayList<Event> dataList;
    private OnItemClickListener listener;
    FormatTanggalIDN formatTanggalIDN;

    public EventListAdapter(ArrayList<Event> dataList,OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener=listener;
    }

    @Override
    public EventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.event_items, parent, false);
        return new EventListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventListViewHolder holder, final int position) {
        formatTanggalIDN=new FormatTanggalIDN();
        holder.txtEventTitle.setText(dataList.get(position).getNamaEvent());
        holder.txtEventDate.setText(formatTanggalIDN.formatDate(dataList.get(position).getTglMulai())+" s.d "+formatTanggalIDN.formatDate(dataList.get(position).getTglBerakhir()));
        holder.txtEventVenue.setText(dataList.get(position).getNamaVenue());
        holder.txtEventAddress.setText(dataList.get(position).getAlamatVenue());
        Picasso.get()
                .load(Config.IMAGE_URL+dataList.get(position).getBannerImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(holder.bannerImage);;
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
        private TextView txtEventTitle, txtEventAddress, txtEventVenue, txtEventDate, txtEventTime;
        private RelativeLayout card;
        private ImageView bannerImage;
        public EventListViewHolder(View itemView) {
            super(itemView);
            txtEventTitle = (TextView) itemView.findViewById(R.id.titleEvent);
            txtEventDate = (TextView) itemView.findViewById(R.id.eventDate);
            txtEventVenue = (TextView) itemView.findViewById(R.id.venueEvent);
            txtEventAddress = (TextView) itemView.findViewById(R.id.venueAddress);
            bannerImage = (ImageView) itemView.findViewById(R.id.bannerImage);
            card =(RelativeLayout) itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }
}
