package com.cargopull.executor_driver.presentation.ordershistoryheader;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import com.cargopull.executor_driver.interactor.OrdersHistorySummaryGateway;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import org.joda.time.DateTime;

public class OrdersHistoryHeaderViewModelImpl extends ViewModel implements
    OrdersHistoryHeaderViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private final OrdersHistorySummaryGateway gateway;
  @NonNull
  private final MutableLiveData<ViewState<OrdersHistoryHeaderViewActions>> viewStateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  private final int currentOffset;
  private boolean loaded;

  @Inject
  public OrdersHistoryHeaderViewModelImpl(int currentOffset,
      @NonNull ErrorReporter errorReporter,
      @NonNull TimeUtils timeUtils,
      @NonNull OrdersHistorySummaryGateway gateway) {
    this.currentOffset = currentOffset;
    this.errorReporter = errorReporter;
    this.timeUtils = timeUtils;
    this.gateway = gateway;
    viewStateLiveData = new MutableLiveData<>();
    retry();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrdersHistoryHeaderViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  public void retry() {
    if (loaded) {
      return;
    }
    if (disposable.isDisposed()) {
      viewStateLiveData.postValue(new OrdersHistoryHeaderViewStatePending());
      disposable = Single.just(currentOffset)
          .subscribeOn(Schedulers.single())
          .flatMap(this::convertOffsetToRequest)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              this::minimize,
              throwable -> {
                errorReporter.reportError(throwable);
                viewStateLiveData.postValue(new OrdersHistoryHeaderViewStateError());
              }
          );
    }
  }

  private void maximize(@NonNull OrdersHistorySummary ordersHistorySummary) {
    loaded = true;
    viewStateLiveData.postValue(
        new OrdersHistoryHeaderViewStateMaximized(
            ordersHistorySummary, () -> minimize(ordersHistorySummary)
        )
    );
  }

  private void minimize(@NonNull OrdersHistorySummary ordersHistorySummary) {
    loaded = true;
    viewStateLiveData.postValue(
        new OrdersHistoryHeaderViewStateMinimized(
            ordersHistorySummary, () -> maximize(ordersHistorySummary)
        )
    );
  }

  private Single<OrdersHistorySummary> convertOffsetToRequest(int offset) {
    DateTime dateTime = new DateTime(timeUtils.currentTimeMillis());
    long from = dateTime.minusMonths(offset).withDayOfMonth(1).withMillisOfDay(0).getMillis();
    long to;
    if (offset == 0) {
      to = dateTime.getMillis();
    } else {
      to = dateTime.minusMonths(offset - 1).withDayOfMonth(1).withMillisOfDay(0)
          .minusMillis(1).getMillis();
    }
    return gateway.getOrdersHistorySummary(from, to);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
