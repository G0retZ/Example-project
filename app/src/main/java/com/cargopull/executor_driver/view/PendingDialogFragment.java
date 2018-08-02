package com.cargopull.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Отображает индикатор процесса.
 */

public class PendingDialogFragment extends BaseDialogFragment {

  @NonNull
  private final AtomicBoolean isShowing = new AtomicBoolean(false);
  private FloatingActionButton exitAction;
  private final Runnable runnable = () -> exitAction.setVisibility(View.VISIBLE);

  @Override
  public void show(FragmentManager manager, String tag) {
    isShowing.set(true); // апдейтим флаг показа фрагмента
    super.show(manager, tag);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_pending, container, false);
    Context context = getContext();
    exitAction = view.findViewById(R.id.exit);
    exitAction.setOnClickListener(
        v -> new Builder(context)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(getString(android.R.string.ok),
                (dialog, which) -> navigate(CommonNavigate.EXIT))
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show()
    );
    exitAction.postDelayed(runnable, 30_000);
    return view;
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

  @Override
  public void onResume() {
    super.onResume();
    isShowing.set(true);
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    isShowing.set(false);
    exitAction.setVisibility(View.GONE);
    exitAction.removeCallbacks(runnable);
  }

  /**
   * Проверяем, если фрагмент показывается или был запланирован для показа
   */
  public boolean isShowing() {
    return isShowing.get();
  }
}
