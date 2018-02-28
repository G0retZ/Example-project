package com.fasten.executor_driver.presentation.codeHeader;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.interactor.DataSharer;
import com.fasten.executor_driver.presentation.ViewState;
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
public class CodeHeaderViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CodeHeaderViewModel codeHeaderViewModel;
  @Mock
  private DataSharer<String> loginSharer;

  private PublishSubject<String> publishSubject;

  @Mock
  private Observer<ViewState<CodeHeaderViewActions>> viewStateObserver;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    publishSubject = PublishSubject.create();
    when(loginSharer.get()).thenReturn(publishSubject);
    codeHeaderViewModel = new CodeHeaderViewModelImpl(loginSharer);
  }

  /* Тетсируем работу с публикатором номера телефона. */


  /**
   * Должен просить юзкейс получить список ТС, при первой и только при первой подписке.
   *
   * @throws Exception error
   */
  @Test
  public void askSelectedDataSharerForLoginInitially() throws Exception {
    // Действие:
    codeHeaderViewModel.getViewStateLiveData();
    codeHeaderViewModel.getViewStateLiveData();
    codeHeaderViewModel.getViewStateLiveData();

    // Результат:
    verify(loginSharer, only()).get();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида без номера изначально.
   *
   * @throws Exception error
   */
  @Test
  public void setViewStateWithZerosToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    codeHeaderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+0 (000) 000-00-00"));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида с именем.
   *
   * @throws Exception error
   */
  @Test
  public void setViewStateWithNumbersToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    codeHeaderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext("79997004450");
    publishSubject.onNext("79998887766");
    publishSubject.onNext("79876543210");
    publishSubject.onNext("70123456789");

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+0 (000) 000-00-00"));
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+7 (999) 700-44-50"));
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+7 (999) 888-77-66"));
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+7 (987) 654-32-10"));
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+7 (012) 345-67-89"));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен игнорировать ошибки.
   *
   * @throws Exception error
   */
  @Test
  public void ignoreErrors() throws Exception {
    // Дано:
    codeHeaderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new IllegalArgumentException());

    // Результат:
    verify(viewStateObserver, only()).onChanged(new CodeHeaderViewState("+0 (000) 000-00-00"));
  }
}