package com.fasten.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.CommonNavigate;
import com.fasten.executor_driver.presentation.balance.BalanceViewActions;
import com.fasten.executor_driver.presentation.balance.BalanceViewModel;
import com.fasten.executor_driver.presentation.menu.MenuNavigate;
import com.fasten.executor_driver.presentation.onlineswitch.OnlineSwitchViewActions;
import com.fasten.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel;
import java.text.DecimalFormat;
import javax.inject.Inject;

/**
 * Отображает меню.
 */

public class MenuFragment extends BaseFragment implements BalanceViewActions,
    OnlineSwitchViewActions {

  private BalanceViewModel balanceViewModel;
  private OnlineSwitchViewModel onlineSwitchViewModel;
  private TextView balanceAmount;
  private boolean pending;
  private boolean nowOnline;

  @Inject
  public void setBalanceViewModel(@NonNull BalanceViewModel balanceViewModel) {
    this.balanceViewModel = balanceViewModel;
  }

  @Inject
  public void setOnlineSwitchViewModel(OnlineSwitchViewModel onlineSwitchViewModel) {
    this.onlineSwitchViewModel = onlineSwitchViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_menu, container, false);
    view.findViewById(R.id.profile).setOnClickListener(v -> navigate(MenuNavigate.PROFILE));
    view.findViewById(R.id.balance).setOnClickListener(v -> navigate(MenuNavigate.BALANCE));
    view.findViewById(R.id.messages).setOnClickListener(v -> navigate(MenuNavigate.MESSAGES));
    view.findViewById(R.id.history).setOnClickListener(v -> navigate(MenuNavigate.HISTORY));
    view.findViewById(R.id.operator).setOnClickListener(v -> navigate(MenuNavigate.OPERATOR));
    view.findViewById(R.id.vehicles).setOnClickListener(v -> navigate(MenuNavigate.VEHICLES));
    view.findViewById(R.id.exit).setOnClickListener(v -> {
      if (nowOnline) {
        onlineSwitchViewModel.setNewState(false);
      }
      navigate(CommonNavigate.EXIT);
    });
    balanceAmount = view.findViewById(R.id.balanceAmount);
    return view;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    balanceViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    balanceViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    onlineSwitchViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    onlineSwitchViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void showMainAccountAmount(int amount) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      amount = Math.round(amount / 100f);
    }
    balanceAmount.setText(
        new DecimalFormat(getString(R.string.currency_format)).format(amount)
    );
    if (amount < 0) {
      balanceAmount.setTextColor(getResources().getColor(R.color.colorError));
    } else {
      balanceAmount.setTextColor(getResources().getColor(android.R.color.white));
    }
  }

  @Override
  public void showBonusAccountAmount(int amount) {

  }

  @Override
  public void showBalancePending(boolean pending) {
    if (this.pending != pending) {
      showPending(pending);
    }
    this.pending = pending;
  }

  @Override
  public void checkSwitch(boolean check) {
    nowOnline = check;
  }

  @Override
  public void showSwitchPending(boolean show) {
    if (this.pending != show) {
      showPending(pending);
    }
    this.pending = show;
  }
}
