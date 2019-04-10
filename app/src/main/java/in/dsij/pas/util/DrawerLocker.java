package in.dsij.pas.util;

import android.support.v4.view.ViewPager;

public interface DrawerLocker {
    void setDrawerEnabled(boolean enabled);

    void showActionBar(boolean shown);

    void showUpButton(boolean shown);

    void setViewPagerWithTabBar(ViewPager viewPager);

    void setActionBarTitle(String title);
}