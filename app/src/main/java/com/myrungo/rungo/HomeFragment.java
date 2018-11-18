package com.myrungo.rungo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {
    private CatView catView;
    private static final String USER_TAG = "USER_TAG";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle user_bundle = getArguments();
        User user = (User) user_bundle.getSerializable(USER_TAG);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        catView = view.findViewById(R.id.cat);
        catView.setSkin(user.getSkin());
        catView.setHead(user.getHead());
        return view;
    }
}
