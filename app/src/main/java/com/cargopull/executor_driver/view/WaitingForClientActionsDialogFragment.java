package com.cargopull.executor_driver.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.waitingforclientactions.WaitingForClientActionsNavigate;

/**
 * Отображает меню действий во время выполнения заказа.
 */

public class WaitingForClientActionsDialogFragment extends BaseDialogFragment {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_waiting_for_client_actions, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Window window = getDialog().getWindow();
    if (window != null) {
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        window.setBackgroundDrawable(
            new ColorDrawable(getResources().getColor(R.color.colorWindowBackground, null)));
      } else {
        window.setBackgroundDrawable(
            new ColorDrawable(getResources().getColor(R.color.colorWindowBackground))
        );
      }
    }
    ((Toolbar) view.findViewById(R.id.appBar)).setNavigationOnClickListener(v -> dismiss());
    view.findViewById(R.id.orderRoute).setOnClickListener(
        v -> navigate(WaitingForClientActionsNavigate.ORDER_ROUTE)
    );
    view.findViewById(R.id.reportAProblem).setOnClickListener(
        v -> navigate(WaitingForClientActionsNavigate.REPORT_A_PROBLEM)
    );
  }

  @Override
  void navigate(@NonNull String destination) {
    dismiss();
    super.navigate(destination);
  }
}