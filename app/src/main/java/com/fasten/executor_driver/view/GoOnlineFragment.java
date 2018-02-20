package com.fasten.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewActions;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewModelImpl;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Отображает кнопку выхода на линию.
 */

public class GoOnlineFragment extends BaseFragment implements OnlineButtonViewActions {

  private OnlineButtonViewModel onlineButtonViewModel;
  private Button goOnlineRequest;
  private Context context;

  private ViewModelProvider.Factory viewModelFactory;

  @Inject
  public void setViewModelFactory(@Named("goOnline") ViewModelProvider.Factory viewModelFactory) {
    this.viewModelFactory = viewModelFactory;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for injection
    appComponent.inject(this);
    onlineButtonViewModel = ViewModelProviders.of(this, viewModelFactory)
        .get(OnlineButtonViewModelImpl.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_go_online, container, false);
    goOnlineRequest = view.findViewById(R.id.goOnline);
    goOnlineRequest.setOnClickListener(v -> onlineButtonViewModel.goOnline());
    onlineButtonViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    return view;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public void enableGoOnlineButton(boolean enable) {
    goOnlineRequest.setEnabled(enable);
  }

  @Override
  public void showGoOnlineError(@Nullable Throwable error) {
    if (error == null) {
      return;
    }
    new Builder(context)
        .setTitle(R.string.error)
        .setMessage(error.getMessage())
        .setPositiveButton(getString(android.R.string.ok), null)
        .setNegativeButton(getString(android.R.string.cancel), null)
        .create()
        .show();
  }
}
