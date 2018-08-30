package com.cargopull.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.balance.BalanceViewActions;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModel;
import com.cargopull.executor_driver.presentation.menu.MenuNavigate;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewActions;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewActions;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewModel;
import java.text.DecimalFormat;
import javax.inject.Inject;

/**
 * Отображает меню.
 */

public class MenuFragment extends BaseFragment implements BalanceViewActions,
    OnlineSwitchViewActions, PreOrderViewActions {

  private BalanceViewModel balanceViewModel;
  private OnlineSwitchViewModel onlineSwitchViewModel;
  private PreOrderViewModel preOrderViewModel;
  private TextView balanceAmount;
  private TextView preOrderAction;
  private boolean nowOnline;

  @Inject
  public void setBalanceViewModel(@NonNull BalanceViewModel balanceViewModel) {
    this.balanceViewModel = balanceViewModel;
  }

  @Inject
  public void setOnlineSwitchViewModel(@NonNull OnlineSwitchViewModel onlineSwitchViewModel) {
    this.onlineSwitchViewModel = onlineSwitchViewModel;
  }

  @Inject
  public void setPreOrderViewModel(PreOrderViewModel preOrderViewModel) {
    this.preOrderViewModel = preOrderViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_menu, container, false);
    view.findViewById(R.id.balance).setOnClickListener(v -> navigate(MenuNavigate.BALANCE));
    Context context = getContext();
    view.findViewById(R.id.exit).setOnClickListener(
        v -> new Builder(context)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(getString(android.R.string.ok), ((dialog, which) -> {
              if (nowOnline) {
                onlineSwitchViewModel.setNewState(false);
              }
              showPending(true, "exit");
              navigate(CommonNavigate.EXIT);
            }))
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show()
    );
    balanceAmount = view.findViewById(R.id.balanceAmount);
    preOrderAction = view.findViewById(R.id.preOrder);
    preOrderAction.setOnClickListener(v -> preOrderViewModel.preOrderConsumed());
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
    preOrderViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    preOrderViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void showMainAccountAmount(long amount) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      amount = Math.round(amount / 100f);
    }
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    balanceAmount.setText(decimalFormat.format(amount));
    if (amount < 0) {
      balanceAmount.setTextColor(getResources().getColor(R.color.colorError));
    } else {
      balanceAmount.setTextColor(getResources().getColor(android.R.color.white));
    }
  }

  @Override
  public void showBonusAccountAmount(long amount) {

  }

  @Override
  public void showBalancePending(boolean pending) {
    showPending(pending, toString() + "0");
  }

  @Override
  public void showBreakText(boolean show) {

  }

  @Override
  public void showTakeBreakButton(boolean show) {
    nowOnline = show;
  }

  @Override
  public void showResumeWorkButton(boolean show) {

  }

  @Override
  public void showSwitchPending(boolean pending) {
    showPending(pending, toString() + "1");
  }

  @Override
  public void showPreOrderAvailable(boolean show) {
    preOrderAction.setEnabled(show);
  }
}
