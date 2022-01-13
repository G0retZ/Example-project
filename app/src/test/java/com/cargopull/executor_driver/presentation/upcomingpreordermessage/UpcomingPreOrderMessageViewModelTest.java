package com.cargopull.executor_driver.presentation.upcomingpreordermessage;

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
public class UpcomingPreOrderMessageViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private UpcomingPreOrderMessageViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private NotificationMessageUseCase useCase;
  @Mock
  private ShakeItPlayer shakeItPlayer;
  @Mock
  private RingTonePlayer ringTonePlayer;
  @Mock
  private Observer<ViewState<UpcomingPreOrderMessageViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<UpcomingPreOrderMessageViewActions>> viewStateCaptor;
  @Mock
  private UpcomingPreOrderMessageViewActions viewActions;

  private PublishSubject<String> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(useCase.getNotificationMessages())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new UpcomingPreOrderMessageViewModelImpl(errorReporter, useCase, shakeItPlayer,
        ringTonePlayer);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса загрузить сообщения о предстоящих предзаказах только при создании.
   */
  @Test
  public void askDataReceiverToSubscribeToUpcomingPreOrdersMessages() {
    // Effect:
    verify(useCase, only()).getNotificationMessages();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void askDataReceiverToSubscribeToUpcomingPreOrdersMessagesIfAlreadyAsked() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(useCase, only()).getNotificationMessages();
  }

  /* Тетсируем работу с вибро и звуком. */

  /**
   * Не должен трогать вибро и звук изначально и на подписках.
   */
  @Test
  public void doNotTouchVibrationAndSoundInitially() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Не должен трогать вибро и звук при ошибке.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnError() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Должен дать вибро и звук отказа при получении предстоящего предзаказа.
   */
  @Test
  public void useVibrationAndSoundOnUpcomingPreOrderMessage() {
    // Action:
    publishSubject.onNext("Message");

    // Effect:
    verify(shakeItPlayer, only()).shakeIt(R.raw.pre_order_reminder_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.pre_order_reminder);
  }

  /**
   * Должен дать вибро и звук отказа при получении предстоящего предзаказаа.
   */
  @Test
  public void useVibrationAndSoundOnUpcomingPreOrderEmptyMessage() {
    // Action:
    publishSubject.onNext("");

    // Effect:
    verify(shakeItPlayer, only()).shakeIt(R.raw.pre_order_reminder_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.pre_order_reminder);
  }

  /**
   * Должен дать вибро и звук отказа при получении предстоящего предзаказа.
   */
  @Test
  public void useVibrationAndSoundOnUpcomingPreOrderSpaceMessage() {
    // Action:
    publishSubject.onNext("\n");

    // Effect:
    verify(shakeItPlayer, only()).shakeIt(R.raw.pre_order_reminder_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.pre_order_reminder);
  }

  /* Тетсируем сообщение. */

  /**
   * Должен показать сообщение о предстоящем предзаказе.
   */
  @Test
  public void showUpcomingPreOrderMessage() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext("Message");

    // Effect:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showUpcomingPreOrderMessage("Message");
  }

  /**
   * Не должен показывать сообщение при ошибке.
   */
  @Test
  public void doNotShowMessageOnError() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verifyNoInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать пустое сообщение.
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
   * Не должен показывать сообщение из пробелов.
   */
  @Test
  public void doNotShowSpaceMessage() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext("\n");

    // Effect:
    verifyNoInteractions(viewStateObserver);
  }
}