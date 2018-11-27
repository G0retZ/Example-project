package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.ordershistoryheader.OrdersHistoryHeaderViewActions;
import com.cargopull.executor_driver.presentation.ordershistoryheader.OrdersHistoryHeaderViewModel;
import javax.inject.Inject;

/**
 * Отображает заголовок истории заказов.
 */

public class OrdersHistoryHeaderFragment extends BaseFragment implements
    OrdersHistoryHeaderViewActions {

  private OrdersHistoryHeaderViewModel ordersHistoryHeaderViewModel;
  private int offset;

  public static OrdersHistoryHeaderFragment create(int offset) {
    OrdersHistoryHeaderFragment fragment = new OrdersHistoryHeaderFragment();
    Bundle args = new Bundle();
    args.putInt("offset", offset);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    offset = getArguments() == null ? 0 : getArguments().getInt("offset");
  }

  @Inject
  public void setOrdersHistoryHeaderViewModel(
      @NonNull OrdersHistoryHeaderViewModel ordersHistoryHeaderViewModel) {
    this.ordersHistoryHeaderViewModel = ordersHistoryHeaderViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_orders_history_header, container, false);
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this, offset);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setClickAction(R.id.retryButton, ordersHistoryHeaderViewModel::retry);
    ordersHistoryHeaderViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public boolean isShowCents() {
    return getResources().getBoolean(R.bool.show_cents);
  }

  @Override
  @NonNull
  public String getCurrencyFormat() {
    return getString(R.string.currency_format);
  }
}
