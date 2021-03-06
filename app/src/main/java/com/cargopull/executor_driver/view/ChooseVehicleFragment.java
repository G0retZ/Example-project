package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleListItem;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewActions;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewModel;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список ТС для выбора при выходе на линию.
 */

public class ChooseVehicleFragment extends BaseFragment implements ChooseVehicleViewActions {

  private ChooseVehicleViewModel chooseVehicleViewModel;
  private RecyclerView recyclerView;
  private TextView errorText;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public void setChooseVehicleViewModel(@NonNull ChooseVehicleViewModel chooseVehicleViewModel) {
    this.chooseVehicleViewModel = chooseVehicleViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_choose_vehicle, container, false);
    recyclerView = view.findViewById(R.id.recyclerView);
    errorText = view.findViewById(R.id.errorText);
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
  public void onDestroyView() {
    super.onDestroyView();
    disposable.dispose();
  }

  @Override
  public void showVehicleListPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showVehicleList(boolean show) {
    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setVehicleListItems(@NonNull List<ChooseVehicleListItem> chooseVehicleListItems) {
    ChooseVehicleAdapter adapter = new ChooseVehicleAdapter(chooseVehicleListItems);
    disposable.dispose();
    disposable = adapter.getSelectionCallbacks()
        .subscribe(chooseVehicleViewModel::selectItem, Throwable::printStackTrace);
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
