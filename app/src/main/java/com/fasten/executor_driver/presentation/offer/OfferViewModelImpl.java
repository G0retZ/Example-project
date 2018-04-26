package com.fasten.executor_driver.presentation.offer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOffersAvailableException;
import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.interactor.OfferUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.utils.TimeUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OfferViewModelImpl extends ViewModel implements OfferViewModel {

  @NonNull
  private final OfferUseCase offerUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OfferViewActions>> viewStateLiveData;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private Disposable offersDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable decisionDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private OfferItem offerItem;

  @Inject
  public OfferViewModelImpl(@NonNull OfferUseCase offerUseCase,
      @NonNull TimeUtils timeUtils) {
    this.offerUseCase = offerUseCase;
    this.timeUtils = timeUtils;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OfferViewStatePending(offerItem));
  }

  @NonNull
  @Override
  public LiveData<ViewState<OfferViewActions>> getViewStateLiveData() {
    loadOffers();
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }


  private void loadOffers() {
    if (offersDisposable.isDisposed()) {
      offersDisposable = offerUseCase.getOffers()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeOffer, this::consumeError);
    }
  }


  private void consumeOffer(@NonNull Offer offer) {
    offerItem = new OfferItem(offer, timeUtils);
    viewStateLiveData.postValue(new OfferViewStateIdle(offerItem));
  }

  private void consumeError(Throwable throwable) {
    if (throwable instanceof NoOffersAvailableException) {
      viewStateLiveData.postValue(new OfferViewStateUnavailableError(offerItem));
    } else {
      viewStateLiveData.postValue(new OfferViewStateNetworkError(offerItem));
    }
  }

  @Override
  public void acceptOffer() {
    if (!decisionDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OfferViewStatePending(offerItem));
    decisionDisposable = offerUseCase.sendDecision(true)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, this::consumeError
        );
  }

  @Override
  public void declineOffer() {
    if (!decisionDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OfferViewStatePending(offerItem));
    decisionDisposable = offerUseCase.sendDecision(false)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, this::consumeError
        );
  }

  @Override
  public void counterTimeOut() {
    if (!(viewStateLiveData.getValue() instanceof OfferViewStatePending)) {
      viewStateLiveData.postValue(new OfferViewStatePending(offerItem));
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    offerItem = null;
    if (!offersDisposable.isDisposed()) {
      offersDisposable.dispose();
    }
    if (!decisionDisposable.isDisposed()) {
      decisionDisposable.dispose();
    }
  }
}
