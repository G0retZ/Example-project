package com.fasten.executor_driver.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.fasten.executor_driver.R;

/**
 * Отображает индикатор процесса.
 */

public class PendingDialogFragment extends DialogFragment {

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_pending, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Window window = getDialog().getWindow();
    if (window != null) {
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        window.setBackgroundDrawable(
            new ColorDrawable(getResources().getColor(R.color.colorSmoke, null)));
      } else {
        window.setBackgroundDrawable(
            new ColorDrawable(getResources().getColor(R.color.colorSmoke))
        );
      }
    }
  }
}
