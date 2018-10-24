package com.cargopull.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.balance.BalanceViewActions;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModel;
import com.cargopull.executor_driver.presentation.menu.MenuNavigate;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewActions;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListItem;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewActions;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewModel;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;

/**
 * Отображает меню.
 */

public class MenuFragment extends BaseFragment implements BalanceViewActions,
    OnlineSwitchViewActions, PreOrdersListViewActions {

  private AppSettingsService appSettingsService;
  private BalanceViewModel balanceViewModel;
  private OnlineSwitchViewModel onlineSwitchViewModel;
  private PreOrdersListViewModel preOrdersListViewModel;
  private TextView balanceAmount;
  private TextView preOrdersAmount;
  private TextView nightModeValue;
  private boolean nowOnline;
  private DialogFragment aboutFragment;

  @Inject
  public void setAppSettingsService(@NonNull AppSettingsService appSettingsService) {
    this.appSettingsService = appSettingsService;
  }

  @Inject
  public void setBalanceViewModel(@NonNull BalanceViewModel balanceViewModel) {
    this.balanceViewModel = balanceViewModel;
  }

  @Inject
  public void setOnlineSwitchViewModel(@NonNull OnlineSwitchViewModel onlineSwitchViewModel) {
    this.onlineSwitchViewModel = onlineSwitchViewModel;
  }

  @Inject
  public void setPreOrdersListViewModel(PreOrdersListViewModel preOrdersListViewModel) {
    this.preOrdersListViewModel = preOrdersListViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
    rootView.findViewById(R.id.balance).setOnClickListener(v -> navigate(MenuNavigate.BALANCE));
    Context context = getContext();
    rootView.findViewById(R.id.exit).setOnClickListener(
        v -> new Builder(context)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(getString(android.R.string.ok), ((dialog, which) -> {
              if (nowOnline) {
                onlineSwitchViewModel.setNewState(false);
              }
              showPending(true, "exit");
              navigate(CommonNavigate.EXIT);
            }))
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show()
    );
    balanceAmount = rootView.findViewById(R.id.balanceAmount);
    rootView.findViewById(R.id.preOrders)
        .setOnClickListener(v -> navigate(MenuNavigate.PRE_ORDERS));
    preOrdersAmount = rootView.findViewById(R.id.preOrdersAmount);
    aboutFragment = new AboutDialogFragment();
    rootView.findViewById(R.id.about).setOnClickListener(
        v -> aboutFragment.show(Objects.requireNonNull(getFragmentManager()), "about")
    );
    rootView.findViewById(R.id.nightMode)
        .setOnClickListener(v -> navigate(MenuNavigate.NIGHT_MODE));
    nightModeValue = rootView.findViewById(R.id.nightModeValue);
    return rootView;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    switch (appSettingsService.getNumber("mode")) {
      case AppCompatDelegate.MODE_NIGHT_YES:
        nightModeValue.setText(R.string.night_mode_on);
        break;
      case AppCompatDelegate.MODE_NIGHT_NO:
        nightModeValue.setText(R.string.night_mode_off);
        break;
      default:
        nightModeValue.setText(R.string.night_mode_auto);
    }
    balanceViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    balanceViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    onlineSwitchViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    onlineSwitchViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
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
  public void showMainAccountAmount(long amount) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      amount = Math.round(amount / 100f);
    }
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    balanceAmount.setText(decimalFormat.format(amount));
    if (amount < 0) {
      balanceAmount.setTextColor(getResources().getColor(R.color.colorError));
    } else {
      balanceAmount.setTextColor(getResources().getColor(R.color.textColorPrimary));
    }
  }

  @Override
  public void showBonusAccountAmount(long amount) {
  }

  @Override
  public void showBalancePending(boolean pending) {
    showPending(pending, toString() + "0");
  }

  @Override
  public void showBreakText(boolean show) {
  }

  @Override
  public void showTakeBreakButton(boolean show) {
    nowOnline = show;
  }

  @Override
  public void showResumeWorkButton(boolean show) {
  }

  @Override
  public void showSwitchPending(boolean pending) {
    showPending(pending, toString() + "1");
  }

  @Override
  public void showPreOrdersListPending(boolean pending) {
  }

  @Override
  public void showPreOrdersList(boolean show) {
  }

  @Override
  public void setPreOrdersListItems(@NonNull List<PreOrdersListItem> preOrdersListItems) {
    int count = 0;
    for (PreOrdersListItem preOrdersListItem : preOrdersListItems) {
      count += preOrdersListItem.getViewType() == 1 ? 1 : 0;
    }
    preOrdersAmount.setText(String.valueOf(count));
  }

  @Override
  public void showEmptyPreOrdersList(boolean show) {
  }
}
