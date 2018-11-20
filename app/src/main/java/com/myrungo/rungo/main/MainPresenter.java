package com.myrungo.rungo.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.myrungo.rungo.base.BasePresenter;
import com.myrungo.rungo.models.Challenge;
import com.myrungo.rungo.models.DBUser;
import com.myrungo.rungo.models.Training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.myrungo.rungo.utils.CustomExceptions.DBFieldsHasDifferentSctructureException;
import static com.myrungo.rungo.utils.CustomExceptions.NullUserInfoException;
import static com.myrungo.rungo.utils.CustomExceptions.UnauthorizedUserException;
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
    public final void onViewCreate() {
        //do work in onCreate method
    }

    @NonNull
    public final CollectionReference getUsersCollection() {
        return getDB().collection(usersCollection);
    }

    @Override
    @NonNull
    public final List<Challenge> getAllChallenges() throws Exception {
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
    public final List<DBUser> getUsers() throws Exception {
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

        @NonNull final List<DBUser> user = result.toObjects(DBUser.class);

        return user;
    }

    @Override
    @NonNull
    public final Task<DBUser> asyncGetCurrentUserInfo() {
        @NonNull final Task<QuerySnapshot> getAllUsersTask = getDB().collection(usersCollection).get();

        return getAllUsersTask.continueWith(new Continuation<QuerySnapshot, DBUser>() {
            @Override
            public DBUser then(@NonNull Task<QuerySnapshot> task) throws Exception {
                @NonNull final NullUserInfoException nullUserInfoException =
                        new NullUserInfoException("DB has no info about current user");

                @Nullable final QuerySnapshot result = task.getResult();

                if (result == null) {
                    throw nullUserInfoException;
                }

                @NonNull final List<DBUser> users = getDbUsers(result);

                for (@Nullable final DBUser user : users) {
                    @Nullable final FirebaseUser currentUser = getCurrentUser();

                    if (currentUser == null) {
                        @NonNull final UnauthorizedUserException unauthorizedUserException =
                                new UnauthorizedUserException("CurrentUser == null. DBUser must sign in");

                        throw unauthorizedUserException;
                    }

                    if (user == null) {
                        continue;
                    }

                    final boolean neededUser = currentUser.getUid().equals(user.getUid());

                    if (neededUser) {
                        return user;
                    }
                }

                throw nullUserInfoException;
            }
        });
    }

    @NonNull
    @Override
    public final DBUser getCurrentUserInfo() throws Exception {
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
            throw new NullUserInfoException("DB has no info about current user");
        }

        @NonNull final List<DBUser> users = getDbUsers(result);

        for (@Nullable final DBUser user : users) {
            @Nullable final FirebaseUser currentUser = getCurrentUser();

            if (currentUser == null) {
                throw new UnauthorizedUserException("CurrentUser == null. DBUser must sign in");
            }

            if (user == null) {
                continue;
            }

            boolean neededUser = currentUser.getUid().equals(user.getUid());

            if (neededUser) {
                return user;
            }
        }

        throw new NullUserInfoException("DB has no info about current user");
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    @NonNull
    public final Task<Void> asyncUpdateUserInfo(@NonNull final DBUser newUserInfo) throws Exception {
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

        batch.set(document, newUserInfoMap);

        return batch.commit();
    }

    /**
     * In firestore fields will be named like parameter's fields
     * For example: if in DB field == reg_date, but model's field == regDate
     * it will be renamed to regDate
     */
    @Override
    public final void updateUserInfo(@NonNull final DBUser newUserInfo) throws Exception {
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

        batch.set(document, newUserInfoMap);

        @NonNull final Task<Void> task = batch.commit();

        waitForAnyResult(task);

        @Nullable final Exception exception = task.getException();

        if (exception != null) {
            throw exception;
        }
    }

    //I'm sure that user's creation must do by login screen
    @Override
    public final void createNewUser(@NonNull final DBUser newUser) throws Exception {
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
    public final List<Training> getUserTrainingsByUid(@NonNull final String uid) throws Exception {
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
    public final List<Training> getUserTrainingsByDocumentId(@NonNull final String documentId) throws Exception {
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

            @Nullable final DBUser user;
            try {
                user = document.toObject(DBUser.class);

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

    @NonNull
    private List<DBUser> getDbUsers(@NonNull final QuerySnapshot result) throws DBFieldsHasDifferentSctructureException {
        @NonNull final List<DBUser> users = new ArrayList<>();
        try {
            @NonNull final List<DBUser> dbUsers = result.toObjects(DBUser.class);
            users.addAll(dbUsers);
        } catch (@Nullable final RuntimeException e) {
            @NonNull final List<DocumentSnapshot> documents = result.getDocuments();

            for (@Nullable final DocumentSnapshot document : documents) {
                if (document != null) {
                    try {
                        @Nullable final DBUser dbUser = document.toObject(DBUser.class);

                        if (dbUser != null) {
                            users.add(dbUser);
                        }
                    } catch (@Nullable final Exception e1) {
                        @Nullable final Map<String, Object> data = document.getData();

                        if (data == null) {
                            throw new DBFieldsHasDifferentSctructureException("Some DB field has different sctructure unlike DBUser model");
                        }

                        @Nullable final Object uidObject = data.get("uid");

                        if (uidObject == null) {
                            throw new DBFieldsHasDifferentSctructureException("Some DB field has different sctructure unlike DBUser model");
                        }

                        @NonNull final String uid = (String) uidObject;

                        throw new DBFieldsHasDifferentSctructureException(uid + " has different sctructure unlike DBUser model");
                    }
                }
            }

            throw new DBFieldsHasDifferentSctructureException("Some DB field has different sctructure unlike DBUser model");
        }

        return users;
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
