package com.myrungo.rungo.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.myrungo.rungo.CatView;
import com.myrungo.rungo.R;
import com.myrungo.rungo.base.BaseFragment;
import com.myrungo.rungo.main.MainContract;


public final class ProfileFragment
        extends BaseFragment<ProfileContract.View, ProfileContract.Presenter<ProfileContract.View>>
        implements ProfileContract.View {

    @Nullable
    private ViewPagerAdapter adapter;

    @NonNull
    private ViewPagerAdapter getAdapter() {
        if (adapter == null) {
            throw new NullPointerException("Adapter must be initialized before getting");
        }

        return adapter;
    }

    private void setAdapter(@Nullable final ViewPagerAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("Parameter must be non-null");
        }

        this.adapter = adapter;
    }

    @Nullable
    private ViewPager pager;

    @NonNull
    public ViewPager getPager() {
        if (pager == null) {
            throw new NullPointerException("Pager must be initialized before getting");
        }

        return pager;
    }

    public void setPager(@Nullable final View pager) {
        if (pager == null) {
            throw new NullPointerException("Parameter must be non-null");
        }

        this.pager = (ViewPager) pager;
    }

    @Nullable
    private CatView catView;

    @NonNull
    private CatView getCatView() {
        if (catView == null) {
            @NonNull final NullPointerException exception = new NullPointerException("catView == null");
//todo
            throw exception;
        }

        return catView;
    }

    private void setCatView(@Nullable final View view) {
        if (view == null) {
            @NonNull final NullPointerException exception = new NullPointerException("view == null");

            //todo
            throw exception;
        }

        this.catView = (CatView) view;
    }

    @Nullable
    private ProfileContract.Presenter<ProfileContract.View> presenter;

    @Override
    protected final void setupPresenter() {
        presenter = new ProfilePresenter();
    }

    @NonNull
    @Override
    protected final ProfileContract.Presenter<ProfileContract.View> getPresenter() {
        if (presenter == null) {
            throw new RuntimeException("presenter == null");
        }

        return presenter;
    }

    @NonNull
    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        setAdapter(new ViewPagerAdapter(getFragmentManager()));

        @NonNull final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setCatView(view.findViewById(R.id.cat));

        setPager(view.findViewById(R.id.pager));

        getPager().setAdapter(getAdapter());

        getPresenter().onViewCreate();

        return view;
    }

    @Override
    public final void dressUp() {
        @NonNull final Task<String> asyncGetPreferredSkinTask = getPresenter().asyncGetPreferredCostume();

        @Nullable final FragmentActivity activity = getActivity();

        if (activity == null) {
            return;
        }

        asyncGetPreferredSkinTask
                .addOnCompleteListener(activity, new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            @Nullable final String preferredSkin = task.getResult();

                            if (preferredSkin == null) {
                                getCatView().setSkin(CatView.Skins.COMMON);
                                return;
                            }

                            switch (preferredSkin) {
                                case "bad":
                                    getCatView().setSkin(CatView.Skins.BAD);
                                    break;

                                case "karate":
                                    getCatView().setSkin(CatView.Skins.KARATE);
                                    break;

                                case "business":
                                    getCatView().setSkin(CatView.Skins.BUSINESS);
                                    break;

                                case "normal":
                                    getCatView().setSkin(CatView.Skins.NORMAL);
                                    break;

                                default:
                                    getCatView().setSkin(CatView.Skins.COMMON);
                            }
                        }

                        hideProgressIndicator();
                    }
                });

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
