package com.sharathp.blabber.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mBinding;

    public static HomeFragment createInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ViewPager viewPager = mBinding.vpHome;
        mBinding.vpHome.setAdapter(new HomePagerAdapter(getChildFragmentManager(), getActivity()));

        mBinding.tlHome.setupWithViewPager(viewPager);
    }

    public static class HomePagerAdapter extends FragmentPagerAdapter {
        private static final int POSITION_HOME = 0;
        private static final int POSITION_MENTIONS = 1;

        private final int PAGE_COUNT = 2;
        private int tabTitleIds[] = new int[]{R.string.home_tab_title_home_timeline,
                R.string.home_tab_title_mentions};
        private Context context;

        public HomePagerAdapter(final FragmentManager fm, final Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(final int position) {
            switch (position) {
                case POSITION_HOME: {
                    return HomeTimelineFragment.createInstance();
                }
                case POSITION_MENTIONS: {
                    return MentionsFragment.createInstance();
                }
            }
            throw new IllegalStateException("Unknown tab position: " + position);
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return context.getResources().getString(tabTitleIds[position]);
        }
    }
}
