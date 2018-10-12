package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;

public class OnlineFragmentForMenu extends OnlineFragment {

  private Button takeBreakAction;
  private Button resumeWorkAction;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_online_menu, container, false);
    takeBreakAction = view.findViewById(R.id.takeBreak);
    resumeWorkAction = view.findViewById(R.id.resumeWork);
    return view;
  }

  @Nullable
  @Override
  protected View getBreakText() {
    return null;
  }

  @Nullable
  @Override
  protected View getTakeBreakAction() {
    return takeBreakAction;
  }

  @Nullable
  @Override
  protected View getResumeWorkAction() {
    return resumeWorkAction;
  }
}
