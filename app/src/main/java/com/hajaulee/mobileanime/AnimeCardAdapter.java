package com.hajaulee.mobileanime;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AnimeCardAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<AnimeCardData> mAnimeCardList;

    public AnimeCardAdapter(Context mContext, List<AnimeCardData> mAnimeCardList) {
        this.mContext = mContext;
        this.mAnimeCardList = mAnimeCardList;
    }

    @NonNull
    @Override
    public AnimeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_row, parent, false);
        return new AnimeCardViewHolder(mView);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        AnimeCardViewHolder aHolder = (AnimeCardViewHolder) holder;
        position = aHolder.getAdapterPosition();
        AnimeCardData cardData = mAnimeCardList.get(position);
        Bitmap image = cardData.getBitmapImage(mContext);
        if (image != null) {
            aHolder.mImage.setImageBitmap(image);
            Log.d("Set success", cardData.getAnimeCardLink());
        } else
            aHolder.mImage.setImageResource(cardData.getAnimeCardImage());
        aHolder.mTitle.setText(cardData.getAnimeCardName());
        final int finalPosition = position;
        aHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, DetailActivity.class);
                mIntent.putExtra("anime", mAnimeCardList.get(finalPosition));
                mContext.startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAnimeCardList.size();
    }
}

class AnimeCardViewHolder extends RecyclerView.ViewHolder {

    ImageView mImage;
    TextView mTitle;
    CardView mCardView;

    AnimeCardViewHolder(View itemView) {
        super(itemView);

        mImage = itemView.findViewById(R.id.ivImage);
        mTitle = itemView.findViewById(R.id.tvTitle);
        mCardView = itemView.findViewById(R.id.cardview);
    }
}