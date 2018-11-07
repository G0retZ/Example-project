package com.cargopull.executor_driver.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.geolocationstate.GeoLocationStateViewActions;
import com.cargopull.executor_driver.presentation.geolocationstate.GeoLocationStateViewModel;
import javax.inject.Inject;

/**
 * Отображает запрос включения геолокации.
 */

public class GeoEngagementDialogFragment extends BaseDialogFragment implements
    GeoLocationStateViewActions {

  private volatile boolean isShowing = false;
  private GeoLocationStateViewModel geoLocationStateViewModel;
  private View rootView;

  @Inject
  public void setGeoLocationStateViewModel(
      @NonNull GeoLocationStateViewModel geoLocationStateViewModel) {
    this.geoLocationStateViewModel = geoLocationStateViewModel;
  }

  @Override
  public void show(FragmentManager manager, String tag) {
    if (!isShowing) {
      isShowing = true; // апдейтим флаг показа фрагмента
      super.show(manager, tag);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_geo_engagement, container, false);
    rootView.findViewById(R.id.settings).setOnClickListener(
        v -> v.getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    );
    return rootView;
  }

  @Override
  void onDependencyInject(AppComponent appComponent) {
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    geoLocationStateViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    isShowing = true;
    geoLocationStateViewModel.checkSettings();
  }

  @Override
  public void dismiss() {
    if (isShowing) {
      super.dismiss();
    }
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    isShowing = false;
  }

  @Override
  public void setVisible(@IdRes int id, boolean visible) {

  }

  @Override
  public void setText(@IdRes int id, @StringRes int stringId) {
    ((TextView) rootView.findViewById(id)).setText(stringId);
  }

  @Override
  public void setImage(@IdRes int id, @DrawableRes int drawableId) {
    ((ImageView) rootView.findViewById(id)).setImageResource(drawableId);
  }
}
