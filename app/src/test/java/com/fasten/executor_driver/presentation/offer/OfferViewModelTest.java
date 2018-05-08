package com.fasten.executor_driver.presentation.offer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.NoOffersAvailableException;
import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.OfferUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
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
public class OfferViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OfferViewModel offerViewModel;
  @Mock
  private OfferUseCase offerUseCase;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private Offer offer;
  @Mock
  private Offer offer1;
  @Mock
  private Offer offer2;

  @Mock
  private Observer<ViewState<OfferViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(offerUseCase.getOffers()).thenReturn(Flowable.never());
    when(offerUseCase.sendDecision(anyBoolean())).thenReturn(Completable.never());
    offerViewModel = new OfferViewModelImpl(offerUseCase, timeUtils);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы, при первой и только при первой подписке.
   */
  @Test
  public void askOfferUseCaseForOffersInitially() {
    // Действие:
    offerViewModel.getViewStateLiveData();
    offerViewModel.getViewStateLiveData();
    offerViewModel.getViewStateLiveData();

    // Результат:
    verify(offerUseCase, only()).getOffers();
  }

  /**
   * Должен попросить юзкейс передать принятие заказа.
   */
  @Test
  public void askOfferUseCaseToSendOfferAccepted() {
    // Дано:
    when(offerUseCase.sendDecision(anyBoolean())).thenReturn(Completable.complete());

    // Действие:
    offerViewModel.acceptOffer();

    // Результат:
    verify(offerUseCase, only()).sendDecision(true);
  }

  /**
   * Должен попросить юзкейс передать отказ от заказа.
   */
  @Test
  public void askOfferUseCaseToSendOfferDeclined() {
    // Дано:
    when(offerUseCase.sendDecision(anyBoolean())).thenReturn(Completable.complete());

    // Действие:
    offerViewModel.declineOffer();

    // Результат:
    verify(offerUseCase, only()).sendDecision(false);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос передачи решения еще не завершился.
   */
  @Test
  public void DoNotTouchOfferUseCaseDuringOfferSetting() {
    // Действие:
    offerViewModel.acceptOffer();
    offerViewModel.declineOffer();
    offerViewModel.acceptOffer();

    // Результат:
    verify(offerUseCase, only()).sendDecision(true);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OfferViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveData() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForMappingError() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOfferAvailableErrorViewStateToLiveData() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoOffersAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие" с полученнымы заказами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    publishSubject.onNext(offer1);
    publishSubject.onNext(offer2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer1, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer2, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOfferToLiveDataForAccept() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    offerViewModel.acceptOffer();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OfferViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOfferToLiveDataForDecline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    offerViewModel.declineOffer();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OfferViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOfferToLiveDataForAccept() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    offerViewModel.acceptOffer();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOfferToLiveDataForDecline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    offerViewModel.declineOffer();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOffersAvailableErrorViewStateWithoutOfferToLiveDataForAccept() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoOffersAvailableException::new));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    offerViewModel.acceptOffer();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOffersAvailableErrorViewStateWithoutOfferToLiveDataForDecline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoOffersAvailableException::new));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    offerViewModel.declineOffer();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForAcceptWithoutOffer() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.sendDecision(anyBoolean())).thenReturn(Completable.complete());
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    offerViewModel.acceptOffer();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OfferViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForDeclineWithoutOffer() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.sendDecision(anyBoolean())).thenReturn(Completable.complete());
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    offerViewModel.declineOffer();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OfferViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveDataForAccept() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    offerViewModel.acceptOffer();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(
        new OfferItem(offer, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveDataForDecline() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    offerViewModel.declineOffer();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(
        new OfferItem(offer, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе", и только 1 раз.
   */
  @Test
  public void setPendingViewStateToLiveDataForTimeout() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    offerViewModel.counterTimeOut();
    offerViewModel.counterTimeOut();
    offerViewModel.counterTimeOut();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(
        new OfferItem(offer, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForAccept() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(offerUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    offerViewModel.acceptOffer();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateNetworkError(
        new OfferItem(offer, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForDecline() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(offerUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    offerViewModel.declineOffer();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateNetworkError(
        new OfferItem(offer, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOffersAvailableErrorViewStateToLiveDataForAccept() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(offerUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoOffersAvailableException::new));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    offerViewModel.acceptOffer();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateUnavailableError(
        new OfferItem(offer, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOffersAvailableErrorViewStateToLiveDataForDecline() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(offerUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoOffersAvailableException::new));
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    offerViewModel.declineOffer();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateUnavailableError(
        new OfferItem(offer, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForAccept() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(offerUseCase.sendDecision(anyBoolean())).thenReturn(Completable.complete());
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    offerViewModel.acceptOffer();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(
        new OfferItem(offer, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForDecline() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(offerUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(offerUseCase.sendDecision(anyBoolean())).thenReturn(Completable.complete());
    offerViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    offerViewModel.declineOffer();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStateIdle(
        new OfferItem(offer, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OfferViewStatePending(
        new OfferItem(offer, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }
}