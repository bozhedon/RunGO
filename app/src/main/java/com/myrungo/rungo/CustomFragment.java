package com.myrungo.rungo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.myrungo.rungo.base.BaseFragment;
import com.myrungo.rungo.custom.CustomContract;
import com.myrungo.rungo.custom.CustomPresenter;
import com.myrungo.rungo.main.MainContract;


@SuppressWarnings("unused")
public final class CustomFragment
        extends BaseFragment<CustomContract.View, CustomContract.Presenter<CustomContract.View>>
        implements CustomContract.View {

    private final String TAG = this.getClass().getName();

    @Nullable
    private CustomContract.Presenter<CustomContract.View> presenter;

    @Override
    final protected void setupPresenter() {
        presenter = new CustomPresenter();
    }

    @NonNull
    @Override
    final protected CustomContract.Presenter<CustomContract.View> getPresenter() {
        if (presenter == null) {
            throw new RuntimeException("presenter == null");
        }

        return presenter;
    }

    @Nullable
    private CatView catview;

    @NonNull
    private CatView getCatview() {
        if (catview == null) {
            throw new NullPointerException("catview == null");
        }

        return catview;
    }

    private void setCatview(@Nullable final View catview) {
        if (catview == null) {
            throw new NullPointerException("catview == null");
        }

        this.catview = (CatView) catview;
    }

    @Nullable
    private ImageView badcat;

    @NonNull
    private ImageView getBadcat() {
        if (badcat == null) {
            throw new NullPointerException("badcat == null");
        }

        return badcat;
    }

    private void setBadcat(@Nullable final View badcat) {
        if (badcat == null) {
            throw new NullPointerException("badcat == null");
        }

        this.badcat = (ImageView) badcat;
    }

    @Nullable
    private ImageView karatecat;

    @NonNull
    private ImageView getKaratecat() {
        if (karatecat == null) {
            throw new NullPointerException("karatecat == null");
        }

        return karatecat;
    }

    private void setKaratecat(@Nullable final View karatecat) {
        if (karatecat == null) {
            throw new NullPointerException("karatecat == null");
        }

        this.karatecat = (ImageView) karatecat;
    }

    @Nullable
    private ImageView officecat;

    @NonNull
    private ImageView getOfficecat() {
        if (officecat == null) {
            throw new NullPointerException("officecat == null");
        }

        return officecat;
    }

    private void setOfficecat(@Nullable final View officecat) {
        if (officecat == null) {
            throw new NullPointerException("officecat == null");
        }

        this.officecat = (ImageView) officecat;
    }

    @Nullable
    private ImageView sportcat;

    @NonNull
    private ImageView getSportcat() {
        if (sportcat == null) {
            throw new NullPointerException("sportcat == null");
        }

        return sportcat;
    }

    private void setSportcat(@Nullable final View sportcat) {
        if (sportcat == null) {
            throw new NullPointerException("officecat == null");
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
    public void dressUp() {
        @Nullable final MainContract.View activity = (MainContract.View) getActivity();

        if (activity == null) {
            return;
        }

        @NonNull final Task<String> asyncGetPreferredSkinTask = getPresenter().asyncGetPreferredSkin();

        activity.showProgressIndicator();

        asyncGetPreferredSkinTask
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(@Nullable final String preferredSkin) {
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
                })
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        activity.hideProgressIndicator();
                    }
                });
    }

    private void setOnClickListeners() {
        getBadcat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.BAD);
                @NonNull final String newCostume = CatView.Skins.BAD.toString().toLowerCase();

                getPresenter().saveNewCostume(newCostume);
            }
        });

        getKaratecat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.KARATE);
                @NonNull final String newCostume = CatView.Skins.KARATE.toString().toLowerCase();

                getPresenter().saveNewCostume(newCostume);
            }
        });

        getOfficecat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.BUSINESS);
                @NonNull final String newCostume = CatView.Skins.BUSINESS.toString().toLowerCase();

                getPresenter().saveNewCostume(newCostume);
            }
        });

        getSportcat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCatview().setSkin(CatView.Skins.NORMAL);
                @NonNull final String newCostume = CatView.Skins.NORMAL.toString().toLowerCase();

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

}
