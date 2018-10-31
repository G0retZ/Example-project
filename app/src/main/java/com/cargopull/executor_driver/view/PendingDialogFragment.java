package com.cargopull.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Отображает индикатор процесса.
 */

public class PendingDialogFragment extends BaseDialogFragment {

  private volatile boolean isShowing = false;
  private FloatingActionButton exitAction;
  private final Runnable runnable = () -> exitAction.show();

  @Override
  public void show(FragmentManager manager, String tag) {
    if (!isShowing) {
      isShowing = true; // апдейтим флаг показа фрагмента
      super.show(manager, tag);
    }
  }

  @Override
  public void dismiss() {
    if (isShowing) {
      super.dismiss();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dialog);
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
  public void onResume() {
    super.onResume();
    isShowing = true;
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    isShowing = false;
    exitAction.hide();
    exitAction.removeCallbacks(runnable);
  }
}
