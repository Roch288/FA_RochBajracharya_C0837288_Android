package com.lambton.FA_RochBajracharya_C0837288_android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lambton.FA_RochBajracharya_C0837288_android.R;
import com.lambton.FA_RochBajracharya_C0837288_android.db.DatabaseClient;
import com.lambton.FA_RochBajracharya_C0837288_android.db.PlaceDao;
import com.lambton.FA_RochBajracharya_C0837288_android.model.Place;
import com.lambton.FA_RochBajracharya_C0837288_android.ui.EditPlaceActivity;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    List<Place> placeList;
    Context context;

    public ListAdapter(Context context, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivFav;
        ImageView ivDone;
        RelativeLayout rlMain;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_title);
            ivFav = itemView.findViewById(R.id.image_fav);
            ivDone = itemView.findViewById(R.id.image_done);
            rlMain = itemView.findViewById(R.id.rl_main);
        }
    }

    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.MyViewHolder holder, int position) {
        Place place = placeList.get(position);

        holder.tvTitle.setText(place.getName());

        if (place.getFav()) {
            holder.ivFav.setVisibility(View.VISIBLE);
        } else {
            holder.ivFav.setVisibility(View.INVISIBLE);
        }

        if (place.getVisit()) {
            holder.ivDone.setVisibility(View.VISIBLE);
        } else {
            holder.ivDone.setVisibility(View.INVISIBLE);
        }

        holder.rlMain.setOnClickListener(view -> {
            Intent i = new Intent(context, EditPlaceActivity.class);
            i.putExtra("id", place.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public void removeItem(int position) {
        DatabaseClient.getInstance(context).getAppDatabase().placeDao().delete(placeList.get(position));
        placeList.remove(position);
        notifyItemRemoved(position);
    }
}
