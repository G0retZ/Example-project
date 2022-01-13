package com.cargopull.executor_driver.presentation.balance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorBalanceUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class BalanceViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private BalanceViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ExecutorBalanceUseCase useCase;
  @Mock
  private ExecutorBalance executorBalance;
  @Mock
  private ExecutorBalance executorBalance1;
  @Mock
  private ExecutorBalance executorBalance2;
  @Mock
  private Observer<String> navigateObserver;

  private PublishSubject<ExecutorBalance> publishSubject;

  @Mock
  private Observer<ViewState<BalanceViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(useCase.getExecutorBalance())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new BalanceViewModelImpl(errorReporter, useCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить юзкейс получать список причин отказа только при создании.
   */
  @Test
  public void askUseCaseForExecutorBalancesInitially() {
    // Effect:
    verify(useCase, only()).getExecutorBalance();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(useCase, only()).getExecutorBalance();
  }

  /* Тетсируем переключение состояний от сервера. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new BalanceViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onError(new Exception());

    // Effect:
    verify(viewStateObserver, only()).onChanged(new BalanceViewStatePending(null));
  }

  /**
   * Должен вернуть состояние вида "списка причин отказа".
   */
  @Test
  public void setBalanceViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(executorBalance);
    publishSubject.onNext(executorBalance1);
    publishSubject.onNext(executorBalance2);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new BalanceViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new BalanceViewState(executorBalance));
    inOrder.verify(viewStateObserver).onChanged(new BalanceViewState(executorBalance1));
    inOrder.verify(viewStateObserver).onChanged(new BalanceViewState(executorBalance2));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем переключение состояний при пополнении счета. */

  /**
   * Не должен давать иных состояний вида.
   */
  @Test
  public void setPendingViewStateStateToLiveDataForReplenishAccount() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(executorBalance);
    viewModel.replenishAccount();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new BalanceViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new BalanceViewState(executorBalance));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать данные от сервера.
   */
  @Test
  public void setNothingToLiveDataForBalanceUpdates() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    // Action:
    publishSubject.onNext(executorBalance);
    publishSubject.onNext(executorBalance1);
    publishSubject.onNext(executorBalance2);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к пополнению счета".
   */
  @Test
  public void setNavigateToReplenishAccountToLiveData() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.replenishAccount();

    // Effect:
    verify(navigateObserver, only()).onChanged(BalanceNavigate.PAYMENT_OPTIONS);
  }

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}