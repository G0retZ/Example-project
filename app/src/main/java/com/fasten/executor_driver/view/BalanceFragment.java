package com.fasten.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.balance.BalanceNavigate;

/**
 * Отображает баланс.
 */

public class BalanceFragment extends BaseFragment {

  private LinearLayout errorLayout;
  private TextView balanceAmount;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_balance, container, false);
    view.findViewById(R.id.goPaymentOptions)
        .setOnClickListener(v -> navigate(BalanceNavigate.PAYMENT_OPTIONS));
    errorLayout = view.findViewById(R.id.balanceError);
    balanceAmount = view.findViewById(R.id.balanceAmount);
    errorLayout.setVisibility(View.GONE);
    balanceAmount.setText(getString(R.string.balance_amount, 5000).replace("5", "5 "));
    return view;
  }

  public void showError() {
    errorLayout.setVisibility(View.VISIBLE);
    balanceAmount.setText(getString(R.string.balance_amount, 400));
  }
}
