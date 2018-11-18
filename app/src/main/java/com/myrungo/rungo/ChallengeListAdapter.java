package com.myrungo.rungo;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.myrungo.rungo.ChallengeItem;

import java.util.List;

public class ChallengeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ChallengeItem> challengeItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public ChallengeListAdapter(Activity activity, List<ChallengeItem> challengeItems) {
        this.activity = activity;
        this.challengeItems = challengeItems;
    }

    @Override
    public int getCount() {
        return challengeItems.size();
    }

    @Override
    public ChallengeItem getItem(int location) {
        return challengeItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.challenge_item, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        TextView text_distance= convertView.findViewById(R.id.text_distance);
        TextView textTime= convertView.findViewById(R.id.textTime);
        NetworkImageView networkImageView = convertView
                .findViewById(R.id.challengeImageView);
        ChallengeItem challengeItem =challengeItems.get(position);

        if (challengeItem.getImge() != null) {
            networkImageView.setVisibility(View.VISIBLE);
            networkImageView.setImageUrl(challengeItem.getImge(), imageLoader);
        } else {
            networkImageView.setVisibility(View.GONE);
        }

        text_distance.setText(challengeItem.distance.toString());
        textTime.setText("0"+challengeItem.hour.toString()+":"+challengeItem.minutes.toString());
        return convertView;
    }
}
