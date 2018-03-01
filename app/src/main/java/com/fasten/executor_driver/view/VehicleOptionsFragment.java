package com.fasten.executor_driver.view;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.options.OptionsListItems;
import com.fasten.executor_driver.presentation.options.OptionsViewActions;
import com.fasten.executor_driver.presentation.options.OptionsViewModel;
import com.fasten.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModelImpl;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Отображает список ТС для выбора при выходе на линию.
 */

public class VehicleOptionsFragment extends BaseFragment implements OptionsViewActions {

  private OptionsViewModel vehicleOptionsViewModel;
  private RecyclerView recyclerView;
  private ProgressBar pendingIndicator;
  private TextView errorText;
  private Button readyButton;

  private ViewModelProvider.Factory viewModelFactory;

  @Inject
  public void setViewModelFactory(
      @Named("vehicleOptions") ViewModelProvider.Factory viewModelFactory) {
    this.viewModelFactory = viewModelFactory;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
    vehicleOptionsViewModel = ViewModelProviders.of(this, viewModelFactory)
        .get(VehicleOptionsViewModelImpl.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_vehicle_options, container, false);
    recyclerView = view.findViewById(R.id.recyclerView);
    pendingIndicator = view.findViewById(R.id.pending);
    errorText = view.findViewById(R.id.errorText);
    readyButton = view.findViewById(R.id.readyButton);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(new ChooseVehicleAdapter(new ArrayList<>()));

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
    readyButton.setOnClickListener(v -> vehicleOptionsViewModel.setOptions(
        ((OptionsAdapter) recyclerView.getAdapter()).getOptionsListItems())
    );
    return view;
  }

  @Override
  public void enableReadyButton(boolean enable) {
    readyButton.setEnabled(enable);
  }

  @Override
  public void showVehicleOptionsPending(boolean pending) {
    pendingIndicator.setVisibility(pending ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showVehicleOptionsList(boolean show) {
    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setVehicleOptionsListItems(
      @NonNull OptionsListItems optionsListItems) {
    OptionsAdapter adapter = new OptionsAdapter(optionsListItems);
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
