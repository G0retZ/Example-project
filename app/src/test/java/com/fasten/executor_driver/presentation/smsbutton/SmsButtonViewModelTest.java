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
import io.reactivex.subjects.CompletableSubject;
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

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private SmsButtonViewModel viewModel;
  private TestScheduler testScheduler;
  @Mock
  private Observer<ViewState<SmsButtonViewActions>> viewStateObserver;

  @Mock
  private SmsUseCase smsUseCase;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(smsUseCase.sendMeCode()).thenReturn(Completable.never());
    viewModel = new SmsButtonViewModelImpl(smsUseCase);
  }

  /* Тетсируем работу с юзкейсом СМС. */

  /**
   * Не должен просить юзкейс отправить СМС с кодом на номер, если предыдущий запрос еще не
   * завершился.
   */
  @Test
  public void DoNotTouchSmsUseCaseToSendMeCodeUntilRequestFinished() {
    // Действие:
    viewModel.sendMeSms();
    viewModel.sendMeSms();
    viewModel.sendMeSms();

    // Результат:
    verify(smsUseCase, only()).sendMeCode();
  }

  /**
   * Не должен просить юзкейс отправить СМС с кодом на номер, если таймер еще не вышел.
   */
  @Test
  public void DoNotTouchSmsUseCaseToSendMeCodeUntilTimeout() {
    // Дано:
    when(smsUseCase.sendMeCode()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(smsUseCase);

    // Действие:
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(20, TimeUnit.SECONDS);
    viewModel.sendMeSms();

    // Результат:
    inOrder.verify(smsUseCase).sendMeCode();
    inOrder.verifyNoMoreInteractions();
    testScheduler.advanceTimeBy(30, TimeUnit.SECONDS);
    viewModel.sendMeSms();
    inOrder.verify(smsUseCase).sendMeCode();
    verifyNoMoreInteractions(smsUseCase);

  }

  /**
   * Должен попросить юзкейс отправить СМС с кодом на номер.
   */
  @Test
  public void askSmsUseCaseToSendMeCode() {
    // Дано:
    when(smsUseCase.sendMeCode()).thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    verify(smsUseCase, times(3)).sendMeCode();
    verifyNoMoreInteractions(smsUseCase);
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть рабочее состояние вида.
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(SmsButtonViewStateReady.class));
  }

  /**
   * Должен вернуть состояния вида "В процессе" для запроса СМС.
   */
  @Test
  public void setPendingViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.sendMeSms();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Ошибка" без отсчета таймаута и после ошибки запроса СМС, если
   * нет сети.
   */
  @Test
  public void setErrorViewStateToLiveDataAfterFail() {
    // Дано:
    CompletableSubject completableSubject = CompletableSubject.create();
    when(smsUseCase.sendMeCode()).thenReturn(completableSubject);
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    completableSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" с отсчетом всего таймаута и возвратом обратно в
   * состояние готовности после ошибки запроса СМС, не связанного с состоянием сети.
   */
  @Test
  public void setHoldViewStateToLiveDataAfterOtherError() {
    // Дано:
    CompletableSubject completableSubject = CompletableSubject.create();
    when(smsUseCase.sendMeCode()).thenReturn(completableSubject);
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
    completableSubject.onError(new IllegalArgumentException());
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

  /**
   * Должен вернуть состояния вида "Ожидайте" с отсчетом всего таймаута и возвратом обратно в
   * состояние готовности после успешного запроса СМС.
   */
  @Test
  public void setHoldViewStateToLiveData() {
    // Дано:
    CompletableSubject completableSubject = CompletableSubject.create();
    when(smsUseCase.sendMeCode()).thenReturn(completableSubject);
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
    completableSubject.onComplete();
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
