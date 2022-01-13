package com.cargopull.executor_driver.presentation.announcement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.presentation.DialogViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class AnnouncementViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private AnnouncementViewModel viewModel;
  @Mock
  private CommonGateway<String> gateway;
  @Mock
  private Observer<ViewState<DialogViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<DialogViewActions>> viewStateCaptor;
  @Mock
  private DialogViewActions viewActions;
  @Captor
  private ArgumentCaptor<Runnable> runnableCaptor;
  private PublishSubject<String> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(gateway.getData()).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new AnnouncementViewModelImpl(gateway);
  }

  /* Тетсируем объявление. */

  /**
   * Должен показать объявление.
   */
  @Test
  public void showUpdateMessageMessage() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext("Message");

    // Effect:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showPersistentDialog(eq("Message"), runnableCaptor.capture());
  }

  /**
   * Должен показать объявление, затем null после его прочтения.
   */
  @Test
  public void showOnlineMessageThenNull() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    publishSubject.onNext("Message");
    inOrder.verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    assertEquals(1, viewStateCaptor.getAllValues().size());
    viewStateCaptor.getAllValues().get(0).apply(viewActions);
    verify(viewActions, only()).showPersistentDialog(eq("Message"), runnableCaptor.capture());

    // Action:
    runnableCaptor.getValue().run();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    assertEquals(2, viewStateCaptor.getAllValues().size());
    assertNull(viewStateCaptor.getAllValues().get(1));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать пустое объявление.
   */
  @Test
  public void doNotShowEmptyMessage() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext("");

    // Effect:
    verifyNoInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать объявление из пробелов.
   */
  @Test
  public void doNotShowSpaceMessage() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext("  ");

    // Effect:
    verifyNoInteractions(viewStateObserver);
  }
}