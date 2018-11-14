package com.cargopull.executor_driver.presentation.announcement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.presentation.ViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
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
  private Observer<ViewState<ViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<ViewActions>> viewStateCaptor;
  @Mock
  private ViewActions viewActions;
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
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext("Message");

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showPersistentDialog(eq("Message"), runnableCaptor.capture());
  }

  /**
   * Должен показать объявление, затем null после его прочтения.
   */
  @Test
  public void showOnlineMessageThenNull() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    publishSubject.onNext("Message");
    inOrder.verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    assertEquals(1, viewStateCaptor.getAllValues().size());
    viewStateCaptor.getAllValues().get(0).apply(viewActions);
    verify(viewActions, only()).showPersistentDialog(eq("Message"), runnableCaptor.capture());

    // Действие:
    runnableCaptor.getValue().run();

    // Результат:
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
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext("");

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать объявление из пробелов.
   */
  @Test
  public void doNotShowSpaceMessage() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext("  ");

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }
}