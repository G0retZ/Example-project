package com.fasten.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.menu.MenuNavigate;

/**
 * Отображает меню.
 */

public class MenuFragment extends BaseFragment {

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_menu, container, false);
    view.findViewById(R.id.profile).setOnClickListener(v -> navigate(MenuNavigate.PROFILE));
    view.findViewById(R.id.balance).setOnClickListener(v -> navigate(MenuNavigate.BALANCE));
    view.findViewById(R.id.messages).setOnClickListener(v -> navigate(MenuNavigate.MESSAGES));
    view.findViewById(R.id.history).setOnClickListener(v -> navigate(MenuNavigate.HISTORY));
    view.findViewById(R.id.operator).setOnClickListener(v -> navigate(MenuNavigate.OPERATOR));
    view.findViewById(R.id.vehicles).setOnClickListener(v -> navigate(MenuNavigate.VEHICLES));
    return view;
  }
}
