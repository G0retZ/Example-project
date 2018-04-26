package com.fasten.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.services.ServicesListItem;
import com.fasten.executor_driver.presentation.services.ServicesSliderViewActions;
import com.fasten.executor_driver.presentation.services.ServicesSliderViewModel;
import com.fasten.executor_driver.presentation.services.ServicesViewActions;
import com.fasten.executor_driver.presentation.services.ServicesViewModel;
import java.util.List;
import javax.inject.Inject;

/**
 * Отображает список ТС для выбора при выходе на линию.
 */

public class ServicesFragment extends BaseFragment implements ServicesViewActions,
    ServicesSliderViewActions {

  private ServicesViewModel servicesViewModel;
  private ServicesSliderViewModel servicesSliderViewModel;
  private AppCompatSeekBar priceSeekBar;
  private TextView minPriceText;
  private TextView maxPriceText;
  private RecyclerView recyclerView;
  private TextView errorText;
  private Button readyButton;
  private Context context;
  private boolean pending;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Inject
  public void setServicesViewModel(@NonNull ServicesViewModel servicesViewModel) {
    this.servicesViewModel = servicesViewModel;
  }

  @Inject
  public void setServicesSliderViewModel(ServicesSliderViewModel servicesSliderViewModel) {
    this.servicesSliderViewModel = servicesSliderViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_services, container, false);
    priceSeekBar = view.findViewById(R.id.priceSeekBar);
    minPriceText = view.findViewById(R.id.minPriceText);
    maxPriceText = view.findViewById(R.id.maxPriceText);
    recyclerView = view.findViewById(R.id.recyclerView);
    errorText = view.findViewById(R.id.errorText);
    readyButton = view.findViewById(R.id.readyButton);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(new ServicesAdapter());
    readyButton.setOnClickListener(v -> servicesViewModel.setServices(
        ((ServicesAdapter) recyclerView.getAdapter()).getServicesListItems())
    );
    priceSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
          servicesViewModel.setSliderPosition(100 - progress);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });
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
    servicesSliderViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    servicesViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public void enableReadyButton(boolean enable) {
    readyButton.setEnabled(enable);
  }

  @Override
  public void showServicesPending(boolean pending) {
    if (this.pending != pending) {
      showPending(pending);
    }
    this.pending = pending;
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
  public void showServicesListErrorMessage(boolean show, int messageId) {
    errorText.setVisibility(show ? View.VISIBLE : View.GONE);
    if (show) {
      errorText.setText(messageId);
    }
  }

  @Override
  public void showServicesListResolvableErrorMessage(boolean show, int messageId) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage(messageId)
          .setPositiveButton(getString(android.R.string.ok),
              (dialog, which) -> servicesViewModel.errorConsumed())
          .create()
          .show();
    }
  }

  @Override
  public void setSliderPosition(int position) {
    priceSeekBar.setProgress(100 - position);
  }

  @Override
  public void setMinPrice(int minPrice) {
    minPriceText.setText(getString(R.string.money_amount, minPrice));
  }

  @Override
  public void setMaxPrice(int maxPrice) {
    maxPriceText.setText(getString(R.string.money_amount, maxPrice));
  }
}
