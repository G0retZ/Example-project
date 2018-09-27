package com.cargopull.executor_driver.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.R;

/**
 * Отображает информацию о приложении.
 */

public class AboutDialogFragment extends DialogFragment {

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_about, container, false);
    TextView textView = view.findViewById(R.id.versionText);
    textView.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));
    view.findViewById(R.id.okButton).setOnClickListener(v -> dismiss());
    return view;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.setCancelable(true);
    return dialog;
  }
}
