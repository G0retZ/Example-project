package com.fasten.executor_driver.view;

import android.content.Context;
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
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.application.BaseActivity;
import com.fasten.executor_driver.presentation.oderfulfillmentmenu.OrderFulfillmentMenuNavigate;

/**
 * Отображает меню действий во время выполнения заказа.
 */

public class OrderFulfillmentActionsDialogFragment extends DialogFragment {

  @Nullable
  private BaseActivity baseActivity;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof BaseActivity) {
      baseActivity = (BaseActivity) context;
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_order_fulfillment_actions, container, false);
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
        v -> navigate(OrderFulfillmentMenuNavigate.ORDER_ROUTE)
    );
    view.findViewById(R.id.pause).setOnClickListener(
        v -> navigate(OrderFulfillmentMenuNavigate.PAUSE)
    );
    view.findViewById(R.id.costDetailed).setOnClickListener(
        v -> navigate(OrderFulfillmentMenuNavigate.COST_DETAILED)
    );
    view.findViewById(R.id.orderDetails).setOnClickListener(
        v -> navigate(OrderFulfillmentMenuNavigate.ORDER_INFORMATION)
    );
    view.findViewById(R.id.callToClient).setOnClickListener(
        v -> navigate(OrderFulfillmentMenuNavigate.CALL_TO_CLIENT)
    );
    view.findViewById(R.id.addService).setOnClickListener(
        v -> navigate(OrderFulfillmentMenuNavigate.ADD_SERVICE)
    );
    view.findViewById(R.id.callToOperator).setOnClickListener(
        v -> navigate(OrderFulfillmentMenuNavigate.CALL_TO_OPERATOR)
    );
  }

  private void navigate(@NonNull String destination) {
    dismiss();
    if (baseActivity != null) {
      baseActivity.navigate(destination);
    }
  }
}
