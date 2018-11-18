package com.myrungo.rungo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private CatView cat;
    private View catView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        cat = view.findViewById(R.id.cat);
        catView = view.findViewById(R.id.cat_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            cat.greet();
        }
        final CatView.Heads currentHead = cat.getCurrentHead();

        final SharedPreferences prefs = Objects.requireNonNull(getContext())
                .getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);

        String preferedSkin = prefs.getString("SKIN", CatView.Skins.COMMON.toString().toLowerCase());

        switch (preferedSkin) {
            case "bad":
                cat.setSkin(CatView.Skins.BAD);
                break;

            case "karate":
                cat.setSkin(CatView.Skins.KARATE);
                break;

            case "business":
                cat.setSkin(CatView.Skins.BUSINESS);
                break;

            case "normal":
                cat.setSkin(CatView.Skins.NORMAL);
                break;

            default:
                cat.setSkin(CatView.Skins.COMMON);
        }

        cat.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cat.setHead(CatView.Heads.SHOCK);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cat.setHead(currentHead);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        catView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cat.slap();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cat.resume();
    }

    @Override
    public void onPause() {
        cat.pause();
        super.onPause();
    }
}
