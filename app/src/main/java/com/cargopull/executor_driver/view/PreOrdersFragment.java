package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListItem;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewActions;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewModel;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список запланированных предзаказов.
 */

public class PreOrdersFragment extends BaseFragment implements PreOrdersListViewActions {

  private PreOrdersListViewModel preOrdersListViewModel;
  private RecyclerView recyclerView;
  private PreOrdersAdapter preOrdersAdapter;
  private TextView emptyText;

  @Inject
  public void setPreOrdersListViewModel(@NonNull PreOrdersListViewModel preOrdersListViewModel) {
    this.preOrdersListViewModel = preOrdersListViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_pre_orders, container, false);
    recyclerView = view.findViewById(R.id.recyclerView);
    emptyText = view.findViewById(R.id.emptyText);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    preOrdersAdapter = new PreOrdersAdapter();
    recyclerView.setAdapter(preOrdersAdapter);
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
    preOrdersListViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    preOrdersListViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void showPreOrdersListPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showPreOrdersList(boolean show) {
    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setPreOrdersListItems(@NonNull List<PreOrdersListItem> preOrdersListItems) {
    preOrdersAdapter.submitList(preOrdersListItems);
  }

  @Override
  public void showEmptyPreOrdersList(boolean show) {
    emptyText.setVisibility(show ? View.VISIBLE : View.GONE);
  }
}
