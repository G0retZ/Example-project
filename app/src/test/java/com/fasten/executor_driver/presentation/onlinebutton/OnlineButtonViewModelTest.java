package com.fasten.executor_driver.presentation.onlinebutton;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.interactor.online.OnlineUseCase;
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
public class OnlineButtonViewModelTest {

  private OnlineButtonViewModel onlineButtonViewModel;

  private TestScheduler testScheduler;

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();

  @Mock
  private Observer<ViewState<OnlineButtonViewActions>> viewStateObserver;

  @Mock
  private OnlineUseCase onlineUseCase;

  @Before
  public void setUp() throws Exception {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(onlineUseCase.goOnline()).thenReturn(Completable.never());
    onlineButtonViewModel = new OnlineButtonViewModelImpl(onlineUseCase);
  }

	/* Тетсируем работу с юзкейсом выхода на линию. */

  /**
   * Не должен просить юзкейс выйти на линию, если предыдущий запрос еще не завершился.
   *
   * @throws Exception error.
   */
  @Test
  public void DoNotTouchOnlineUseCaseUntilRequestFinished() throws Exception {
    // Действие:
    onlineButtonViewModel.goOnline();
    onlineButtonViewModel.goOnline();
    onlineButtonViewModel.goOnline();

    // Результат:
    verify(onlineUseCase, only()).goOnline();
  }

  /**
   * Не должен просить юзкейс выйти на линию, если предыдущий запрос еще не завершился.
   *
   * @throws Exception error.
   */
  @Test
  public void DoNotTouchOnlineUseCaseToGoOnlineUntilTimeout() throws Exception {
    // Дано:
    when(onlineUseCase.goOnline()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(onlineUseCase);

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();

    // Результат:
    inOrder.verify(onlineUseCase).goOnline();
    inOrder.verifyNoMoreInteractions();
    testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();
    inOrder.verify(onlineUseCase).goOnline();
    verifyNoMoreInteractions(onlineUseCase);

  }

  /**
   * Должен попросить юзкейс отправить выйти на линию.
   *
   * @throws Exception error.
   */
  @Test
  public void askOnlineUseCaseToGoOnline() throws Exception {
    // Дано:
    when(onlineUseCase.goOnline()).thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    verify(onlineUseCase, times(3)).goOnline();
    verifyNoMoreInteractions(onlineUseCase);
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
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(OnlineButtonViewStateReady.class));
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" для запроса выхода на линию.
   *
   * @throws Exception error
   */
  @Test
  public void setPendingViewStateToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ожидайте" с возвратом обратно в состояние готовности после
   * ошибки запроса выхода на линию.
   *
   * @throws Exception error
   */
  @Test
  public void setHoldViewStateToLiveDataAfterFail() throws Exception {
    // Дано:
    when(onlineUseCase.goOnline()).thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineButtonViewStateError(new NoNetworkException()));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" с возвратом обратно в состояние готовности после
   * успешного запроса выхода на линию.
   *
   * @throws Exception error
   */
  @Test
  public void setHoldViewStateToLiveData() throws Exception {
    // Дано:
    when(onlineUseCase.goOnline()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(30, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateProceed.class));
    verifyNoMoreInteractions(viewStateObserver);
  }
}