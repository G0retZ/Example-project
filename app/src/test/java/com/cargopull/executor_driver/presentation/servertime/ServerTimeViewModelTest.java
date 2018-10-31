package com.cargopull.executor_driver.presentation.servertime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ServerTimeUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.subjects.CompletableSubject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServerTimeViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ServerTimeViewModel currentCostPollingViewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ServerTimeUseCase useCase;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<Runnable>> viewStateObserver;

  private CompletableSubject completableSubject;

  @Before
  public void setUp() {
    completableSubject = CompletableSubject.create();
    when(useCase.getServerTime()).thenReturn(completableSubject);
    currentCostPollingViewModel = new ServerTimeViewModelImpl(errorReporter, useCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Действие:
    completableSubject.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить у юзкейса текущеее времея сервера только при создании.
   */
  @Test
  public void askUseCaseToSubscribeToServerTimeInitially() {
    // Результат:
    verify(useCase, only()).getServerTime();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Действие:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Результат:
    verify(useCase, only()).getServerTime();
  }

  /* Тетсируем переключение состояния. */

  /**
   * Не должен трогать вид по завершению.
   */
  @Test
  public void doNotTouchViewActionsOnComplete() {
    // Дано:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    completableSubject.onComplete();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при ошибке маппинга.
   */
  @Test
  public void doNotTouchViewActionsOnMappingError() {
    // Дано:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    completableSubject.onError(new DataMappingException());

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при другой ошибке.
   */
  @Test
  public void doNotTouchViewActionsOnOtherError() {
    // Дано:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    completableSubject.onError(new Exception());

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке формата данных от сервера".
   */
  @Test
  public void navigateToServerDataError() {
    // Дано:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    completableSubject.onError(new DataMappingException());

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен ничего возвращать.
   */
  @Test
  public void doNotNavigateForOtherError() {
    // Дано:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    completableSubject.onError(new Exception());

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Не должен ничего возвращать.
   */
  @Test
  public void doNotNavigateForComplete() {
    // Дано:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    completableSubject.onComplete();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }
}