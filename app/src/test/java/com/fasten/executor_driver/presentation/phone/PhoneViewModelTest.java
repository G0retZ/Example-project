package com.fasten.executor_driver.presentation.phone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.entity.ValidationException;
import com.fasten.executor_driver.interactor.auth.LoginUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private PhoneViewModel phoneViewModel;
  @Mock
  private LoginUseCase useCase;

  @Mock
  private Observer<ViewState<PhoneViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    phoneViewModel = new PhoneViewModelImpl(useCase);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
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
    // Действие:
    phoneViewModel.phoneNumberChanged("12");
    phoneViewModel.phoneNumberChanged("123");
    phoneViewModel.phoneNumberChanged("1234");

    // Результат:
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
    // Действие:
    phoneViewModel.phoneNumberChanged("1234");
    phoneViewModel.nextClicked();

    // Результат:
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
    // Действие:
    phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(PhoneViewStateInitial.class));
  }

  /**
   * Не должен менять состояние вида, если логин не валидируется.
   */
  @Test
  public void setNoNewViewStateToLiveData() {
    // Дано:
    phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(useCase.validateLogin(anyString()))
        .thenReturn(Completable.error(new ValidationException()));

    // Действие:
    phoneViewModel.phoneNumberChanged("");
    phoneViewModel.phoneNumberChanged("12");
    phoneViewModel.phoneNumberChanged("1245");
    phoneViewModel.phoneNumberChanged("12457");

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(PhoneViewStateInitial.class));
  }

  /**
   * Должен вернуть состояние вида "Готов".
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(useCase.validateLogin(anyString()))
        .thenReturn(Completable.error(new ValidationException()));
    when(useCase.validateLogin("12457")).thenReturn(Completable.complete());

    // Действие:
    phoneViewModel.phoneNumberChanged("");
    phoneViewModel.phoneNumberChanged("12");
    phoneViewModel.phoneNumberChanged("1245");
    phoneViewModel.phoneNumberChanged("12457");

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(useCase.validateLogin(anyString()))
        .thenReturn(Completable.error(new ValidationException()));
    when(useCase.validateLogin("1245")).thenReturn(Completable.complete());

    // Действие:
    phoneViewModel.phoneNumberChanged("1245");
    phoneViewModel.phoneNumberChanged("124");
    phoneViewModel.phoneNumberChanged("123");
    phoneViewModel.phoneNumberChanged("1234");
    phoneViewModel.phoneNumberChanged("12345");

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать начальное состояние вида если не "Готов".
   */
  @Test
  public void doNotSetInitialViewStateToLiveDataPending() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    phoneViewModel.nextClicked();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен ввернуть начальное состояние после "Готов".
   */
  @Test
  public void setInitialViewStateToLiveDataPending() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(useCase.validateLogin(anyString())).thenReturn(Completable.complete());

    // Действие:
    phoneViewModel.phoneNumberChanged("(124)5");
    phoneViewModel.nextClicked();

    // Результат:
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
    // Дано:
    phoneViewModel.getNavigationLiveData().observeForever(navigateObserver);
    when(useCase.validateLogin(anyString())).thenReturn(Completable.complete());

    // Действие:
    phoneViewModel.phoneNumberChanged("(124)5");
    phoneViewModel.nextClicked();

    // Результат:
    verify(navigateObserver, only()).onChanged(PhoneNavigate.PASSWORD);
  }
}