package com.fasten.executor_driver.presentation.cancelorderreasons;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.accounts.AuthenticatorException;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.CancelOrderReason;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.CancelOrderUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderReasonsViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CancelOrderReasonsViewModel cancelOrderReasonsViewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<CancelOrderReasonsViewActions>> viewStateObserver;
  @Mock
  private CancelOrderUseCase cancelOrderUseCase;
  @Mock
  private CancelOrderReason cancelOrderReason;

  @Before
  public void setUp() {
    ExecutorState.ONLINE.setData(null);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(cancelOrderUseCase.getCancelOrderReasons(anyBoolean())).thenReturn(Flowable.never());
    cancelOrderReasonsViewModel = new CancelOrderReasonsViewModelImpl(cancelOrderUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса причины отказа от заказа со сбросом кеша.
   */
  @Test
  public void askDataReceiverToSubscribeToCancelOrderReasonsUpdatesWithCacheReset() {
    // Действие:
    cancelOrderReasonsViewModel.initializeCancelOrderReasons();

    // Результат:
    verify(cancelOrderUseCase, only()).getCancelOrderReasons(true);
  }

  /**
   * Должен просить у юзкейса загрузить причины отказа от заказа, со сбросом кеша, даже если уже
   * подписан.
   */
  @Test
  public void doNotTouchUseCaseBeforeFirstRequestComplete() {
    // Действие:
    cancelOrderReasonsViewModel.initializeCancelOrderReasons();
    cancelOrderReasonsViewModel.initializeCancelOrderReasons();
    cancelOrderReasonsViewModel.initializeCancelOrderReasons();

    // Результат:
    verify(cancelOrderUseCase, times(3)).getCancelOrderReasons(true);
    verifyNoMoreInteractions(cancelOrderUseCase);
  }

  /* Тетсируем сообщение. */

  /**
   * Не должен ничего показывать.
   */
  @Test
  public void showNothing() {
    // Дано:
    when(cancelOrderUseCase.getCancelOrderReasons(anyBoolean()))
        .thenReturn(Flowable.just(Collections.singletonList(cancelOrderReason)));

    // Действие:
    cancelOrderReasonsViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    cancelOrderReasonsViewModel.initializeCancelOrderReasons();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForError() {
    // Дано:
    when(cancelOrderUseCase.getCancelOrderReasons(anyBoolean()))
        .thenReturn(Flowable.error(NoNetworkException::new));

    // Действие:
    cancelOrderReasonsViewModel.getNavigationLiveData().observeForever(navigationObserver);
    cancelOrderReasonsViewModel.initializeCancelOrderReasons();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForAuthorize() {
    // Дано:
    when(cancelOrderUseCase.getCancelOrderReasons(anyBoolean()))
        .thenReturn(Flowable.error(AuthenticatorException::new));

    // Действие:
    cancelOrderReasonsViewModel.getNavigationLiveData().observeForever(navigationObserver);
    cancelOrderReasonsViewModel.initializeCancelOrderReasons();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForData() {
    // Дано:
    when(cancelOrderUseCase.getCancelOrderReasons(anyBoolean()))
        .thenReturn(Flowable.just(Collections.singletonList(cancelOrderReason)));

    // Действие:
    cancelOrderReasonsViewModel.getNavigationLiveData().observeForever(navigationObserver);
    cancelOrderReasonsViewModel.initializeCancelOrderReasons();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Должен вернуть ошибку данных сервера.
   */
  @Test
  public void navigateToServerDataError() {
    // Дано:
    when(cancelOrderUseCase.getCancelOrderReasons(anyBoolean()))
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    cancelOrderReasonsViewModel.getNavigationLiveData().observeForever(navigationObserver);
    cancelOrderReasonsViewModel.initializeCancelOrderReasons();

    // Результат:
    verify(navigationObserver, only()).onChanged(CancelOrderReasonsNavigate.SERVER_DATA_ERROR);
  }
}