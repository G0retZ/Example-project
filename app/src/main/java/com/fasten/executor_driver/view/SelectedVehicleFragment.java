package com.fasten.executor_driver.view;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleViewActions;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModel;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModelImpl;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Отображает выбранную ТС.
 */

public class SelectedVehicleFragment extends BaseFragment implements SelectedVehicleViewActions {

  private SelectedVehicleViewModel selectedVehicleViewModel;
  private Button changeButton;
  private TextView nameText;

  private ViewModelProvider.Factory viewModelFactory;

  @Inject
  public void setViewModelFactory(
      @Named("selectedVehicle") ViewModelProvider.Factory viewModelFactory) {
    this.viewModelFactory = viewModelFactory;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
    selectedVehicleViewModel = ViewModelProviders.of(this, viewModelFactory)
        .get(SelectedVehicleViewModelImpl.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_selected_vehicle, container, false);
    changeButton = view.findViewById(R.id.changeButton);
    nameText = view.findViewById(R.id.vehicleNameText);

    changeButton.setOnClickListener(v -> selectedVehicleViewModel.changeVehicle());

    selectedVehicleViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    selectedVehicleViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    return view;
  }

  @Override
  public void setVehicleName(String name) {
    nameText.setText(name);
  }

  @Override
  public void enableChangeButton(boolean enable) {
    changeButton.setEnabled(enable);
  }
}
