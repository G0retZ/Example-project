package com.cargopull.executor_driver.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.cargopull.executor_driver.R;

/**
 * Отображает запрос включения геолокации.
 */

public class GeoEngagementDialogFragment extends BaseDialogFragment {

  private volatile boolean isShowing = false;

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
    View view = inflater.inflate(R.layout.fragment_geo_engagement, container, false);
    view.findViewById(R.id.settings).setOnClickListener(
        v -> v.getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    );
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    isShowing = true;
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
}
