package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewActions;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewModel;
import javax.inject.Inject;

/**
 * Отображает кнопку рассмотрения предложения предварительного заказа.
 */

public class NewPreOrderButtonFragment extends BaseFragment implements PreOrderViewActions {

  private PreOrderViewModel preOrderViewModel;
  private ImageButton goToPreOrder;

  @Inject
  public void setPreOrderViewModel(@NonNull PreOrderViewModel preOrderViewModel) {
    this.preOrderViewModel = preOrderViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_new_pre_order_button, container, false);
    goToPreOrder = view.findViewById(R.id.goToPreOrder);
    goToPreOrder.setOnClickListener(v -> preOrderViewModel.preOrderConsumed());
    return view;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    preOrderViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    preOrderViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void showPreOrderAvailable(boolean show) {
    goToPreOrder.setVisibility(show ? View.VISIBLE : View.GONE);
  }
}
