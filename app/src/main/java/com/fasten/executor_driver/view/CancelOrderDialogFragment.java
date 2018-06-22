package com.fasten.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.entity.CancelOrderReason;
import com.fasten.executor_driver.presentation.cancelorder.CancelOrderViewActions;
import com.fasten.executor_driver.presentation.cancelorder.CancelOrderViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список причин для отказа от заказа.
 */

public class CancelOrderDialogFragment extends BaseDialogFragment implements
    CancelOrderViewActions {

  private CancelOrderViewModel cancelOrderViewModel;
  private RecyclerView recyclerView;
  private Context context;
  private boolean pending;

  @Inject
  public void setCancelOrderViewModel(@NonNull CancelOrderViewModel cancelOrderViewModel) {
    this.cancelOrderViewModel = cancelOrderViewModel;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
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
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public void showCancelOrderPending(boolean pending) {
    if (this.pending != pending) {
      showPending(pending);
    }
    this.pending = pending;
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

  @Override
  public void showCancelOrderServerDataError() {
    new Builder(context)
        .setTitle(R.string.error)
        .setMessage(R.string.server_data_format_error)
        .setPositiveButton(getString(android.R.string.ok), null)
        .create()
        .show();
  }
}
