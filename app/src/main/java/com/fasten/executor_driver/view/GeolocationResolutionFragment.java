package com.fasten.executor_driver.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fasten.executor_driver.R;
import io.reactivex.disposables.Disposable;

public class GeolocationResolutionFragment extends BaseFragment {

  public static final String NAVIGATE_TO_SETTINGS = "to.App.Permissions.Settings";
  public static final String NAVIGATE_TO_RESOLVED = "to.App.Permissions.Resolved";

  private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION};

  @Nullable
  private PermissionChecker permissionChecker;
  @Nullable
  private Disposable permissionDisposable;

  private Activity activity;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.activity = (Activity) context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_geo_resolution, container, false);
    view.findViewById(R.id.resolve).setOnClickListener(v -> resolvePermissions());
    return view;
  }

  @Override
  public boolean onBackPressed() {
    return false;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (permissionDisposable != null) {
      permissionDisposable.dispose();
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    activity = null;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case 1003:
        if (permissionChecker != null) {
          permissionChecker.onResult(requestCode, permissions, grantResults);
        }
        break;
    }
  }

  private void resolvePermissions() {
    permissionChecker = new PermissionChecker(1003);
    boolean bool = true;
    for (String permission : PERMISSIONS) {
      bool = bool & shouldShowRequestPermissionRationale(permission);
    }
    boolean showRationaleBefore = bool;
    permissionDisposable = permissionChecker.check(this, activity, PERMISSIONS)
        .doFinally(() -> permissionChecker = null)
        .subscribe(() -> navigate(NAVIGATE_TO_RESOLVED),
            throwable -> {
              // user rejected the permission
              boolean showRationale = true;
              for (String permission : PERMISSIONS) {
                showRationale = showRationale & shouldShowRequestPermissionRationale(permission);
              }
              if (!showRationale && !showRationaleBefore) {
                // user also CHECKED "never ask again"
                // you can either enable some fall back,
                // disable features of your app
                // or open another dialog explaining
                // again the permission and directing to
                // the app setting
                navigate(NAVIGATE_TO_SETTINGS);
              }
            }
        );
  }
}