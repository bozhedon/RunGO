package com.myrungo.rungo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChallengeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFirestore profilered = FirebaseFirestore.getInstance();
    private static final String TAG = ChallengeFragment.class.getSimpleName();

    @Nullable final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Map data, data2;
    private ListView listView;
    private List<ChallengeItem> challengeItems;
    private ChallengeListAdapter challengeListAdapter;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    Dialog MyDialog;
    Button accept,close;

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        listView = view.findViewById(R.id.list);
        Log.d(TAG, user.getUid().toString()+"<=>");


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
                                //Log.d(TAG,document.getId()+"="+document.getData()+"=>");
                                data = document.getData();
                                ChallengeItem item = new ChallengeItem();

                                item.setDistance((Long) data.get("distance"));
                                item.setHour((Long) data.get("hour"));
                                item.setMinutes((Long) data.get("minutes"));
                                item.setId((String) data.get("id"));
                                String imageURL = ((String) data.get("imgURL"));
                                if (imageURL==null){

                                }
                                else item.setImge(imageURL);
                                challengeItems.add(item);
                            }
                            challengeListAdapter.notifyDataSetChanged();
                        } else {
                            @Nullable final Exception exception = task.getException();

                            if (exception != null) {
                                reportException(exception);

                                Log.w(TAG, "Error getting documents.", exception);
                            }
                        }

                    }
                });






        return view;
    }

    private void set_challenge(int position)
    {
        final ChallengeItem challengeItem;
        challengeItem = (ChallengeItem) listView.getAdapter().getItem(position);
        String UID = user.getUid();
        final DocumentReference sfDocRef = profilered.collection("users").document(UID);
        profilered.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                String chal_id = challengeItem.getId();
                transaction.update(sfDocRef, "active_challenge", chal_id);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception exception) {
                        reportException(exception);

                        Log.w(TAG, "Transaction failure.", exception);
                    }
                });
    }

    private void set_dialog(final int position)
    {
        ChallengeItem challengeItem;
        challengeItem = (ChallengeItem) listView.getAdapter().getItem(position);

        MyDialog = new Dialog(getActivity());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.challenge_dialog);
        NetworkImageView imageView = MyDialog.findViewById(R.id.profilePic);

        imageView.setImageUrl(challengeItem.getImge(), imageLoader);
        TextView textDistance = MyDialog.findViewById(R.id.textDistance);
        TextView textHour = MyDialog.findViewById(R.id.textHour);

        if (challengeItem.distance!=null){
            textDistance.setText(challengeItem.distance.toString()+"км");
        }
        if (challengeItem.hour!=null){
            textHour.setText("0"+challengeItem.hour.toString()+":"+challengeItem.minutes.toString());
        }


        accept = MyDialog.findViewById(R.id.accept);
        close = MyDialog.findViewById(R.id.close);

        accept.setEnabled(true);
        close.setEnabled(true);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_challenge(position);
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

    private void reportException(@NonNull final Throwable throwable) {
        Crashlytics.logException(throwable);
        YandexMetrica.reportUnhandledException(throwable);
    }

}
