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
import com.cargopull.executor_driver.presentation.orderroute.OrderRouteViewActions;
import com.cargopull.executor_driver.presentation.orderroute.OrderRouteViewModel;
import com.cargopull.executor_driver.presentation.orderroute.RoutePointItem;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список ТС для выбора при выходе на линию.
 */

public class WaitingForClientRouteFragment extends BaseFragment implements OrderRouteViewActions {

  private OrderRouteViewModel orderRouteViewModel;
  private RecyclerView recyclerView;

  @Inject
  public void setOrderRouteViewModel(@NonNull OrderRouteViewModel orderRouteViewModel) {
    this.orderRouteViewModel = orderRouteViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_waiting_for_client_route, container, false);
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
    orderRouteViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void showOrderRoutePending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void setRoutePointItems(@NonNull List<RoutePointItem> routePointItems) {
    WaitingForClientRouteAdapter adapter = new WaitingForClientRouteAdapter(routePointItems);
    recyclerView.setAdapter(adapter);
  }
}
