package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.presentation.cancelorder.CancelOrderViewActions;
import com.cargopull.executor_driver.presentation.cancelorder.CancelOrderViewModel;
import com.cargopull.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewActions;
import com.cargopull.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список причин для отказа от заказа.
 */

public class CancelOrderDialogFragment extends BaseDialogFragment implements
    CancelOrderViewActions, CancelOrderReasonsViewActions {

  private CancelOrderViewModel cancelOrderViewModel;
  private CancelOrderReasonsViewModel cancelOrderReasonsViewModel;
  private RecyclerView recyclerView;

  @Inject
  public void setCancelOrderViewModel(@NonNull CancelOrderViewModel cancelOrderViewModel) {
    this.cancelOrderViewModel = cancelOrderViewModel;
  }

  @Inject
  public void setCancelOrderReasonsViewModel(
      @NonNull CancelOrderReasonsViewModel cancelOrderReasonsViewModel) {
    this.cancelOrderReasonsViewModel = cancelOrderReasonsViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cancel_order, container, false);
    recyclerView = view.findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(new ChooseVehicleAdapter(new ArrayList<>()));
    view.findViewById(R.id.doNotCancel).setOnClickListener(v -> dismiss());
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
    cancelOrderViewModel.getNavigationLiveData().observe(this, destination -> {
      dismiss();
      if (destination != null) {
        navigate(destination);
      }
    });
    cancelOrderViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    cancelOrderReasonsViewModel.getNavigationLiveData().observe(this, destination -> {
      dismiss();
      if (destination != null) {
        navigate(destination);
      }
    });
    cancelOrderReasonsViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void showCancelOrderPending(boolean pending) {
    showPending(pending, getClass().getSimpleName() + hashCode() + "0");
  }

  @Override
  public void showCancelOrderReasonsPending(boolean pending) {
    showPending(pending, getClass().getSimpleName() + hashCode() + "1");
  }

  @Override
  public void showCancelOrderReasons(boolean show) {
    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setCancelOrderReasons(@NonNull List<CancelOrderReason> cancelOrderReasons) {
    CancelOrderAdapter adapter = new CancelOrderAdapter(cancelOrderReasons,
        cancelOrderViewModel::selectItem);
    recyclerView.setAdapter(adapter);
  }
}
