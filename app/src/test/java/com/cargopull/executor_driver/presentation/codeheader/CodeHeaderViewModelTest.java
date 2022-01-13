package com.cargopull.executor_driver.presentation.codeheader;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.interactor.DataReceiver;
import com.cargopull.executor_driver.presentation.TextViewActions;
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

import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class CodeHeaderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CodeHeaderViewModel viewModel;
  @Mock
  private DataReceiver<String> loginReceiver;

  @Mock
  private Observer<ViewState<TextViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    when(loginReceiver.get()).thenReturn(PublishSubject.never());
    viewModel = new CodeHeaderViewModelImpl(loginReceiver);
  }

  /* Тетсируем работу с публикатором номера телефона. */


  /**
   * Должен просить юзкейс получить список ТС, при первой и только при первой подписке.
   */
  @Test
  public void askSelectedDataSharerForLoginInitially() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();

    // Effect:
    verify(loginReceiver, only()).get();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида с именем.
   */
  @Test
  public void setViewStateWithNumbersToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    PublishSubject<String> publishSubject = PublishSubject.create();
    when(loginReceiver.get()).thenReturn(publishSubject);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext("79997004450");
    publishSubject.onNext("79998887766");
    publishSubject.onNext("79876543210");
    publishSubject.onNext("70123456789");

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+7 (999) 700-44-50"));
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+7 (999) 888-77-66"));
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+7 (987) 654-32-10"));
    inOrder.verify(viewStateObserver).onChanged(new CodeHeaderViewState("+7 (012) 345-67-89"));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен игнорировать ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void ignoreErrors() {
    // Given:
    PublishSubject<String> publishSubject = PublishSubject.create();
    when(loginReceiver.get()).thenReturn(publishSubject, PublishSubject.never());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onError(new IllegalArgumentException());

    // Effect:
    verifyNoInteractions(viewStateObserver);
  }
}