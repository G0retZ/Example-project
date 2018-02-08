package com.fasten.executor_driver.gateway;

import android.content.Context;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.interactor.map.HeatMapGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import javax.inject.Inject;

public class HeatMapGatewayImpl implements HeatMapGateway {

  private final int[] jsonFiles = new int[]{R.raw.ttk, R.raw.star, R.raw.mkad, R.raw.heart,
      R.raw.key};
  private final Context context;
  private int counter;

  @Inject
  public HeatMapGatewayImpl(Context context) {
    this.context = context;
  }

  @NonNull
  @Override
  public Single<String> getHeatMap() {
    return Single.fromCallable(() -> {
      Writer writer = new StringWriter();
      char[] buffer = new char[1024];
      try (InputStream is = context.getResources().openRawResource(jsonFiles[counter % 5])) {
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
          writer.write(buffer, 0, n);
        }
      }
      counter++;
      return writer.toString();
    }).subscribeOn(Schedulers.io()).observeOn(Schedulers.single());
  }
}
