package com.cargopull.executor_driver.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
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
import com.cargopull.executor_driver.presentation.ViewActions;

/**
 * {@link Fragment} с поддержкой:
 * <ul>
 * <li>onBackPressed()</li>
 * <li>Отображения процесса</li>
 * <li>Получения View с кешированием</li>
 * </ul>
 */

public class BaseFragment extends Fragment implements OnBackPressedInterceptor, ViewActions {

  private View rootView;
  @NonNull
  private final SparseArray<View> foundViews = new SparseArray<>();

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
   * Метод перехода куда либо.
   * Позволяет отвязать {@link android.app.Fragment} от конкретных {@link Activity}.
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

  protected boolean isViewPresent(@IdRes int id) {
    return true;
  }

  /**
   * Получить View по его ИД для каких-либо действий.
   *
   * @param id - ИД View для поиска.
   */
  @SuppressWarnings("unchecked")
  @NonNull
  <T extends View> T findView(@IdRes int id) {
    if (foundViews.get(id) == null) {
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
  public void showView(@IdRes int id) {
    if (isViewPresent(id)) {
      if (foundViews.get(id) == null) {
        foundViews.put(id, rootView.findViewById(id));
      }
      foundViews.get(id).setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void hideView(@IdRes int id) {
    if (isViewPresent(id)) {
      if (foundViews.get(id) == null) {
        foundViews.put(id, rootView.findViewById(id));
      }
      foundViews.get(id).setVisibility(View.GONE);
    }
  }

  @Override
  public void setText(@IdRes int id, @NonNull String text) {
    if (isViewPresent(id)) {
      if (foundViews.get(id) == null) {
        foundViews.put(id, rootView.findViewById(id));
      }
      ((TextView) foundViews.get(id)).setText(text);
    }
  }

  @Override
  public void setText(@IdRes int id, @StringRes int stringId) {
    if (isViewPresent(id)) {
      if (foundViews.get(id) == null) {
        foundViews.put(id, rootView.findViewById(id));
      }
      ((TextView) foundViews.get(id)).setText(stringId);
    }
  }

  @Override
  public void setFormattedText(@IdRes int id, @StringRes int stringId, Object... formatArgs) {
    if (isViewPresent(id)) {
      if (foundViews.get(id) == null) {
        foundViews.put(id, rootView.findViewById(id));
      }
      ((TextView) foundViews.get(id)).setText(getString(stringId, formatArgs));
    }
  }

  @Override
  public void setTextColor(@IdRes int id, @ColorRes int colorId) {
    if (isViewPresent(id)) {
      if (foundViews.get(id) == null) {
        foundViews.put(id, rootView.findViewById(id));
      }
      ((TextView) foundViews.get(id)).setTextColor(getResources().getColor(colorId));
    }
  }

  @Override
  public void setImage(int id, int drawableId) {
    if (isViewPresent(id)) {
      if (foundViews.get(id) == null) {
        foundViews.put(id, rootView.findViewById(id));
      }
      ((ImageView) foundViews.get(id)).setImageResource(drawableId);
    }
  }
}
