package com.fasten.executor_driver.presentation.timeoutbutton;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class TimeoutButtonViewModelImpl extends ViewModel implements TimeoutButtonViewModel {

	private final int duration;
	private Disposable disposable;

	@NonNull
	private final MutableLiveData<ViewState<TimeoutButtonViewActions>> viewStateLiveData;

	TimeoutButtonViewModelImpl(int duration) {
		this.duration = duration;
		viewStateLiveData = new MutableLiveData<>();
		viewStateLiveData.postValue(new TimeoutButtonViewStateReady());
	}

	@NonNull
	@Override
	public LiveData<ViewState<TimeoutButtonViewActions>> getViewStateLiveData() {
		return viewStateLiveData;
	}

	@Override
	public boolean buttonClicked() {
		if (disposable != null && !disposable.isDisposed()) return false;
		disposable = Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
				.take(duration)
				.map(count -> duration - count)
				.subscribe(
						count -> viewStateLiveData.postValue(new TimeoutButtonViewStateHold(count)),
						throwable -> {},
						() -> viewStateLiveData.postValue(new TimeoutButtonViewStateReady())
				);
		return true;
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		if (disposable != null) {
			disposable.dispose();
		}
	}
}
