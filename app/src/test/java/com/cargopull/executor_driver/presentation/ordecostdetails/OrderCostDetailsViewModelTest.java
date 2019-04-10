package com.cargopull.executor_driver.presentation.ordecostdetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.DataReceiver;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.subjects.PublishSubject;
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
public class OrderCostDetailsViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderCostDetailsViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private DataReceiver<OrderCostDetails> orderCostDetailsUseCase;
  @Mock
  private OrderCostDetails orderCostDetails;
  @Mock
  private OrderCostDetails orderCostDetails1;
  @Mock
  private OrderCostDetails orderCostDetails2;

  @Mock
  private Observer<ViewState<OrderCostDetailsViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  private PublishSubject<OrderCostDetails> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(orderCostDetailsUseCase.get()).thenReturn(publishSubject);
    viewModel = new OrderCostDetailsViewModelImpl(errorReporter, orderCostDetailsUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку маппинга.
   */
  @Test
  public void reportDataMappingError() {
    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы при создании.
   */
  @Test
  public void askUseCaseForOrderCostDetailsInitially() {
    // Результат:
    verify(orderCostDetailsUseCase, only()).get();
  }

  /**
   * Не должен трогать юзкейс при подписках
   */
  @Test
  public void doNotouchUseCaseOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(orderCostDetailsUseCase, only()).get();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrderCostDetails = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrderCostDetails.verify(viewStateObserver)
        .onChanged(any(OrderCostDetailsViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
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
    verify(viewStateObserver, only()).onChanged(new OrderCostDetailsViewStatePending(null));
  }

  /**
   * Должен вернуть состояния вида "Бездействие" с полученнымы заказами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(orderCostDetails);
    publishSubject.onNext(orderCostDetails1);
    publishSubject.onNext(orderCostDetails2);

    // Результат:
    inOrder.verify(viewStateObserver)
        .onChanged(new OrderCostDetailsViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostDetailsViewStateIdle(
        new OrderCostDetailsItem(orderCostDetails))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderCostDetailsViewStateIdle(
        new OrderCostDetailsItem(orderCostDetails1))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderCostDetailsViewStateIdle(
        new OrderCostDetailsItem(orderCostDetails2))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}