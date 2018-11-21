package com.myrungo.rungo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.myrungo.rungo.base.BaseFragment;
import com.myrungo.rungo.custom.CustomContract;
import com.myrungo.rungo.custom.CustomPresenter;
import com.myrungo.rungo.main.MainContract;
import com.yandex.metrica.YandexMetrica;

import java.util.List;
import java.util.Objects;

import static com.myrungo.rungo.utils.DBConstants.badboyChallengeReward;
import static com.myrungo.rungo.utils.DBConstants.karateChallengeReward;
import static com.myrungo.rungo.utils.DBConstants.officeChallengeReward;
import static com.myrungo.rungo.utils.DBConstants.ordinaryChallengeReward;


@SuppressWarnings("unused")
public final class CustomFragment
        extends BaseFragment<CustomContract.View, CustomContract.Presenter<CustomContract.View>>
        implements CustomContract.View {

    private final String TAG = this.getClass().getName();

    @Nullable
    private CustomContract.Presenter<CustomContract.View> presenter;

    @Override
    protected final void setupPresenter() {
        presenter = new CustomPresenter();
    }

    @NonNull
    @Override
    protected final CustomContract.Presenter<CustomContract.View> getPresenter() {
        if (presenter == null) {
            @NonNull final RuntimeException exception = new RuntimeException("presenter == null");
            reportException(exception);

            throw exception;
        }

        return presenter;
    }

    @Nullable
    private CatView catview;

    @NonNull
    private CatView getCatview() {
        if (catview == null) {
            @NonNull final NullPointerException exception = new NullPointerException("catview == null");
            reportException(exception);

            throw exception;
        }

        return catview;
    }

    private void setCatview(@Nullable final View catview) {
        if (catview == null) {
            @NonNull final NullPointerException exception = new NullPointerException("catview == null");
            reportException(exception);

            throw exception;
        }

        this.catview = (CatView) catview;
    }

    @Nullable
    private ImageView badcat;

    @NonNull
    private ImageView getBadcat() {
        if (badcat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("badcat == null");
            reportException(exception);

            throw exception;
        }

        return badcat;
    }

    private void setBadcat(@Nullable final View badcat) {
        if (badcat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("badcat == null");
            reportException(exception);

            throw exception;
        }

        this.badcat = (ImageView) badcat;
    }

    @Nullable
    private ImageView karatecat;

    @NonNull
    private ImageView getKaratecat() {
        if (karatecat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("karatecat == null");
            reportException(exception);

            throw exception;
        }

        return karatecat;
    }

    private void setKaratecat(@Nullable final View karatecat) {
        if (karatecat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("karatecat == null");
            reportException(exception);

            throw exception;
        }

        this.karatecat = (ImageView) karatecat;
    }

    @Nullable
    private ImageView officecat;

    @NonNull
    private ImageView getOfficecat() {
        if (officecat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("officecat == null");
            reportException(exception);

            throw exception;
        }

        return officecat;
    }

    private void setOfficecat(@Nullable final View officecat) {
        if (officecat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("officecat == null");
            reportException(exception);

            throw exception;
        }

        this.officecat = (ImageView) officecat;
    }

    @Nullable
    private ImageView sportcat;

    @NonNull
    private ImageView getSportcat() {
        if (sportcat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("sportcat == null");
            reportException(exception);

            throw exception;
        }

        return sportcat;
    }

    private void setSportcat(@Nullable final View sportcat) {
        if (sportcat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("officecat == null");
            reportException(exception);

            throw exception;
        }

        this.sportcat = (ImageView) sportcat;
    }

    private static final String USER_TAG = "USER_TAG";

    @NonNull
    @Override
    public final View onCreateView(
            @NonNull final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {

        //Bundle user_bundle = getArguments();
        //User user = (User) user_bundle.getSerializable(USER_TAG);

        @NonNull final View view = inflater.inflate(R.layout.fragment_customization, container, false);

        initViews(view);

        setOnClickListeners();

        //getCatview().setSkin(user.getSkin());
        //getCatview().setHead(user.getHead());

        getPresenter().onViewCreate();

        return view;
    }

    private void initViews(@NonNull final View view) {
        setCatview(view.findViewById(R.id.cat));

        setBadcat(view.findViewById(R.id.bad_cat_cloth));
        setKaratecat(view.findViewById(R.id.karate_cloth));
        setOfficecat(view.findViewById(R.id.office_cloth));
        setSportcat(view.findViewById(R.id.casual_sport_cloth));

        getBadcat().setClickable(true);
        getKaratecat().setClickable(true);
        getOfficecat().setClickable(true);
        getSportcat().setClickable(true);
    }

    @Override
    public final void showAvailableCostumes(@NonNull final Task<List<String>> getUserChallengesTask) {
        @Nullable final MainContract.View activity = (MainContract.View) getActivity();

        if (activity == null) {
            @NonNull final NullPointerException exception = new NullPointerException("activity == null");
            reportException(exception);

            return;
        }

        activity.showProgressIndicator();

        getUserChallengesTask
                .addOnCompleteListener(new OnCompleteListener<List<String>>() {
                    @Override
                    public void onComplete(@NonNull final Task<List<String>> task) {
                        if (task.isSuccessful()) {
                            @Nullable final List<String> userRewards = task.getResult();

                            if (userRewards == null || userRewards.isEmpty()) {
                                activity.hideProgressIndicator();
                                return;
                            }

                            for (@Nullable final String userReward : userRewards) {
                                if (userReward == null) {
                                    continue;
                                }

                                if (Objects.equals(userReward, ordinaryChallengeReward)) {
                                    Objects.requireNonNull(getActivity())
                                            .findViewById(R.id.casual_sport_cloth)
                                            .setVisibility(View.VISIBLE);
                                } else if (Objects.equals(userReward, officeChallengeReward)) {
                                    Objects.requireNonNull(getActivity())
                                            .findViewById(R.id.office_cloth)
                                            .setVisibility(View.VISIBLE);
                                } else if (Objects.equals(userReward, karateChallengeReward)) {
                                    Objects.requireNonNull(getActivity())
                                            .findViewById(R.id.karate_cloth)
                                            .setVisibility(View.VISIBLE);
                                } else if (Objects.equals(userReward, badboyChallengeReward)) {
                                    Objects.requireNonNull(getActivity())
                                            .findViewById(R.id.bad_cat_cloth)
                                            .setVisibility(View.VISIBLE);
                                }
                            }

                            activity.hideProgressIndicator();
                        }
                    }
                });
    }

    @Override
    public final void dressUp() {
        @NonNull final Task<String> asyncGetPreferredSkinTask = getPresenter().asyncGetPreferredCostume();

        asyncGetPreferredSkinTask
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            @Nullable final String preferredSkin = task.getResult();

                            if (preferredSkin == null) {
                                getCatview().setSkin(CatView.Skins.COMMON);
                                return;
                            }

                            switch (preferredSkin) {
                                case "bad":
                                    getCatview().setSkin(CatView.Skins.BAD);
                                    break;

                                case "karate":
                                    getCatview().setSkin(CatView.Skins.KARATE);
                                    break;

                                case "business":
                                    getCatview().setSkin(CatView.Skins.BUSINESS);
                                    break;

                                case "normal":
                                    getCatview().setSkin(CatView.Skins.NORMAL);
                                    break;

                                default:
                                    getCatview().setSkin(CatView.Skins.COMMON);
                            }

                        }
                    }
                });
    }

    private void setOnClickListeners() {
        getBadcat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.BAD);
                @NonNull final String newCostume = CatView.Skins.BAD.toString().toLowerCase();

                @Nullable final MainContract.View mainView = ((MainContract.View) getActivity());

                if (mainView == null) {
                    @NonNull final NullPointerException exception = new NullPointerException("mainView == null");

                    reportException(exception);
                } else {
                    @NonNull final Bundle bundle = new Bundle();
                    bundle.putString("newCostume", newCostume);
                    mainView.getFirebaseAnalytics().setUserProperty("selected_costume", newCostume);
                    mainView.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }

                getPresenter().saveNewCostume(newCostume);
            }
        });

        getKaratecat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.KARATE);
                @NonNull final String newCostume = CatView.Skins.KARATE.toString().toLowerCase();

                @Nullable final MainContract.View mainView = ((MainContract.View) getActivity());

                if (mainView == null) {
                    @NonNull final NullPointerException exception = new NullPointerException("mainView == null");

                    reportException(exception);
                } else {
                    @NonNull final Bundle bundle = new Bundle();
                    bundle.putString("newCostume", newCostume);
                    mainView.getFirebaseAnalytics().setUserProperty("selected_costume", newCostume);
                    mainView.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }

                getPresenter().saveNewCostume(newCostume);
            }
        });

        getOfficecat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.BUSINESS);
                @NonNull final String newCostume = CatView.Skins.BUSINESS.toString().toLowerCase();

                @Nullable final MainContract.View mainView = ((MainContract.View) getActivity());

                if (mainView == null) {
                    @NonNull final NullPointerException exception = new NullPointerException("mainView == null");

                    reportException(exception);
                } else {
                    @NonNull final Bundle bundle = new Bundle();
                    bundle.putString("newCostume", newCostume);
                    mainView.getFirebaseAnalytics().setUserProperty("selected_costume", newCostume);
                    mainView.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }

                getPresenter().saveNewCostume(newCostume);
            }
        });

        getSportcat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.NORMAL);
                @NonNull final String newCostume = CatView.Skins.NORMAL.toString().toLowerCase();

                @Nullable final MainContract.View mainView = ((MainContract.View) getActivity());

                if (mainView == null) {
                    @NonNull final NullPointerException exception = new NullPointerException("mainView == null");

                    reportException(exception);
                } else {
                    @NonNull final Bundle bundle = new Bundle();
                    bundle.putString("newCostume", newCostume);
                    mainView.getFirebaseAnalytics().setUserProperty("selected_costume", newCostume);
                    mainView.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }

                getPresenter().saveNewCostume(newCostume);
            }
        });
    }

    @Override
    public final void onPause() {
        getCatview().pause();
        super.onPause();
    }

    @Override
    public final void onResume() {
        super.onResume();
        getCatview().resume();
    }

    private void reportException(@NonNull final Throwable throwable) {
        Crashlytics.logException(throwable);
        YandexMetrica.reportUnhandledException(throwable);
    }

}
