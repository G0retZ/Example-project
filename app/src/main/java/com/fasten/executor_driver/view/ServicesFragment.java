package com.fasten.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.services.ServicesListItem;
import com.fasten.executor_driver.presentation.services.ServicesViewActions;
import com.fasten.executor_driver.presentation.services.ServicesViewModel;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список ТС для выбора при выходе на линию.
 */

public class ServicesFragment extends BaseFragment implements ServicesViewActions {

  private ServicesViewModel servicesViewModel;
  private RecyclerView recyclerView;
  private FrameLayout pendingIndicator;
  private TextView errorText;
  private Button readyButton;

  @Inject
  public void setServicesViewModel(@NonNull ServicesViewModel servicesViewModel) {
    this.servicesViewModel = servicesViewModel;
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
    recyclerView.setAdapter(new ServicesAdapter());
    readyButton.setOnClickListener(v -> servicesViewModel.setServices(
        ((ServicesAdapter) recyclerView.getAdapter()).getServicesListItems())
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
    servicesViewModel.getViewStateLiveData().observe(this, viewState -> {
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
  public void showServicesPending(boolean pending) {
    pendingIndicator.setVisibility(pending ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showServicesList(boolean show) {
    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setServicesListItems(@NonNull List<ServicesListItem> servicesListItems) {
    ((ServicesAdapter) recyclerView.getAdapter()).submitList(servicesListItems);
  }

  @Override
  public void showServicesListErrorMessage(boolean show) {
    errorText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setServicesListErrorMessage(int messageId) {
    errorText.setText(messageId);
  }
}
