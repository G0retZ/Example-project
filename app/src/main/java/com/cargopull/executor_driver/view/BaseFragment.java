package com.cargopull.executor_driver.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import com.cargopull.executor_driver.application.BaseActivity;
import com.cargopull.executor_driver.application.OnBackPressedInterceptor;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Fragment} с поддержкой:
 * <ul>
 * <li>onBackPressed()</li>
 * <li>Отображения процесса</li>
 * <li>Получения View с кешированием</li>
 * </ul>
 */

public class BaseFragment extends Fragment implements OnBackPressedInterceptor,
    FragmentViewActions {

  @SuppressLint("UseSparseArrays")
  @NonNull
  private final Map<Integer, View> foundViews = new HashMap<>();
  private View rootView;
  @Nullable
  private AlertDialog alertDialog;

  @Nullable
  private BaseActivity baseActivity;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      baseActivity = (BaseActivity) context;
    } catch (ClassCastException e) {
      Log.w(this.getClass().getName(), context.getClass().getName() +
          " не наследует BaseActivity. OnBackPressed никогда не будет вызван.");
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    rootView = view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (baseActivity != null) {
      onDependencyInject(baseActivity.getDiComponent());
    }
  }

  @Override
  public void onDetach() {
    if (alertDialog != null) {
      alertDialog.dismiss();
    }
    super.onDetach();
    baseActivity = null;
  }

  /**
   * Колбэк для внедрения зависимостей. Вызывается сразу после завершения {@link #onCreate(Bundle)}
   * здесь.
   *
   * @param appComponent - компонент, который может произвести внедрение
   */
  protected void onDependencyInject(AppComponent appComponent) {
  }

  @Override
  public void onResume() {
    super.onResume();
    if (baseActivity != null) {
      baseActivity.registerOnBackPressedInterceptor(this);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (baseActivity != null) {
      baseActivity.unregisterOnBackPressedInterceptor(this);
    }
  }

  @Override
  public boolean onBackPressed() {
    return false;
  }

  /**
   * Метод перехода куда либо. Позволяет отвязать {@link android.app.Fragment} от конкретных {@link
   * Activity}.
   *
   * @param destination пункт назначения
   */
  protected void navigate(@NonNull String destination) {
    if (baseActivity != null) {
      baseActivity.navigate(destination);
    }
  }

  /**
   * Показать экран процесса.
   *
   * @param show - показать или нет.
   * @param blockerId - Уникальный ИД блокирующего.
   */
  protected void showPending(boolean show, @NonNull String blockerId) {
    if (baseActivity != null) {
      baseActivity.blockWithPending(show, blockerId);
    }
  }

  /**
   * Получить View по его ИД для каких-либо действий.
   *
   * @param id - ИД View для поиска.
   */
  @SuppressWarnings("unchecked")
  @Nullable
  <T extends View> T findViewById(@IdRes int id) {
    if (!foundViews.containsKey(id)) {
      foundViews.put(id, rootView.findViewById(id));
    }
    return (T) foundViews.get(id);
  }

  @Override
  public void blockWithPending(@NonNull String blockerId) {
    if (baseActivity != null) {
      baseActivity.blockWithPending(toString() + ">" + blockerId);
    }
  }

  @Override
  public void unblockWithPending(@NonNull String blockerId) {
    if (baseActivity != null) {
      baseActivity.unblockWithPending(toString() + ">" + blockerId);
    }
  }

  @Override
  public void setVisible(int id, boolean visible) {
    View view = findViewById(id);
    if (view != null) {
      view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
  }

  @Override
  public void setText(@IdRes int id, @NonNull String text) {
    TextView textView = findViewById(id);
    if (textView != null) {
      textView.setText(text);
    }
  }

  @Override
  public void setText(@IdRes int id, @StringRes int stringId) {
    TextView textView = findViewById(id);
    if (textView != null) {
      textView.setText(stringId);
    }
  }

  @Override
  public void setFormattedText(@IdRes int id, @StringRes int stringId, Object... formatArgs) {
    TextView textView = findViewById(id);
    if (textView != null) {
      textView.setText(getString(stringId, formatArgs));
    }
  }

  @Override
  public void setTextColor(@IdRes int id, @ColorRes int colorId) {
    TextView textView = findViewById(id);
    if (textView != null) {
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        textView.setTextColor(getResources().getColor(colorId, null));
      } else {
        textView.setTextColor(getResources().getColor(colorId));
      }
    }
  }

  @Override
  public void setImage(int id, int drawableId) {
    ImageView imageView = findViewById(id);
    if (imageView != null) {
      imageView.setImageResource(drawableId);
    }
  }

  @Override
  public void setImage(int id, @NonNull String drawableUrl) {
    ImageView imageView = findViewById(id);
    if (imageView != null) {
      Picasso.get().load(drawableUrl).into(imageView);
    }
  }

  @Override
  public void dismissDialog() {
    if (alertDialog != null) {
      alertDialog.dismiss();
    }
  }

  @Override
  public void showPersistentDialog(int stringId, @Nullable Runnable okAction) {
    dismissDialog();
    alertDialog = new Builder(baseActivity)
        .setMessage(stringId)
        .setCancelable(false)
        .setPositiveButton(android.R.string.ok, okAction == null ? null : (a, b) -> okAction.run())
        .create();
    alertDialog.show();
  }

  @Override
  public void showPersistentDialog(@NonNull String message, @Nullable Runnable okAction) {
    dismissDialog();
    alertDialog = new Builder(baseActivity)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(android.R.string.ok, okAction == null ? null : (a, b) -> okAction.run())
        .create();
    alertDialog.show();
  }

  @Override
  public void setClickAction(int id, @Nullable Runnable okAction) {
    View view = findViewById(id);
    if (view != null) {
      view.setOnClickListener(okAction == null ? null : v -> okAction.run());
    }
  }

  @Override
  public void setEnabled(int id, boolean enable) {
    View view = findViewById(id);
    if (view != null) {
      view.setEnabled(enable);
    }
  }
}
