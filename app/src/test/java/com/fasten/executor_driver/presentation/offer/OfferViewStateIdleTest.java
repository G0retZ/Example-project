package com.fasten.executor_driver.presentation.offer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OfferViewStateIdleTest {

  private OfferViewStateIdle viewState;

  @Mock
  private OfferViewActions offerViewActions;

  @Mock
  private OfferItem offerItem;

  @Before
  public void setUp() {
    when(offerItem.getAddress()).thenReturn("address");
    viewState = new OfferViewStateIdle(offerItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(offerItem.getAddress()).thenReturn("address");
    when(offerItem.getDistance()).thenReturn(123L);
    when(offerItem.getLoadPointMapUrl()).thenReturn("url");
    when(offerItem.getOfferComment()).thenReturn("comm");
    when(offerItem.getPassengersCount()).thenReturn(1);
    when(offerItem.getPortersCount()).thenReturn(2);
    when(offerItem.getProgressLeft()).thenReturn(new long[]{123, 4532});

    // Действие:
    viewState.apply(offerViewActions);

    // Результат:
    verify(offerViewActions).showLoadPoint("url");
    verify(offerViewActions).showDistance(123L);
    verify(offerViewActions).showLoadPointAddress("address");
    verify(offerViewActions).showPassengersCount(1);
    verify(offerViewActions).showPortersCount(2);
    verify(offerViewActions).showOfferComment("comm");
    verify(offerViewActions).showTimeout(123, 4532);
    verify(offerViewActions).showOfferPending(false);
    verify(offerViewActions).enableAcceptButton(true);
    verify(offerViewActions).enableDeclineButton(true);
    verify(offerViewActions).showOfferAvailabilityError(false);
    verify(offerViewActions).showOfferNetworkErrorMessage(false);
    verifyNoMoreInteractions(offerViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OfferViewStateIdle(null);

    // Действие:
    viewState.apply(offerViewActions);

    // Результат:
    verify(offerViewActions).showOfferPending(false);
    verify(offerViewActions).enableAcceptButton(true);
    verify(offerViewActions).enableDeclineButton(true);
    verify(offerViewActions).showOfferAvailabilityError(false);
    verify(offerViewActions).showOfferNetworkErrorMessage(false);
    verifyNoMoreInteractions(offerViewActions);
  }
}