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
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.balance.BalanceViewActions;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModel;
import com.cargopull.executor_driver.presentation.menu.MenuNavigate;
import com.cargopull.executor_driver.presentation.menu.MenuViewModel;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewActions;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewActions;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListItem;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewActions;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewModel;
import java.text.DecimalFormat;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает меню.
 */

public class MenuFragment extends BaseFragment implements BalanceViewActions,
    OnlineSwitchViewActions, PreOrdersListViewActions, OnlineButtonViewActions {

  private AppSettingsService appSettingsService;
  private BalanceViewModel balanceViewModel;
  private OnlineSwitchViewModel onlineSwitchViewModel;
  private PreOrdersListViewModel preOrdersListViewModel;
  private MenuViewModel menuViewModel;
  private OnlineButtonViewModel onlineButtonViewModel;
  private TextView balanceAmount;
  private TextView preOrdersAmount;
  private TextView nightModeValue;
  private boolean nowOnline;

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

  public void setMenuViewModel(MenuViewModel menuViewModel) {
    this.menuViewModel = menuViewModel;
  }

  @Inject
  public void setOnlineButtonViewModel(@NonNull OnlineButtonViewModel onlineButtonViewModel) {
    this.onlineButtonViewModel = onlineButtonViewModel;
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
    rootView.findViewById(R.id.history)
        .setOnClickListener(v -> navigate(MenuNavigate.ORDERS_HISTORY));
    rootView.findViewById(R.id.nightMode)
        .setOnClickListener(v -> navigate(MenuNavigate.NIGHT_MODE));
    rootView.findViewById(R.id.about).setOnClickListener(v -> navigate(MenuNavigate.ABOUT));
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
    menuViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    menuViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    onlineButtonViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    onlineButtonViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  protected void navigate(@NonNull String destination) {
    if (destination.equals(MenuNavigate.ORDERS_FILTER)) {
      onlineButtonViewModel.goOnline();
    } else {
      super.navigate(destination);
    }
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

  @Override
  public void enableGoOnlineButton(boolean enable) {
    //TODO: костыль для получения данных для фильтра
    setEnabled(R.id.filter, enable);
  }

  @Override
  public void showGoOnlinePending(boolean pending) {
    showPending(pending, toString() + "1");
  }
}
