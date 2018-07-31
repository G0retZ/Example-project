package com.cargopull.executor_driver.presentation.announcement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnnouncementViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private AnnouncementViewModel viewModel;
  @Mock
  private Observer<ViewState<AnnouncementStateViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<AnnouncementStateViewActions>> viewStateCaptor;
  @Mock
  private AnnouncementStateViewActions viewActions;

  @Before
  public void setUp() {
    viewModel = new AnnouncementViewModelImpl();
  }

  /* Тетсируем объявление. */

  /**
   * Должен показать объявление.
   */
  @Test
  public void showUpdateMessageMessage() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.postMessage("Message");

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showAnnouncementMessage("Message");
  }

  /**
   * Должен показать объявление, затем null после его прочтения.
   */
  @Test
  public void showOnlineMessageThenNull() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.postMessage("Message");
    viewModel.announcementConsumed();

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    assertEquals(2, viewStateCaptor.getAllValues().size());
    assertNull(viewStateCaptor.getAllValues().get(1));
    viewStateCaptor.getAllValues().get(0).apply(viewActions);
    verify(viewActions, only()).showAnnouncementMessage("Message");
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать пустое объявление.
   */
  @Test
  public void doNotShowEmptyMessage() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.postMessage("");

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать объявление из пробелов.
   */
  @Test
  public void doNotShowSpaceMessage() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.postMessage("  ");

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }
}