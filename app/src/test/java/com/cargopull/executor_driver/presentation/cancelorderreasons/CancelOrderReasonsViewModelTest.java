package com.cargopull.executor_driver.presentation.cancelorderreasons;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.AuthorizationException;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderReasonsViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CancelOrderReasonsViewModel viewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<CancelOrderReasonsViewActions>> viewStateObserver;
  @Mock
  private CancelOrderReasonsUseCase useCase;
  @Mock
  private CancelOrderReason cancelOrderReason;
  @Mock
  private CancelOrderReason cancelOrderReason1;
  @Mock
  private CancelOrderReason cancelOrderReason2;

  private PublishSubject<List<CancelOrderReason>> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(useCase.getCancelOrderReasons())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new CancelOrderReasonsViewModelImpl(useCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить юзкейс получать список причин отказа только при создании.
   */
  @Test
  public void askUseCaseForCancelOrderReasonsInitially() {
    // Результат:
    verify(useCase, only()).getCancelOrderReasons();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(useCase, only()).getCancelOrderReasons();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие и Результат:
    verify(viewStateObserver, only()).onChanged(new CancelOrderReasonsViewStatePending(null));
  }

  /**
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(viewStateObserver, only()).onChanged(new CancelOrderReasonsViewStatePending(null));
  }

  /**
   * Должен вернуть состояние вида "списка причин отказа".
   */
  @Test
  public void setCancelOrderViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Collections.singletonList(cancelOrderReason));
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    publishSubject.onNext(Arrays.asList(cancelOrderReason1, cancelOrderReason, cancelOrderReason2));
    publishSubject.onNext(Arrays.asList(cancelOrderReason2, cancelOrderReason1, cancelOrderReason));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderReasonsViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderReasonsViewState(
        Collections.singletonList(cancelOrderReason)
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderReasonsViewState(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderReasonsViewState(
        Arrays.asList(cancelOrderReason1, cancelOrderReason, cancelOrderReason2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderReasonsViewState(
        Arrays.asList(cancelOrderReason2, cancelOrderReason1, cancelOrderReason)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать данные от сервера.
   */
  @Test
  public void setNothingToLiveDataForNewReasons() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Collections.singletonList(cancelOrderReason));
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    publishSubject.onNext(Arrays.asList(cancelOrderReason1, cancelOrderReason, cancelOrderReason2));
    publishSubject.onNext(Arrays.asList(cancelOrderReason2, cancelOrderReason1, cancelOrderReason));

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForError() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoNetworkException());

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForAuthorize() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new AuthorizationException());

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}