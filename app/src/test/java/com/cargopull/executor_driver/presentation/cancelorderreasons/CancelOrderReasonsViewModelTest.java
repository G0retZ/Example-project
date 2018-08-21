package com.cargopull.executor_driver.presentation.cancelorderreasons;

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
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.CancelOrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import java.util.Collections;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderReasonsViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CancelOrderReasonsViewModel viewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<Runnable>> viewStateObserver;
  @Mock
  private CancelOrderUseCase cancelOrderUseCase;
  @Mock
  private CancelOrderReason cancelOrderReason;

  @Before
  public void setUp() {
    ExecutorState.ONLINE.setData(null);
    when(cancelOrderUseCase.getCancelOrderReasons(anyBoolean())).thenReturn(Flowable.never());
    viewModel = new CancelOrderReasonsViewModelImpl(cancelOrderUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса причины отказа от заказа со сбросом кеша.
   */
  @Test
  public void askDataReceiverToSubscribeToCancelOrderReasonsUpdatesWithCacheReset() {
    // Действие:
    viewModel.initializeCancelOrderReasons();

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
    viewModel.initializeCancelOrderReasons();
    viewModel.initializeCancelOrderReasons();
    viewModel.initializeCancelOrderReasons();

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeCancelOrderReasons();

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
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeCancelOrderReasons();

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
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeCancelOrderReasons();

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
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeCancelOrderReasons();

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
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeCancelOrderReasons();

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}