package com.example.club_tip;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.club_tip.Fragment.DiaryFragment;
import com.example.club_tip.Fragment.FeedFragment;
import com.example.club_tip.Fragment.TipFragment;


public class ViewpagerAdapter extends FragmentStateAdapter {


    public ViewpagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FeedFragment();
            case 1:
                return new DiaryFragment();
            case 2:
                return new TipFragment();
            default:
                return new FeedFragment();
        }
    }


    @Override
    public int getItemCount() {
        return 3;
    }
}
