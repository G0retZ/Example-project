package com.cargopull.executor_driver.presentation.smsbutton;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.interactor.auth.CodeUseCase;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.CompletableSubject;
import java.util.concurrent.TimeUnit;
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

@RunWith(MockitoJUnitRunner.class)
public class SmsButtonViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private SmsButtonViewModel viewModel;
  private TestScheduler testScheduler;
  @Mock
  private Observer<ViewState<FragmentViewActions>> viewStateObserver;

  @Mock
  private CodeUseCase codeUseCase;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    when(codeUseCase.sendMeCode()).thenReturn(Completable.never());
    viewModel = new SmsButtonViewModelImpl(codeUseCase);
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
    verify(codeUseCase, only()).sendMeCode();
  }

  /**
   * Не должен просить юзкейс отправить СМС с кодом на номер, если таймер еще не вышел.
   */
  @Test
  public void DoNotTouchSmsUseCaseToSendMeCodeUntilTimeout() {
    // Дано:
    when(codeUseCase.sendMeCode()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(codeUseCase);

    // Действие:
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(20, TimeUnit.SECONDS);
    viewModel.sendMeSms();

    // Результат:
    inOrder.verify(codeUseCase).sendMeCode();
    inOrder.verifyNoMoreInteractions();
    testScheduler.advanceTimeBy(30, TimeUnit.SECONDS);
    viewModel.sendMeSms();
    inOrder.verify(codeUseCase).sendMeCode();
    verifyNoMoreInteractions(codeUseCase);

  }

  /**
   * Должен попросить юзкейс отправить СМС с кодом на номер.
   */
  @Test
  public void askSmsUseCaseToSendMeCode() {
    // Дано:
    when(codeUseCase.sendMeCode()).thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.sendMeSms();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    verify(codeUseCase, times(3)).sendMeCode();
    verifyNoMoreInteractions(codeUseCase);
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
    when(codeUseCase.sendMeCode()).thenReturn(completableSubject);
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
    when(codeUseCase.sendMeCode()).thenReturn(completableSubject);
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
    when(codeUseCase.sendMeCode()).thenReturn(completableSubject);
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
