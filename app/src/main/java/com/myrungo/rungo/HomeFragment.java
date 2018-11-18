package com.myrungo.rungo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HomeFragment extends Fragment {
    private CatView catView;
    private static final String USER_TAG = "USER_TAG";
    private ImageView love_heart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle user_bundle = getArguments();
        User user = (User) user_bundle.getSerializable(USER_TAG);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        catView = view.findViewById(R.id.cat);
        love_heart = view.findViewById(R.id.love_heart);
        catView.setSkin(user.getSkin());
        catView.setHead(user.getHead());
        catView.setClickable(true);
        catView.setOnClickListener(new View.OnClickListener() {
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
}
