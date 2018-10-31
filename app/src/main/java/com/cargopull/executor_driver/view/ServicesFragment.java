package com.cargopull.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.services.ServicesListItem;
import com.cargopull.executor_driver.presentation.services.ServicesSliderViewActions;
import com.cargopull.executor_driver.presentation.services.ServicesSliderViewModel;
import com.cargopull.executor_driver.presentation.services.ServicesViewActions;
import com.cargopull.executor_driver.presentation.services.ServicesViewModel;
import java.text.DecimalFormat;
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
  private ServicesAdapter servicesAdapter;
  private TextView errorText;
  private Button readyButton;
  private Context context;

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
  public void setServicesSliderViewModel(@NonNull ServicesSliderViewModel servicesSliderViewModel) {
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
    servicesAdapter = new ServicesAdapter(() -> recyclerView.scrollToPosition(0));
    recyclerView.setAdapter(servicesAdapter);
    readyButton.setOnClickListener(
        v -> servicesViewModel.setServices(servicesAdapter.getServicesListItems())
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
    showPending(pending, toString());
  }

  @Override
  public void showServicesList(boolean show) {
    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setServicesListItems(@NonNull List<ServicesListItem> servicesListItems) {
    servicesAdapter.submitList(servicesListItems);
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
    if (!getResources().getBoolean(R.bool.show_cents)) {
      minPrice = Math.round(minPrice / 100f);
    }
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    minPriceText.setText(decimalFormat.format(minPrice));
  }

  @Override
  public void setMaxPrice(int maxPrice) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      maxPrice = Math.round(maxPrice / 100f);
    }
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    maxPriceText.setText(decimalFormat.format(maxPrice));
  }
}
