package com.cargopull.executor_driver.presentation.reportproblem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.Problem;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ReportProblemUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class ReportProblemViewModelImpl extends ViewModel implements ReportProblemViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ReportProblemUseCase useCase;
  @NonNull
  private final MutableLiveData<ViewState<ReportProblemViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable loadDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable reportDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<ReportProblemViewActions> lastViewState;

  @Inject
  public ReportProblemViewModelImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull ReportProblemUseCase useCase) {
    this.errorReporter = errorReporter;
    this.useCase = useCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadProblems();
  }

  @NonNull
  @Override
  public LiveData<ViewState<ReportProblemViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void selectItem(Problem problem) {
    if (!reportDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new ReportProblemViewStatePending(lastViewState));
    reportDisposable = useCase.reportProblem(problem)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              viewStateLiveData.postValue(lastViewState);
              navigateLiveData.postValue(ReportProblemNavigate.ORDER_CANCELED);
            },
            throwable -> {
              errorReporter.reportError(throwable);
              viewStateLiveData.postValue(lastViewState);
              if (throwable instanceof IllegalStateException) {
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              }
            }
        );
  }

  private void loadProblems() {
    loadDisposable.dispose();
    viewStateLiveData.postValue(new ReportProblemViewStatePending(lastViewState));
    loadDisposable = useCase.getAvailableProblems()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            cancelOrderReasons -> viewStateLiveData.postValue(
                lastViewState = new ReportProblemViewState(cancelOrderReasons)
            ),
            throwable -> {
              errorReporter.reportError(throwable);
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              }
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    loadDisposable.dispose();
    reportDisposable.dispose();
  }
}
