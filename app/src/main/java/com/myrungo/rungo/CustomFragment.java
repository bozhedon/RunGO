package com.myrungo.rungo;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class CustomFragment extends Fragment {
    CatView catview;
    ImageView badcat;
    ImageView karatecat;
    ImageView officecat;
    ImageView sportcat;
    private static final String USER_TAG = "USER_TAG";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle user_bundle = getArguments();
        User user = (User) user_bundle.getSerializable(USER_TAG);

        View view = inflater.inflate(R.layout.fragment_customization, container, false);

        catview = view.findViewById(R.id.cat);
        catview.setSkin(user.getSkin());
        catview.setHead(user.getHead());

        badcat = view.findViewById(R.id.bad_cat_cloth);
        karatecat = view.findViewById(R.id.karate_cloth);
        officecat = view.findViewById(R.id.office_cloth);
        sportcat = view.findViewById(R.id.casual_sport_cloth);
        badcat.setClickable(true);
        karatecat.setClickable(true);
        officecat.setClickable(true);
        sportcat.setClickable(true);
        badcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catview.setSkin(CatView.Skins.BAD);
            }
        });
        karatecat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catview.setSkin(CatView.Skins.KARATE);
            }
        });
        officecat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catview.setSkin(CatView.Skins.BUSINESS);
            }
        });
        sportcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catview.setSkin(CatView.Skins.NORMAL);
            }
        });
        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
    }
}
