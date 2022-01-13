package com.cargopull.executor_driver.presentation.phone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.ValidationException;
import com.cargopull.executor_driver.interactor.auth.LoginUseCase;
import com.cargopull.executor_driver.presentation.ViewState;

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

import io.reactivex.Completable;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private PhoneViewModel viewModel;
  @Mock
  private LoginUseCase useCase;

  @Mock
  private Observer<ViewState<PhoneViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    viewModel = new PhoneViewModelImpl(useCase);
    when(useCase.validateLogin(anyString())).thenReturn(Completable.never());
    when(useCase.rememberLogin()).thenReturn(Completable.complete());
  }

  /* Тетсируем работу с юзкейсом */

  /**
   * Должен попросить юзкейс валидировать логин.
   */
  @Test
  public void askUseCaseToValidateLogin() {
    when(useCase.validateLogin(anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));
    // Action:
    viewModel.phoneNumberChanged("12");
    viewModel.phoneNumberChanged("123");
    viewModel.phoneNumberChanged("1234");

    // Effect:
    verify(useCase).validateLogin("12");
    verify(useCase).validateLogin("123");
    verify(useCase).validateLogin("1234");
    verifyNoMoreInteractions(useCase);
  }

  /**
   * Должен попросить юзкейс запонмнить логин.
   */
  @Test
  public void askUseCaseToRememberLogin() {
    when(useCase.validateLogin(anyString())).thenReturn(Completable.complete());
    // Action:
    viewModel.phoneNumberChanged("1234");
    viewModel.nextClicked();

    // Effect:
    verify(useCase).validateLogin("1234");
    verify(useCase).rememberLogin();
    verifyNoMoreInteractions(useCase);
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть начальное состояние вида.
   */
  @Test
  public void setInitialViewStateToLiveData() {
    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    verify(viewStateObserver, only()).onChanged(any(PhoneViewStateInitial.class));
  }

  /**
   * Не должен менять состояние вида, если логин не валидируется.
   */
  @Test
  public void setNoNewViewStateToLiveData() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(useCase.validateLogin(anyString()))
        .thenReturn(Completable.error(new ValidationException()));

    // Action:
    viewModel.phoneNumberChanged("");
    viewModel.phoneNumberChanged("12");
    viewModel.phoneNumberChanged("1245");
    viewModel.phoneNumberChanged("12457");

    // Effect:
    verify(viewStateObserver, only()).onChanged(any(PhoneViewStateInitial.class));
  }

  /**
   * Должен вернуть состояние вида "Готов".
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(useCase.validateLogin(anyString()))
        .thenReturn(Completable.error(new ValidationException()));
    when(useCase.validateLogin("12457")).thenReturn(Completable.complete());

    // Action:
    viewModel.phoneNumberChanged("");
    viewModel.phoneNumberChanged("12");
    viewModel.phoneNumberChanged("1245");
    viewModel.phoneNumberChanged("12457");

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть начальное состояние вида после состояния "Готов", если номер изменился и не
   * валидировался.
   */
  @Test
  public void setInitialViewStateToLiveDataAfterReady() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(useCase.validateLogin(anyString()))
        .thenReturn(Completable.error(new ValidationException()));
    when(useCase.validateLogin("1245")).thenReturn(Completable.complete());

    // Action:
    viewModel.phoneNumberChanged("1245");
    viewModel.phoneNumberChanged("124");
    viewModel.phoneNumberChanged("123");
    viewModel.phoneNumberChanged("1234");
    viewModel.phoneNumberChanged("12345");

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть начальное состояние вида повторно.
   */
  @Test
  public void doNotSetInitialViewStateToLiveDataPending() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.nextClicked();

    // Effect:
    inOrder.verify(viewStateObserver, times(2)).onChanged(any(PhoneViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен ввернуть начальное состояние после "Готов".
   */
  @Test
  public void setInitialViewStateToLiveDataPending() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(useCase.validateLogin(anyString())).thenReturn(Completable.complete());

    // Action:
    viewModel.phoneNumberChanged("(124)5");
    viewModel.nextClicked();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к вводу пароля".
   */
  @Test
  public void setNavigateToPasswordToLiveData() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    when(useCase.validateLogin(anyString())).thenReturn(Completable.complete());

    // Action:
    viewModel.phoneNumberChanged("(124)5");
    viewModel.nextClicked();

    // Effect:
    verify(navigateObserver, only()).onChanged(PhoneNavigate.PASSWORD);
  }
}