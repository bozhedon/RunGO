package com.myrungo.rungo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.myrungo.rungo.ChallengeImageView;
import com.myrungo.rungo.ChallengeItem;

import java.util.List;

public class ChallengeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ChallengeItem> challengeItems;

    public ChallengeListAdapter(Activity activity, List<ChallengeItem> feedItems) {
        this.activity = activity;
        this.challengeItems = feedItems;
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



        ChallengeItem item = challengeItems.get(position);

        return convertView;
    }
}
