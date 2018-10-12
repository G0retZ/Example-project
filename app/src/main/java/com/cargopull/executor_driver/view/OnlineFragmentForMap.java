package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;

public class OnlineFragmentForMap extends OnlineFragment {

  private View breakText;
  private Button resumeWorkAction;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    breakText = inflater.inflate(R.layout.fragment_online_map, container, false);
    resumeWorkAction = breakText.findViewById(R.id.resumeWork);
    return breakText;
  }

  @Nullable
  @Override
  protected View getBreakText() {
    return breakText;
  }

  @Nullable
  @Override
  protected View getTakeBreakAction() {
    return null;
  }

  @Nullable
  @Override
  protected View getResumeWorkAction() {
    return resumeWorkAction;
  }
}
