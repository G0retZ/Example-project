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
import com.fasten.executor_driver.presentation.orderroute.OrderRouteViewActions;
import com.fasten.executor_driver.presentation.orderroute.OrderRouteViewModel;
import com.fasten.executor_driver.presentation.orderroute.RoutePointItem;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список ТС для выбора при выходе на линию.
 */

public class OrderRouteFragment extends BaseFragment implements OrderRouteViewActions {

  private OrderRouteViewModel orderRouteViewModel;
  private RecyclerView recyclerView;
  private Context context;
  private boolean pending;

  @Inject
  public void setOrderRouteViewModel(@NonNull OrderRouteViewModel orderRouteViewModel) {
    this.orderRouteViewModel = orderRouteViewModel;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_order_route, container, false);
    recyclerView = view.findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(new ChooseVehicleAdapter(new ArrayList<>()));
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
    orderRouteViewModel.getViewStateLiveData().observe(this, viewState -> {
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
  public void showOrderRoutePending(boolean pending) {
    if (this.pending != pending) {
      showPending(pending);
    }
    this.pending = pending;
  }

  @Override
  public void setRoutePointItems(@NonNull List<RoutePointItem> routePointItems) {
    OrderRouteAdapter adapter = new OrderRouteAdapter(routePointItems,
        orderRouteViewModel::selectNextRoutePoint);
    recyclerView.setAdapter(adapter);
  }

  @Override
  public void showOrderRouteErrorMessage(boolean show) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage(R.string.no_network_connection)
          .setPositiveButton(getString(android.R.string.ok), null)
          .create()
          .show();
    }
  }
}
