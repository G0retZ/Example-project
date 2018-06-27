package com.fasten.executor_driver.presentation.balance;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.entity.ExecutorBalance;
import com.fasten.executor_driver.interactor.ExecutorBalanceUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BalanceViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private BalanceViewModel viewModel;
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
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(useCase.getExecutorBalance(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new BalanceViewModelImpl(useCase);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать список причин отказа только при создании.
   */
  @Test
  public void askUseCaseForExecutorBalancesInitially() {
    // Результат:
    verify(useCase, only()).getExecutorBalance(false);
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(useCase, only()).getExecutorBalance(false);
  }

  /* Тетсируем переключение состояний от сервера. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new BalanceViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForMappingError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(viewStateObserver, only()).onChanged(new BalanceViewStatePending(null));
  }

  /**
   * Должен вернуть состояние вида "списка причин отказа".
   */
  @Test
  public void setBalanceViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(executorBalance);
    publishSubject.onNext(executorBalance1);
    publishSubject.onNext(executorBalance2);

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(executorBalance);
    viewModel.replenishAccount();

    // Результат:
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
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    // Действие:
    publishSubject.onNext(executorBalance);
    publishSubject.onNext(executorBalance1);
    publishSubject.onNext(executorBalance2);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к пополнению счета".
   */
  @Test
  public void setNavigateToReplenishAccountToLiveData() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.replenishAccount();

    // Результат:
    verify(navigateObserver, only()).onChanged(BalanceNavigate.PAYMENT_OPTIONS);
  }

  /**
   * Должен вернуть ошибку данных сервера.
   */
  @Test
  public void setNavigateToServerDataError() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(navigateObserver, only()).onChanged(BalanceNavigate.SERVER_DATA_ERROR);
  }
}