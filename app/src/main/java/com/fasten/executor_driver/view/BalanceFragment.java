package com.fasten.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.balance.BalanceNavigate;
import com.fasten.executor_driver.presentation.balance.BalanceViewActions;
import com.fasten.executor_driver.presentation.balance.BalanceViewModel;
import java.text.DecimalFormat;
import javax.inject.Inject;

/**
 * Отображает баланс.
 */

public class BalanceFragment extends BaseFragment implements BalanceViewActions {

  private BalanceViewModel balanceViewModel;
  private TextView balanceAmount;
  private TextView bonusAmount;
  private Context context;
  private boolean pending;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Inject
  public void setBalanceViewModel(@NonNull BalanceViewModel balanceViewModel) {
    this.balanceViewModel = balanceViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_balance, container, false);
    view.findViewById(R.id.goPaymentOptions)
        .setOnClickListener(v -> navigate(BalanceNavigate.PAYMENT_OPTIONS));
    balanceAmount = view.findViewById(R.id.balanceAmount);
    bonusAmount = view.findViewById(R.id.bonusAmount);
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
  public void onDetach() {
    super.onDetach();
    context = null;
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
    if (!getResources().getBoolean(R.bool.show_cents)) {
      amount = Math.round(amount / 100f);
    }
    bonusAmount.setText(
        new DecimalFormat(getString(R.string.currency_format)).format(amount)
    );
  }

  @Override
  public void showBalancePending(boolean pending) {
    if (this.pending != pending) {
      showPending(pending);
    }
    this.pending = pending;
  }

  @Override
  public void showBalanceServerDataErrorMessage() {
    new Builder(context)
        .setTitle(R.string.error)
        .setMessage(R.string.server_data_format_error)
        .setPositiveButton(getString(android.R.string.ok), null)
        .create()
        .show();
  }
}
