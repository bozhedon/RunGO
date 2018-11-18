package com.myrungo.rungo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class ChallengeFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        ImageView imageView = view.findViewById(R.id.imageView16);
        ImageView imageView1 = view.findViewById(R.id.imageView15);
        ImageView imageView2 = view.findViewById(R.id.imageView17);
        ImageView imageView3 = view.findViewById(R.id.imageView18);
        imageView.setOnClickListener(this);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        return view;
    }

    public void onChallengeClick(View v) {
        AlertDialog.Builder ad;
        String title = "Выборать вызов?";
        String message = "Выполни условния вызова";
        String button1String = "Вернуться";
        String button2String = "Начать";

        ad = new AlertDialog.Builder(v.getContext());
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // с
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.setCancelable(true);
        ad.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView16:
                onChallengeClick(view);
                break;
            case R.id.imageView15:
                onChallengeClick(view);
                break;
            case R.id.imageView17:
                onChallengeClick(view);
                break;
            case R.id.imageView18:
                onChallengeClick(view);
                break;
        }
    }
}
