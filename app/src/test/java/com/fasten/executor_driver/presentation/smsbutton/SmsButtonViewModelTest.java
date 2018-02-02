package com.fasten.executor_driver.presentation.smsbutton;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.interactor.auth.SmsUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
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
public class SmsButtonViewModelTest {

  private SmsButtonViewModel smsButtonViewModel;

  private TestScheduler testScheduler;

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();

  @Mock
  private Observer<ViewState<SmsButtonViewActions>> viewStateObserver;

  @Mock
  private SmsUseCase smsUseCase;

  @Before
  public void setUp() throws Exception {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(smsUseCase.sendMeCode()).thenReturn(Completable.never());
    smsButtonViewModel = new SmsButtonViewModelImpl(smsUseCase);
  }

	/* Тетсируем работу с юзкейсом СМС. */

  /**
   * Не должен просить юзкейс отправить СМС с кодом на номер, если предыдущий запрос еще не
   * завершился.
   *
   * @throws Exception error.
   */
  @Test
  public void DoNotTouchSmsUseCaseToSendMeCodeUntilRequestFinished() throws Exception {
    // Действие:
    smsButtonViewModel.sendMeSms();
    smsButtonViewModel.sendMeSms();
    smsButtonViewModel.sendMeSms();

    // Результат:
    verify(smsUseCase, only()).sendMeCode();
  }

  /**
   * Не должен просить юзкейс отправить СМС с кодом на номер, если предыдущий запрос еще не
   * завершился.
   *
   * @throws Exception error.
   */
  @Test
  public void DoNotTouchSmsUseCaseToSendMeCodeUntilTimeout() throws Exception {
    // Дано:
    when(smsUseCase.sendMeCode()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(smsUseCase);

    // Действие:
    smsButtonViewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    smsButtonViewModel.sendMeSms();
    testScheduler.advanceTimeBy(20, TimeUnit.SECONDS);
    smsButtonViewModel.sendMeSms();

    // Результат:
    inOrder.verify(smsUseCase).sendMeCode();
    inOrder.verifyNoMoreInteractions();
    testScheduler.advanceTimeBy(30, TimeUnit.SECONDS);
    smsButtonViewModel.sendMeSms();
    inOrder.verify(smsUseCase).sendMeCode();
    verifyNoMoreInteractions(smsUseCase);

  }

  /**
   * Должен попросить юзкейс отправить СМС с кодом на номер.
   *
   * @throws Exception error.
   */
  @Test
  public void askSmsUseCaseToSendMeCode() throws Exception {
    // Дано:
    when(smsUseCase.sendMeCode()).thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    smsButtonViewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    smsButtonViewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    smsButtonViewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    verify(smsUseCase, times(3)).sendMeCode();
    verifyNoMoreInteractions(smsUseCase);
  }

	/* Тетсируем переключение состояний */

  /**
   * Должен вернуть рабочее состояние вида.
   *
   * @throws Exception error
   */
  @Test
  public void setReadyViewStateToLiveData() throws Exception {
    // Действие:
    smsButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(SmsButtonViewStateReady.class));
  }

  /**
   * Должен вернуть состояния вида "В процессе" для запроса СМС.
   *
   * @throws Exception error
   */
  @Test
  public void setPendingViewStateToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    smsButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    smsButtonViewModel.sendMeSms();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" с отсчетом всего таймаута и возвратом обратно в
   * состояние готовности после ошибки запроса СМС.
   *
   * @throws Exception error
   */
  @Test
  public void setHoldViewStateToLiveDataAfterFail() throws Exception {
    // Дано:
    when(smsUseCase.sendMeCode()).thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    smsButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    smsButtonViewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new SmsButtonViewStateError(new NoNetworkException()));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(5));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(4));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(3));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(2));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(1));
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" с отсчетом всего таймаута и возвратом обратно в
   * состояние готовности после успешного запроса СМС.
   *
   * @throws Exception error
   */
  @Test
  public void setHoldViewStateToLiveData() throws Exception {
    // Дано:
    when(smsUseCase.sendMeCode()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    smsButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    smsButtonViewModel.sendMeSms();
    testScheduler.advanceTimeBy(30, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(30));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(29));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(28));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(27));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(26));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(25));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(24));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(23));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(22));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(21));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(20));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(19));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(18));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(17));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(16));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(15));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(14));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(13));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(12));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(11));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(10));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(9));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(8));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(7));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(6));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(5));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(4));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(3));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(2));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(1));
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }
}
