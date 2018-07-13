package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleListItem;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewActions;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewModel;
import com.cargopull.executor_driver.presentation.selectedvehicle.SelectedVehicleViewActions;
import com.cargopull.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModel;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает выбранную ТС.
 */

public class SelectedVehicleFragment extends BaseFragment implements SelectedVehicleViewActions,
    ChooseVehicleViewActions {

  private SelectedVehicleViewModel selectedVehicleViewModel;
  private ChooseVehicleViewModel chooseVehicleViewModel;
  private Button changeButton;
  private TextView nameText;

  @Inject
  public void setSelectedVehicleViewModel(
      @NonNull SelectedVehicleViewModel selectedVehicleViewModel) {
    this.selectedVehicleViewModel = selectedVehicleViewModel;
  }

  @Inject
  public void setChooseVehicleViewModel(@NonNull ChooseVehicleViewModel chooseVehicleViewModel) {
    this.chooseVehicleViewModel = chooseVehicleViewModel;
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
    chooseVehicleViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    chooseVehicleViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void setVehicleName(String name) {
    nameText.setText(name);
  }

  @Override
  public void enableChangeButton(boolean enable) {
    changeButton.setEnabled(enable);
  }

  @Override
  public void showVehicleListPending(boolean pending) {

  }

  @Override
  public void showVehicleList(boolean show) {
    changeButton.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setVehicleListItems(@NonNull List<ChooseVehicleListItem> chooseVehicleListItems) {
    changeButton.setVisibility(chooseVehicleListItems.size() > 1 ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showVehicleListErrorMessage(boolean show) {

  }

  @Override
  public void setVehicleListErrorMessage(int messageId) {

  }
}
