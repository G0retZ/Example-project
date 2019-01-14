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
import com.cargopull.executor_driver.entity.Problem;
import com.cargopull.executor_driver.presentation.reportproblem.ReportProblemViewActions;
import com.cargopull.executor_driver.presentation.reportproblem.ReportProblemViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список причин для отказа от заказа.
 */

public class ReportProblemDialogFragment extends BaseDialogFragment implements
    ReportProblemViewActions {

  private ReportProblemViewModel reportProblemViewModel;
  private RecyclerView recyclerView;

  @Inject
  public void setReportProblemViewModel(@NonNull ReportProblemViewModel reportProblemViewModel) {
    this.reportProblemViewModel = reportProblemViewModel;
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
    reportProblemViewModel.getNavigationLiveData().observe(this, destination -> {
      dismiss();
      if (destination != null) {
        navigate(destination);
      }
    });
    reportProblemViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void showReportProblemPending(boolean pending) {
    showPending(pending, getClass().getSimpleName() + hashCode());
  }

  @Override
  public void showAvailableProblems(boolean show) {
    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setAvailableProblems(@NonNull List<Problem> problems) {
    ReportProblemAdapter adapter = new ReportProblemAdapter(problems,
        reportProblemViewModel::selectItem);
    recyclerView.setAdapter(adapter);
  }
}
