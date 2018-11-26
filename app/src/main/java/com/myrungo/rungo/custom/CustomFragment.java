package com.myrungo.rungo.custom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.myrungo.rungo.CatView;
import com.myrungo.rungo.R;
import com.myrungo.rungo.base.BaseFragment;
import com.myrungo.rungo.main.MainContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.myrungo.rungo.utils.DBConstants.badboyChallengeReward;
import static com.myrungo.rungo.utils.DBConstants.businessChallengeReward;
import static com.myrungo.rungo.utils.DBConstants.karateChallengeReward;
import static com.myrungo.rungo.utils.DBConstants.ordinaryChallengeReward;


public final class CustomFragment
        extends BaseFragment<CustomContract.View, CustomContract.Presenter<CustomContract.View>>
        implements CustomContract.View {

    private static final String USER_TAG = "USER_TAG";

    @Nullable
    private CustomContract.Presenter<CustomContract.View> presenter;

    @Nullable
    private CatView catview;

    @Nullable
    private ImageView badcat;

    @Nullable
    private ImageView karatecat;

    @Nullable
    private ImageView officecat;

    @Nullable
    private ImageView sportcat;

    @Override
    protected final void setupPresenter() {
        presenter = new CustomPresenter();
    }

    @NonNull
    @Override
    protected final CustomContract.Presenter<CustomContract.View> getPresenter() {
        if (presenter == null) {
            @NonNull final RuntimeException exception = new RuntimeException("presenter == null");
            reportError(exception);

            throw exception;
        }

        return presenter;
    }

    @NonNull
    private CatView getCatview() {
        if (catview == null) {
            @NonNull final NullPointerException exception = new NullPointerException("catview == null");
            reportError(exception);

            throw exception;
        }

        return catview;
    }

    private void setCatview(@Nullable final View catview) {
        if (catview == null) {
            @NonNull final NullPointerException exception = new NullPointerException("catview == null");
            reportError(exception);

            throw exception;
        }

        this.catview = (CatView) catview;
    }

    @NonNull
    private ImageView getBadcat() {
        if (badcat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("badcat == null");
            reportError(exception);

            throw exception;
        }

        return badcat;
    }

    private void setBadcat(@Nullable final View badcat) {
        if (badcat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("badcat == null");
            reportError(exception);

            throw exception;
        }

        this.badcat = (ImageView) badcat;
    }

    @NonNull
    private ImageView getKaratecat() {
        if (karatecat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("karatecat == null");
            reportError(exception);

            throw exception;
        }

        return karatecat;
    }

    private void setKaratecat(@Nullable final View karatecat) {
        if (karatecat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("karatecat == null");
            reportError(exception);

            throw exception;
        }

        this.karatecat = (ImageView) karatecat;
    }

    @NonNull
    private ImageView getOfficecat() {
        if (officecat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("officecat == null");
            reportError(exception);

            throw exception;
        }

        return officecat;
    }

    private void setOfficecat(@Nullable final View officecat) {
        if (officecat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("officecat == null");
            reportError(exception);

            throw exception;
        }

        this.officecat = (ImageView) officecat;
    }

    @NonNull
    private ImageView getSportcat() {
        if (sportcat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("sportcat == null");
            reportError(exception);

            throw exception;
        }

        return sportcat;
    }

    private void setSportcat(@Nullable final View sportcat) {
        if (sportcat == null) {
            @NonNull final NullPointerException exception = new NullPointerException("officecat == null");
            reportError(exception);

            throw exception;
        }

        this.sportcat = (ImageView) sportcat;
    }

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
    public final void showAvailableCostumes(@NonNull final Task<List<String>> getUserRewardsTask) {
        @Nullable final FragmentActivity activity = getActivity();

        if (activity == null) {
            @NonNull final NullPointerException exception = new NullPointerException("activity == null");
            reportError(exception);

            return;
        }

        getUserRewardsTask
                .addOnCompleteListener(activity, new OnCompleteListener<List<String>>() {
                    @Override
                    public void onComplete(@NonNull final Task<List<String>> task) {
                        if (task.isSuccessful()) {
                            @Nullable final List<String> result = task.getResult();

                            if (result == null) {
                                return;
                            }

                            @NonNull final List<String> userRewards = new ArrayList<>(result);

                            @NonNull final TextView errorTextView =
                                    activity.findViewById(R.id.errorTextView);

                            @NonNull final ConstraintLayout mainLayout =
                                    activity.findViewById(R.id.mainLayout);

                            if (isUserRewardsEmpty(userRewards, errorTextView, mainLayout)) {
                                return;
                            }

                            showAvailableCostumes(userRewards, activity);

                            errorTextView.setVisibility(View.GONE);
                            mainLayout.setVisibility(View.VISIBLE);
                            dressUp();
                        }
                    }
                });
    }

    private void showAvailableCostumes(
            @NonNull final List<String> userRewards,
            @NonNull final FragmentActivity activity) {
        for (@Nullable final String userReward : userRewards) {
            if (userReward == null) {
                continue;
            }

            if (Objects.equals(userReward, ordinaryChallengeReward)) {

                activity
                        .findViewById(R.id.casual_sport_cloth)
                        .setVisibility(View.VISIBLE);

            } else if (Objects.equals(userReward, businessChallengeReward)) {

                activity
                        .findViewById(R.id.office_cloth)
                        .setVisibility(View.VISIBLE);

            } else if (Objects.equals(userReward, karateChallengeReward)) {

                activity
                        .findViewById(R.id.karate_cloth)
                        .setVisibility(View.VISIBLE);

            } else if (Objects.equals(userReward, badboyChallengeReward)) {

                activity
                        .findViewById(R.id.bad_cat_cloth)
                        .setVisibility(View.VISIBLE);

            }
        }
    }

    private boolean isUserRewardsEmpty(
            @NonNull final List<String> userRewards,
            @NonNull final TextView errorTextView,
            @NonNull final ConstraintLayout mainLayout) {
        @NonNull final String preferredCostume = getPresenter().getPrefferedCostume();

        if (userRewards.isEmpty()) {
            if (preferredCostume.equals(CatView.Skins.COMMON.toString().toLowerCase())) {
                errorTextView.setText(getString(R.string.you_do_not_have_any_costume));
                errorTextView.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);

                hideProgressIndicator();
                return true;
            } else {
                userRewards.add(preferredCostume);

                getPresenter().asyncSaveNewCostume(preferredCostume);
                getPresenter().asyncUpdateUserRewards(preferredCostume);
            }
        }

        return false;
    }

    private void dressUp() {
        @Nullable final FragmentActivity activity = getActivity();

        if (activity == null) {
            return;
        }

        @NonNull final Task<String> asyncGetPreferredSkinTask = getPresenter().asyncGetPreferredCostume();

        asyncGetPreferredSkinTask
                .addOnCompleteListener(activity, new OnCompleteListener<String>() {
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

                        hideProgressIndicator();
                    }
                });
    }

    private void setOnClickListeners() {
        getBadcat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.BAD);
                @NonNull final String newCostume = CatView.Skins.BAD.toString().toLowerCase();

                getPresenter().asyncSaveNewCostume(newCostume);
            }
        });

        getKaratecat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.KARATE);
                @NonNull final String newCostume = CatView.Skins.KARATE.toString().toLowerCase();

                getPresenter().asyncSaveNewCostume(newCostume);
            }
        });

        getOfficecat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.BUSINESS);
                @NonNull final String newCostume = CatView.Skins.BUSINESS.toString().toLowerCase();

                getPresenter().asyncSaveNewCostume(newCostume);
            }
        });

        getSportcat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.NORMAL);
                @NonNull final String newCostume = CatView.Skins.NORMAL.toString().toLowerCase();

                getPresenter().asyncSaveNewCostume(newCostume);
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

    @Override
    public final void showProgressIndicator() {
        @Nullable final MainContract.View mainView = (MainContract.View) getActivity();

        if (mainView == null) {
            return;
        }

        mainView.showProgressIndicator();
    }

    @Override
    public final void hideProgressIndicator() {
        @Nullable final MainContract.View mainView = (MainContract.View) getActivity();

        if (mainView == null) {
            return;
        }

        mainView.hideProgressIndicator();
    }

}
