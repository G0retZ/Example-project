package com.cargopull.executor_driver.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cargopull.executor_driver.backend.analytics.ErrorReporter
import com.cargopull.executor_driver.backend.web.ServerResponseException
import com.cargopull.executor_driver.gateway.DataMappingException
import com.cargopull.executor_driver.interactor.NextExecutorStateUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.EmptyDisposable

/**
 * ViewModel окна перехода к следующему состоянию исполнителя.
 */
interface NextExecutorStateViewModel : ViewModel<FragmentViewActions> {

    /**
     * Запрашивает переход к следующему состоянию исполнителя.
     */
    fun routeToNextState()
}

class NextExecutorStateViewModelImpl(
        private val errorReporter: ErrorReporter,
        private val nextExecutorStateUseCase: NextExecutorStateUseCase) : androidx.lifecycle.ViewModel(), NextExecutorStateViewModel {
    private val viewStateLiveData: MutableLiveData<ViewState<FragmentViewActions>> = MutableLiveData()
    private val navigateLiveData: SingleLiveEvent<String> = SingleLiveEvent()
    private var disposable: Disposable = EmptyDisposable.INSTANCE

    init {
        viewStateLiveData.postValue(NextExecutorStateViewStateIdle())
    }

    override fun getViewStateLiveData(): LiveData<ViewState<FragmentViewActions>> {
        return viewStateLiveData
    }

    override fun getNavigationLiveData(): LiveData<String> {
        return navigateLiveData
    }

    override fun routeToNextState() {
        if (!disposable.isDisposed) {
            return
        }
        viewStateLiveData.postValue(NextExecutorStateViewStatePending())
        disposable = nextExecutorStateUseCase
                .proceedToNextState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { viewStateLiveData.postValue(NextExecutorStateViewStateIdle()) },
                        { throwable ->
                            viewStateLiveData.postValue(NextExecutorStateViewStateIdle())
                            if (throwable is DataMappingException) {
                                errorReporter.reportError(throwable)
                                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR)
                            } else {
                                if (throwable is ServerResponseException) {
                                    errorReporter.reportError(throwable)
                                }
                                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION)
                            }
                        }
                )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}

/**
 * Состояние вида ожидания.
 */
class NextExecutorStateViewStateIdle : ViewState<FragmentViewActions> {

    override fun apply(stateActions: FragmentViewActions) {
        stateActions.unblockWithPending("NextExecutorState")
    }
}

/**
 * Состояние вида ожидания.
 */
class NextExecutorStateViewStatePending : ViewState<FragmentViewActions> {

    override fun apply(stateActions: FragmentViewActions) {
        stateActions.blockWithPending("NextExecutorState")
    }
}
