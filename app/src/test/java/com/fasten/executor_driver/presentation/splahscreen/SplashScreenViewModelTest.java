package com.fasten.executor_driver.presentation.splahscreen;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.CompletableSubject;
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
public class SplashScreenViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private SplashScreenViewModel mapViewModel;
  @Mock
  private Observer<ViewState<SplashScreenViewActions>> viewStateObserver;

  @Mock
  private ExecutorStateUseCase executorStateUseCase;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(executorStateUseCase.loadStatus()).thenReturn(Completable.never());
    mapViewModel = new SplashScreenViewModelImpl(executorStateUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса загрузить статус исполнителя.
   *
   * @throws Exception error
   */
  @Test
  public void askDataReceiverToSubscribeToLocationUpdates() throws Exception {
    // Действие:
    mapViewModel.getViewStateLiveData();

    // Результат:
    verify(executorStateUseCase, only()).loadStatus();
  }

  /**
   * Не должен просить у юзкейса загрузить статус исполнителя (после поворотов), если запрос уже
   * выполняется.
   *
   * @throws Exception error
   */
  @Test
  public void DoNotTouchDataReceiverAfterFirstSubscription() throws Exception {
    // Действие:
    mapViewModel.getViewStateLiveData();
    mapViewModel.getViewStateLiveData();
    mapViewModel.getViewStateLiveData();

    // Результат:
    verify(executorStateUseCase, only()).loadStatus();
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояние загрузки изначально в случае успеха.
   *
   * @throws Exception error
   */
  @Test
  public void setPendingViewInitially() throws Exception {
    // Действие:
    mapViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(SplashScreenViewStatePending.class));
  }

  /**
   * Должен вернуть состояние готово в случае успеха.
   *
   * @throws Exception error
   */
  @Test
  public void setDoneViewStateForSuccess() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    CompletableSubject completableSubject = CompletableSubject.create();
    when(executorStateUseCase.loadStatus()).thenReturn(completableSubject);

    // Действие:
    mapViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    completableSubject.onComplete();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(SplashScreenViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(SplashScreenViewStateDone.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ошибки сети в отсутствия сети.
   *
   * @throws Exception error
   */
  @Test
  public void setErrorViewStateForSuccess() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    CompletableSubject completableSubject = CompletableSubject.create();
    when(executorStateUseCase.loadStatus()).thenReturn(completableSubject);

    // Действие:
    mapViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    completableSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(SplashScreenViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(SplashScreenViewStateNetworkError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен кинуть исключение в случае неуспеха.
   *
   * @throws Exception error
   */
  @Test(expected = RuntimeException.class)
  public void throwExceptionForError() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(executorStateUseCase.loadStatus())
        .thenReturn(Completable.error(DataMappingException::new));

    // Действие:
    mapViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver, only()).onChanged(any(SplashScreenViewStatePending.class));
  }
}