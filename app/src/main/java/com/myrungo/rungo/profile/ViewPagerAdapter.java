package com.myrungo.rungo.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import static com.myrungo.rungo.profile.Constants.POSITION_KEY;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    ViewPagerAdapter(@Nullable final FragmentManager fm) {
        super(fm);
    }

    @Override
    final public Fragment getItem(final int position) {
        @NonNull final Bundle bundle = new Bundle();

        bundle.putInt(POSITION_KEY, position);

        @NonNull final TimeIntervalFragment fragment = new TimeIntervalFragment();

        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    final public CharSequence getPageTitle(final int position) {
        if (position == 0) {
            return "Неделя";
        } else if (position == 1) {
            return "Месяц";
        } else if (position == 2) {
            return "Год";
        }

        return super.getPageTitle(position);
    }

    @Override
    final public int getCount() {
        return 3;
    }

}
