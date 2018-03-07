package com.fasten.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleListItem;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleViewActions;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleViewModel;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список ТС для выбора при выходе на линию.
 */

public class ChooseVehicleFragment extends BaseFragment implements ChooseVehicleViewActions {

  private ChooseVehicleViewModel chooseVehicleViewModel;
  private RecyclerView recyclerView;
  private ProgressBar pendingIndicator;
  private TextView errorText;
  @Nullable
  private Disposable disposable;

  @Inject
  public void setChooseVehicleViewModel(@NonNull ChooseVehicleViewModel chooseVehicleViewModel) {
    this.chooseVehicleViewModel = chooseVehicleViewModel;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_choose_vehicle, container, false);
    recyclerView = view.findViewById(R.id.recyclerView);
    pendingIndicator = view.findViewById(R.id.pending);
    errorText = view.findViewById(R.id.errorText);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(new ChooseVehicleAdapter(new ArrayList<>()));

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
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (disposable != null) {
      disposable.dispose();
    }
  }

  @Override
  public void showVehicleListPending(boolean pending) {
    pendingIndicator.setVisibility(pending ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showVehicleList(boolean show) {
    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setVehicleListItems(@NonNull List<ChooseVehicleListItem> chooseVehicleListItems) {
    ChooseVehicleAdapter adapter = new ChooseVehicleAdapter(chooseVehicleListItems);
    if (disposable != null) {
      disposable.dispose();
    }
    disposable = adapter.getSelectionCallbacks().subscribe(chooseVehicleViewModel::selectItem);
    recyclerView.setAdapter(adapter);
  }

  @Override
  public void showVehicleListErrorMessage(boolean show) {
    errorText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setVehicleListErrorMessage(int messageId) {
    errorText.setText(messageId);
  }
}
