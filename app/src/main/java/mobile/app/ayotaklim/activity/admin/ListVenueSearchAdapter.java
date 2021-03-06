package mobile.app.ayotaklim.activity.admin;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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
import mobile.app.ayotaklim.models.venue.Venue;

public class ListVenueSearchAdapter extends RecyclerView.Adapter<ListVenueSearchAdapter.VenueListViewHolder> {


    private ArrayList<Venue> dataList;
    private OnItemClickListener listener;
    Context mContext;

    public ListVenueSearchAdapter(Context context, ArrayList<Venue> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener=listener;
        this.mContext = context;
    }

    @Override
    public VenueListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.venue_items_search, parent, false);
        return new VenueListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VenueListViewHolder holder, final int position) {
        holder.txtVenueAddress.setText(dataList.get(position).getAlamat());
        holder.txtVenue.setText(dataList.get(position).getNama());
        Log.d("image url : ",Config.IMAGE_URL+dataList.get(position).getImageVenue() );
        Picasso.get()
                .load(Config.IMAGE_URL+dataList.get(position).getImageVenue())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(holder.imageView);
       // ConvertImageBase64.setImageFromBase64(holder.imageView,dataList.get(position).getImageVenue());
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
        private RelativeLayout card;
        private ImageView imageView , btnDelete;
        public VenueListViewHolder(View itemView) {
            super(itemView);
            txtVenue = (TextView) itemView.findViewById(R.id.venueName);
            txtVenueAddress = (TextView) itemView.findViewById(R.id.venueAddress);
            imageView =itemView.findViewById(R.id.imageVenue);
            card=(RelativeLayout) itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Venue venue);
    }
}
