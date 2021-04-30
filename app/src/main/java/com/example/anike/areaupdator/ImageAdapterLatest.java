package com.example.anike.areaupdator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapterLatest extends RecyclerView.Adapter<ImageAdapterLatest.ImageViewHolder> {
    private Context mcontext;
    private List<Upload> mUploads;


    public ImageAdapterLatest(Context context, List<Upload> uploads){
        mcontext = context;
        mUploads = uploads;
    }

    @Override
    public ImageAdapterLatest.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.image_item_latest, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapterLatest.ImageViewHolder imageViewHolder, int i) {
        Upload uploadCurrent = mUploads.get(i);
        imageViewHolder.textViewName.setText(uploadCurrent.getTime());
        Picasso.with(mcontext).load(uploadCurrent.getLink()).into(imageViewHolder.imageView);
        imageViewHolder.textViewName2.setText(uploadCurrent.getArea());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName;
        public ImageView imageView;
        public TextView textViewName2;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
            textViewName2 = itemView.findViewById(R.id.text_view_name_area);
        }

    }

}
