package com.cargopull.executor_driver.presentation.upcomingpreorder;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.interactor.UpcomingPreOrderMessagesUseCase;
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
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingPreOrderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private UpcomingPreOrderViewModel viewModel;
  @Mock
  private UpcomingPreOrderMessagesUseCase useCase;
  @Mock
  private Observer<ViewState<UpcomingPreOrderViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<UpcomingPreOrderViewActions>> viewStateCaptor;
  @Mock
  private UpcomingPreOrderViewActions viewActions;

  private PublishSubject<String> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(useCase.getUpcomingPreOrderMessages())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new UpcomingPreOrderViewModelImpl(useCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса загрузить сообщения о предстоящих предзаказах только при создании.
   */
  @Test
  public void askDataReceiverToSubscribeToUpcomingPreOrdersMessages() {
    // Результат:
    verify(useCase, only()).getUpcomingPreOrderMessages();
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
    verify(useCase, only()).getUpcomingPreOrderMessages();
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
    verify(viewActions, only()).showUpcomingPreOrderMessage("Message");
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
    verifyZeroInteractions(viewStateObserver);
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
    verifyZeroInteractions(viewStateObserver);
  }
}