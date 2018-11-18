package com.myrungo.rungo.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.myrungo.rungo.base.BasePresenter;
import com.myrungo.rungo.models.Challenge;
import com.myrungo.rungo.models.Training;
import com.myrungo.rungo.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.myrungo.rungo.utils.CustomExceptions.UnauthorizedUserExceptions;
import static com.myrungo.rungo.utils.DBConstants.challengesCollection;
import static com.myrungo.rungo.utils.DBConstants.usersCollection;

public final class MainPresenter
        extends BasePresenter<MainContract.View>
        implements MainContract.Presenter<MainContract.View> {

    private final String TAG = this.getClass().getName();

    @Nullable
    private FirebaseFirestore db;
    @Nullable
    private FirebaseAuth firebaseAuth;

    @NonNull
    private FirebaseFirestore getDB() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }

        return db;
    }

    @NonNull
    private FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.useAppLanguage();
        }

        return firebaseAuth;
    }

    @Nullable
    private FirebaseUser getCurrentUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    @Override
    final public void onViewCreate() {
        //do work in onCreate method
    }

    @Override
    final public void onViewStart() {
        //do work in onStart method
    }

    @Override
    @NonNull
    final public List<Challenge> getAllChallenges() throws Exception {
        @NonNull final Task<QuerySnapshot> task = getDB()
                .collection(challengesCollection)
                .get();

        waitForAnyResult(task);

        @Nullable final Exception exception = task.getException();

        if (exception != null) {
            throw exception;
        }

        @Nullable final QuerySnapshot result = task.getResult();

        if (result == null) {
            return Collections.emptyList();
        }

        @NonNull final List<Challenge> challenges = result.toObjects(Challenge.class);

        return challenges;
    }

    @NonNull
    @Override
    final public List<User> getUsers() throws Exception {
        @NonNull final Task<QuerySnapshot> task = getDB()
                .collection(usersCollection)
                .get();

        waitForAnyResult(task);

        @Nullable final Exception exception = task.getException();

        if (exception != null) {
            throw exception;
        }

        @Nullable final QuerySnapshot result = task.getResult();

        if (result == null) {
            return Collections.emptyList();
        }

        @NonNull final List<User> user = result.toObjects(User.class);

        return user;
    }

    @Nullable
    @Override
    final public User getCurrentUserInfo() throws Exception {
        @NonNull final Task<QuerySnapshot> task = getDB()
                .collection(usersCollection)
                .get();

        waitForAnyResult(task);

        @Nullable final Exception exception = task.getException();

        if (exception != null) {
            throw exception;
        }

        @Nullable final QuerySnapshot result = task.getResult();

        if (result == null) {
            return null;
        }

        @NonNull final List<User> users = result.toObjects(User.class);

        for (@Nullable final User user : users) {
            @Nullable final FirebaseUser currentUser = getCurrentUser();

            if (currentUser == null) {
                throw new UnauthorizedUserExceptions("CurrentUser == null. User must sign in");
            }

            if (user == null) {
                continue;
            }

            boolean neededUser = currentUser.getUid().equals(user.getUid());

            if (neededUser) {
                return user;
            }
        }

        return null;
    }

    /**
     * In firestore fields will be named like parameter's fields
     * For example: if in DB field == reg_date, but parameter's field == regDate
     * it will be renamed to regDate
     */
    @Override
    final public void updateUserInfo(@NonNull final User newUserInfo) throws Exception {
        @NonNull final ObjectMapper mapper = new ObjectMapper();

        //convert POJO to Map
        @Nullable final Map<String, Object> newUserInfoMap = mapper.convertValue(
                newUserInfo,
                new TypeReference<Map<String, Object>>() {
                });

        if (newUserInfoMap == null) {
            throw new RuntimeException("newUserInfoMap == null. Update not available");
        }

        @NonNull final WriteBatch batch = getDB().batch();

        @Nullable final Object uidObject = newUserInfoMap.get("uid");

        if (uidObject == null) {
            @NonNull final String message =
                    "newUserInfoMap does not contain uid key. Key is needed for update user info";

            throw new RuntimeException(message);
        }

        @NonNull final String uid = (String) uidObject;

        @NonNull final DocumentReference document = getDB()
                .collection(usersCollection)
                .document(uid);

        batch.update(document, newUserInfoMap);

        @NonNull final Task<Void> task = batch.commit();

        waitForAnyResult(task);

        @Nullable final Exception exception = task.getException();

        if (exception != null) {
            throw exception;
        }
    }

    //I'm sure that user's creation must do by login screen
    @Override
    final public void createNewUser(@NonNull final User newUser) throws Exception {
        if (newUser.getUid().trim().isEmpty()) {
            throw new RuntimeException("Uid must not be empty");
        }

        @NonNull final Task<Void> task = getDB()
                .collection(usersCollection)
                .document(newUser.getUid().trim())
                .set(newUser);

        waitForAnyResult(task);

        @Nullable final Exception exception = task.getException();

        if (exception != null) {
            throw exception;
        }
    }

    /**
     * @return user's trainings (can returns empty list)
     */
    @NonNull
    @Override
    final public List<Training> getUserTrainingsByUid(@NonNull final String uid) throws Exception {
        @Nullable final DocumentReference userDocument = getUserDocumentReferenceByUid(uid);

        if (userDocument == null) {
            return Collections.emptyList();
        }

        @NonNull final String userDocumentId = userDocument.getId();

        @NonNull final Task<QuerySnapshot> task = getDB()
                .collection(usersCollection)
                .document(userDocumentId)
                .collection("trainings")
                .get();

        waitForAnyResult(task);

        @Nullable final Exception exception = task.getException();

        if (exception != null) {
            throw exception;
        }

        @Nullable final QuerySnapshot result = task.getResult();

        if (result == null) {
            return Collections.emptyList();
        }

        @NonNull final List<DocumentSnapshot> documents = result.getDocuments();

        @NonNull final List<Training> userTrainings = new ArrayList<>();

        for (@Nullable final DocumentSnapshot document : documents) {
            if (document != null) {
                @Nullable final Training training = document.toObject(Training.class);

                if (training != null) {
                    userTrainings.add(training);
                }
            }
        }

        return userTrainings;
    }

    /**
     * @return user's trainings (can returns empty list)
     */
    @NonNull
    @Override
    final public List<Training> getUserTrainingsByDocumentId(@NonNull final String documentId) throws Exception {
        @NonNull final Task<QuerySnapshot> task = getDB()
                .collection(usersCollection)
                .document(documentId)
                .collection("trainings")
                .get();

        waitForAnyResult(task);

        @Nullable final Exception exception = task.getException();

        if (exception != null) {
            throw exception;
        }

        @Nullable final QuerySnapshot result = task.getResult();

        if (result == null) {
            return Collections.emptyList();
        }

        @NonNull final List<DocumentSnapshot> documents = result.getDocuments();

        @NonNull final List<Training> userTrainings = new ArrayList<>();

        for (@Nullable final DocumentSnapshot document : documents) {
            if (document != null) {
                @Nullable final Training training = document.toObject(Training.class);

                if (training != null) {
                    userTrainings.add(training);
                }
            }
        }

        return userTrainings;
    }

    @Nullable
    private DocumentReference getUserDocumentReferenceByUid(@NonNull final String uid) throws Exception {
        @NonNull final Task<QuerySnapshot> task = getDB()
                .collection(usersCollection)
                .get();

        waitForAnyResult(task);

        @Nullable final Exception exception = task.getException();

        if (exception != null) {
            throw exception;
        }

        @Nullable final QuerySnapshot result = task.getResult();

        if (result == null) {
            return null;
        }

        @NonNull final List<DocumentSnapshot> documents = result.getDocuments();

        for (@Nullable final DocumentSnapshot document : documents) {
            if (document == null) {
                continue;
            }

            @Nullable final User user;
            try {
                user = document.toObject(User.class);

                if (user == null) {
                    continue;
                }

                final boolean neededUser = user.getUid().equals(uid);

                if (neededUser) {
                    return document.getReference();
                }
            } catch (Exception e) {
                e.printStackTrace();
                @Nullable final Map<String, Object> data = document.getData();

                if (data != null) {
                    @Nullable final Object uidFromDb = data.get("uid");

                    if (uidFromDb != null) {
                        final boolean neededUser = uidFromDb.equals(uid);

                        if (neededUser) {
                            return document.getReference();
                        }
                    }
                }
            }
        }

        return null;
    }

    private void waitForAnyResult(@NonNull final Task<?> task) {
        while (true) {
            final boolean complete = task.isComplete();

            final boolean canceled = task.isCanceled();

            final boolean successful = task.isSuccessful();

            //for sync realization
            @NonNull final String msg = "task.isComplete() == " +
                    complete +
                    "; task.isCanceled() == " +
                    canceled +
                    "; task.isSuccessful() == " +
                    successful;

            Log.d(TAG, msg);

            if (successful || canceled || complete) {
                break;
            }
        }
    }

}
