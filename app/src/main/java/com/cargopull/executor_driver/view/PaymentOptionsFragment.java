package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.paymentoptions.PaymentOptionsNavigate;

/**
 * Отображает способы оплаты.
 */

public class PaymentOptionsFragment extends BaseFragment {

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_payment_options, container, false);
    view.findViewById(R.id.qiwi).setOnClickListener(v -> navigate(PaymentOptionsNavigate.QIWI));
    view.findViewById(R.id.sberbank)
        .setOnClickListener(v -> navigate(PaymentOptionsNavigate.SBERBANK_ONLINE));
    return view;
  }
}
