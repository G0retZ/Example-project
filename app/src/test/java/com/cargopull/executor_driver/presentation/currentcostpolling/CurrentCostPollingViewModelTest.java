package com.cargopull.executor_driver.presentation.currentcostpolling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.CurrentCostPollingUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.subjects.CompletableSubject;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCostPollingViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CurrentCostPollingViewModel currentCostPollingViewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private CurrentCostPollingUseCase useCase;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<Runnable>> viewStateObserver;

  private CompletableSubject completableSubject;

  @Before
  public void setUp() {
    completableSubject = CompletableSubject.create();
    when(useCase.listenForPolling()).thenReturn(completableSubject);
    currentCostPollingViewModel = new CurrentCostPollingViewModelImpl(errorReporter, useCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Action:
    completableSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить у юзкейса начать поллинг только при создании.
   */
  @Test
  public void askUseCaseForExecutorBalancesInitially() {
    // Effect:
    verify(useCase, only()).listenForPolling();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Action:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Effect:
    verify(useCase, only()).listenForPolling();
  }

  /* Тетсируем переключение состояния. */

  /**
   * Не должен трогать вид по завершению.
   */
  @Test
  public void doNotTouchViewActionsOnComplete() {
    // Given:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    completableSubject.onComplete();

    // Effect:
    verifyNoInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при ошибке маппинга.
   */
  @Test
  public void doNotTouchViewActionsOnMappingError() {
    // Given:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    completableSubject.onError(new DataMappingException());

    // Effect:
    verifyNoInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при другой ошибке.
   */
  @Test
  public void doNotTouchViewActionsOnOtherError() {
    // Given:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    completableSubject.onError(new Exception());

    // Effect:
    verifyNoInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке формата данных от сервера".
   */
  @Test
  public void navigateToServerDataError() {
    // Given:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Action:
    completableSubject.onError(new DataMappingException());

    // Effect:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен ничего возвращать.
   */
  @Test
  public void doNotNavigateForOtherError() {
    // Given:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Action:
    completableSubject.onError(new Exception());

    // Effect:
    verifyNoInteractions(navigationObserver);
  }

  /**
   * Не должен ничего возвращать.
   */
  @Test
  public void doNotNavigateForComplete() {
    // Given:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Action:
    completableSubject.onComplete();

    // Effect:
    verifyNoInteractions(navigationObserver);
  }
}