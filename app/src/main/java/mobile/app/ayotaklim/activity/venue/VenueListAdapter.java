package mobile.app.ayotaklim.activity.venue;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.event.EventListAdapter;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.ConvertImageBase64;

public class VenueListAdapter extends RecyclerView.Adapter<VenueListAdapter.VenueListViewHolder> {


    private ArrayList<Venue> dataList;
    private OnItemClickListener listener;

    public VenueListAdapter(ArrayList<Venue> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener=listener;
    }

    @Override
    public VenueListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.venue_items, parent, false);
        return new VenueListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VenueListViewHolder holder, final int position) {
        holder.txtVenueAddress.setText(dataList.get(position).getAlamat());
        holder.txtVenue.setText(dataList.get(position).getNama());
        ConvertImageBase64.setImageFromBase64(holder.imageView,dataList.get(position).getImageVenue());
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

    public class VenueListViewHolder extends RecyclerView.ViewHolder{
        private TextView txtVenue, txtVenueAddress, txtEventVenue, txtEventDate, txtEventTime;
        private CardView card;
        private ImageView imageView;
        public VenueListViewHolder(View itemView) {
            super(itemView);
            txtVenue = (TextView) itemView.findViewById(R.id.venueName);
            txtVenueAddress = (TextView) itemView.findViewById(R.id.venueAddress);
            imageView =itemView.findViewById(R.id.imageVenue);
            card=(CardView) itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Venue venue);
    }
}
