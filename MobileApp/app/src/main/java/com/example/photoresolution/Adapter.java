package com.example.photoresolution;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoresolution.model.ImageDatabase;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {

    Context context;
    ArrayList<ImageDatabase> images;

    public Adapter(Context c, ArrayList<ImageDatabase> imgs) {
        context = c;
        images = imgs;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        Log.d("imageId", images.get(position).getImageId());

        final String imageId = images.get(position).getImageId();

        holder.id.setText("Id: " + images.get(position).getImageId());
        holder.name.setText("Name: " + images.get(position).getName());
        holder.timestamp.setText("Timestamp: " + images.get(position).getTimestamp());
        holder.resolution.setText("Resolution: " + images.get(position).getResolution());

        //Picasso.get().load(images.get(position).getImg()).into(holder.image);
        Glide.with(context)
                .load(Uri.parse(images.get(position).getImg()))
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("imageId", imageId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView id, name, timestamp, resolution;
        ImageView image;

        public ItemClickListener itemClickListener;

        public viewHolder(View itemView) {
            super(itemView);

            id = (TextView) itemView.findViewById(R.id.text_id);
            name = (TextView) itemView.findViewById(R.id.text_name);
            timestamp = (TextView) itemView.findViewById(R.id.text_timestamp);
            resolution = (TextView) itemView.findViewById(R.id.text_resolution);

            image = (ImageView) itemView.findViewById(R.id.reconstruct_image);
        }

        public void setItemClicked(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
