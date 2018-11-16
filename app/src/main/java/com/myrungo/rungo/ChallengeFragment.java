package com.myrungo.rungo;

        import android.content.Context;
        import android.content.DialogInterface;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v7.app.AlertDialog;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.Toast;


public class ChallengeFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        ImageView imageView = view.findViewById(R.id.imageView16);
        imageView.setOnClickListener(this);
        return view;
    }

    public void onChallengeClick(View v) {
        AlertDialog.Builder ad;
        String title = "Выборать вызов?";
        String message = "Выполни условния вызова";
        String button1String = "Вкусная пища";
        String button2String = "Здоровая пища";

        ad = new AlertDialog.Builder(v.getContext());
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // с
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(getActivity(), "Вернуться",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(getActivity(), "Начать", Toast.LENGTH_LONG)
                        .show();
            }
        });
        ad.setCancelable(true);
        ad.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView16:
                onChallengeClick(view);
                break;
        }
    }
}
