package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.balance.BalanceViewActions;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModel;
import com.cargopull.executor_driver.presentation.menu.MenuNavigate;
import java.text.DecimalFormat;
import javax.inject.Inject;

/**
 * Отображает баланс.
 */

public class BalanceSummaryFragment extends BaseFragment implements BalanceViewActions {

  private BalanceViewModel balanceViewModel;
  private TextView balanceAmount;

  @Inject
  public void setBalanceViewModel(@NonNull BalanceViewModel balanceViewModel) {
    this.balanceViewModel = balanceViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_balance_summary, container, false);
    balanceAmount = view.findViewById(R.id.balanceAmount);
    balanceAmount.setOnClickListener(v -> navigate(MenuNavigate.BALANCE));
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
    showPending(pending, toString());
  }
}
