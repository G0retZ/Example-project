package com.cargopull.executor_driver.application;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.view.OrdersHistoryHeaderFragment;
import org.joda.time.DateTime;

public class OrdersHistoryActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_orders_history);
    ViewPager viewPager = findViewById(R.id.pager);
    viewPager.setAdapter(new OrdersHistoryPagerAdapter(getSupportFragmentManager()));
    viewPager.setOffscreenPageLimit(2);
    if (savedInstanceState == null) {
      viewPager.setCurrentItem(2);
    }
  }

  private class OrdersHistoryPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] monthNames;

    OrdersHistoryPagerAdapter(FragmentManager fm) {
      super(fm);
      monthNames = getResources().getStringArray(R.array.month_names);
    }

    @Override
    public Fragment getItem(int position) {
      return OrdersHistoryHeaderFragment.create(2 - position);
    }

    @Override
    public int getCount() {
      return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
      return monthNames[DateTime.now().minusMonths(2 - position).getMonthOfYear() - 1];
    }
  }
}
