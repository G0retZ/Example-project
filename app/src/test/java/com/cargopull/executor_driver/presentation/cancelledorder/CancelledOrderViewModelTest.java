package com.cargopull.executor_driver.presentation.cancelledorder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.NotificationMessageUseCase;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class CancelledOrderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CancelledOrderViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private NotificationMessageUseCase useCase;
  @Mock
  private ShakeItPlayer shakeItPlayer;
  @Mock
  private RingTonePlayer ringTonePlayer;
  @Mock
  private Observer<ViewState<CancelledOrderViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<CancelledOrderViewActions>> viewStateCaptor;
  @Mock
  private CancelledOrderViewActions viewActions;

  private PublishSubject<String> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(useCase.getNotificationMessages())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new CancelledOrderViewModelImpl(errorReporter, useCase, shakeItPlayer,
        ringTonePlayer);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса загрузить сообщения о предстоящих предзаказах только при создании.
   */
  @Test
  public void askDataReceiverToSubscribeToUpcomingPreOrdersMessages() {
    // Результат:
    verify(useCase, only()).getNotificationMessages();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void askDataReceiverToSubscribeToUpcomingPreOrdersMessagesIfAlreadyAsked() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(useCase, only()).getNotificationMessages();
  }

  /* Тетсируем работу с вибро и звуком. */

  /**
   * Не должен трогать вибро и звук изначально и на подписках.
   */
  @Test
  public void doNotTouchVibrationAndSoundInitially() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Не должен трогать вибро и звук при ошибке.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnError() {
    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Должен дать вибро и звук отказа при получении отказа.
   */
  @Test
  public void useVibrationAndSoundOnOrderCanceledWithMessage() {
    // Действие:
    publishSubject.onNext("Message");

    // Результат:
    verify(shakeItPlayer, only()).shakeIt(R.raw.skip_order_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.skip_order);
  }

  /**
   * Должен дать вибро и звук отказа при получении отказа.
   */
  @Test
  public void useVibrationAndSoundOnOrderCanceledWithEmptyMessage() {
    // Действие:
    publishSubject.onNext("");

    // Результат:
    verify(shakeItPlayer, only()).shakeIt(R.raw.skip_order_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.skip_order);
  }

  /**
   * Должен дать вибро и звук отказа при получении отказа.
   */
  @Test
  public void useVibrationAndSoundOnOrderCanceledWithSpaceMessage() {
    // Действие:
    publishSubject.onNext("\n");

    // Результат:
    verify(shakeItPlayer, only()).shakeIt(R.raw.skip_order_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.skip_order);
  }

  /* Тетсируем сообщение. */

  /**
   * Должен показать сообщение о предстоящем предзаказе.
   */
  @Test
  public void showUpcomingPreOrderMessage() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext("Message");

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showCancelledOrderMessage("Message");
  }

  /**
   * Не должен показывать сообщение при ошибке.
   */
  @Test
  public void doNotShowMessageOnError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verifyNoInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать пустое сообщение.
   */
  @Test
  public void doNotShowEmptyMessage() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext("");

    // Результат:
    verifyNoInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать сообщение из пробелов.
   */
  @Test
  public void doNotShowSpaceMessage() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext("\n");

    // Результат:
    verifyNoInteractions(viewStateObserver);
  }
}