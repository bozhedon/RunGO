package com.myrungo.rungo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.animation.Animation;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private CatView cat;
    private static final String USER_TAG = "USER_TAG";
    private ImageView love_heart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle user_bundle = getArguments();
        User user = (User) user_bundle.getSerializable(USER_TAG);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        love_heart = view.findViewById(R.id.love_heart);
        cat = view.findViewById(R.id.cat);
        cat.setClickable(true);
        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (love_heart.getVisibility()==View.INVISIBLE)
                    love_heart.setVisibility(View.VISIBLE);
                else
                    love_heart.setVisibility(View.INVISIBLE);
            }
        });

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

        cat.setOnClickListener(new View.OnClickListener() {
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
