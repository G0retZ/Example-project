package com.cargopull.executor_driver.presentation.updatemessage;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.interactor.UpdateMessageUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
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
public class UpdateMessageViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private UpdateMessageViewModel viewModel;
  @Mock
  private Observer<ViewState<UpdateMessageViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<UpdateMessageViewActions>> viewStateCaptor;
  @Mock
  private UpdateMessageViewActions viewActions;

  @Mock
  private UpdateMessageUseCase updateMessageUseCase;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(updateMessageUseCase.getUpdateMessages()).thenReturn(Flowable.never());
    viewModel = new UpdateMessageViewModelImpl(updateMessageUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса загрузить сообщения о новой версии приложения.
   */
  @Test
  public void askDataReceiverToSubscribeToUpdateMessages() {
    // Действие:
    viewModel.initializeUpdateMessages();

    // Результат:
    verify(updateMessageUseCase, only()).getUpdateMessages();
  }

  /**
   * Должен просить у юзкейса загрузить сообщения о новой версии приложения, даже если уже подписан.
   */
  @Test
  public void askDataReceiverToSubscribeToUpdateMessagesIfAlreadyAsked() {
    // Действие:
    viewModel.initializeUpdateMessages();
    viewModel.initializeUpdateMessages();
    viewModel.initializeUpdateMessages();

    // Результат:
    verify(updateMessageUseCase, times(3)).getUpdateMessages();
    verifyNoMoreInteractions(updateMessageUseCase);
  }

  /* Тетсируем сообщение. */

  /**
   * Должен показать сообщение о новой версии приложения.
   */
  @Test
  public void showUpdateMessageMessage() {
    // Дано:
    when(updateMessageUseCase.getUpdateMessages()).thenReturn(Flowable.just("Message"));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeUpdateMessages();

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showUpdateMessage("Message");
  }

  /**
   * Не должен показывать пустое сообщение.
   */
  @Test
  public void doNotShowEmptyMessage() {
    // Дано:
    when(updateMessageUseCase.getUpdateMessages()).thenReturn(Flowable.just(""));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeUpdateMessages();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать сообщение из пробелов.
   */
  @Test
  public void doNotShowSpaceMessage() {
    // Дано:
    when(updateMessageUseCase.getUpdateMessages()).thenReturn(Flowable.just("\n"));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeUpdateMessages();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }
}