package com.myrungo.rungo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.TAG;


public class ChallengeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map data;
    private Long distance,hour,minutes;
    private ListView listView;
    private List<ChallengeItem> challengeItems;
    private ChallengeListAdapter challengeListAdapter;

    Dialog MyDialog;
    Button accept,close;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        listView = view.findViewById(R.id.list);

        challengeItems = new ArrayList<ChallengeItem>();

        challengeListAdapter = new ChallengeListAdapter(getActivity(), challengeItems);
        listView.setAdapter(challengeListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                set_dialog(position);
            }
        });

        db.collection("challenges")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,document.getId()+"="+document.getData()+"=>");
                                data = document.getData();
                                ChallengeItem item = new ChallengeItem();
                                item.setDistance((Long) data.get("distance"));
                                item.setHour((Long) data.get("hour"));
                                item.setMinutes((Long) data.get("minutes"));
                                challengeItems.add(item);
                            }
                            challengeListAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });






        return view;
    }


    private void set_dialog(int position)
    {
        ChallengeItem challengeItem;
        challengeItem = (ChallengeItem) listView.getAdapter().getItem(position);

        MyDialog = new Dialog(getActivity());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.challenge_dialog);
        NetworkImageView profilePic = MyDialog.findViewById(R.id.profilePic);

        TextView descr = MyDialog.findViewById(R.id.textDistance);
        TextView name = MyDialog.findViewById(R.id.textHour);

        if (challengeItem.distance!=null){
            descr.setText(challengeItem.distance.toString());
        }
        if (challengeItem.hour!=null){
            name.setText(challengeItem.hour.toString());
        }


        accept = MyDialog.findViewById(R.id.accept);
        close = MyDialog.findViewById(R.id.close);

        accept.setEnabled(true);
        close.setEnabled(true);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });

        MyDialog.show();
    }
    private void accept_challenge(int position){

    }

}
