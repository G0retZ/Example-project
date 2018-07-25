package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.vehicleoptions.VehicleOptionsListItems;
import com.cargopull.executor_driver.presentation.vehicleoptions.VehicleOptionsViewActions;
import com.cargopull.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModel;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Отображает список ТС для выбора при выходе на линию.
 */

public class VehicleOptionsFragment extends BaseFragment implements VehicleOptionsViewActions {

  private VehicleOptionsViewModel vehicleOptionsViewModel;
  private RecyclerView recyclerView;
  private TextView errorText;
  private Button readyButton;

  @Inject
  public void setVehicleOptionsViewModel(@NonNull VehicleOptionsViewModel vehicleOptionsViewModel) {
    this.vehicleOptionsViewModel = vehicleOptionsViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_vehicle_options, container, false);
    recyclerView = view.findViewById(R.id.recyclerView);
    errorText = view.findViewById(R.id.errorText);
    readyButton = view.findViewById(R.id.readyButton);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(new ChooseVehicleAdapter(new ArrayList<>()));
    readyButton.setOnClickListener(v -> vehicleOptionsViewModel.setOptions(
        ((VehicleOptionsAdapter) recyclerView.getAdapter()).getVehicleOptionsListItems())
    );
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
    vehicleOptionsViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    vehicleOptionsViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void enableReadyButton(boolean enable) {
    readyButton.setEnabled(enable);
  }

  @Override
  public void showVehicleOptionsPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showVehicleOptionsList(boolean show) {
    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setVehicleOptionsListItems(@NonNull VehicleOptionsListItems vehicleOptionsListItems) {
    VehicleOptionsAdapter adapter = new VehicleOptionsAdapter(vehicleOptionsListItems);
    recyclerView.setAdapter(adapter);
  }

  @Override
  public void showVehicleOptionsListErrorMessage(boolean show) {
    errorText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setVehicleOptionsListErrorMessage(int messageId) {
    errorText.setText(messageId);
  }
}
