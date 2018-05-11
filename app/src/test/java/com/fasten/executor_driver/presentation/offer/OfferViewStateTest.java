package com.fasten.executor_driver.presentation.offer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OfferViewStateTest {

  private OfferViewState viewState;

  @Mock
  private OfferViewActions offerViewActions;

  @Mock
  private OfferItem offerItem;
  @Mock
  private OfferItem offerItem2;

  @Before
  public void setUp() {
    when(offerItem.getAddress()).thenReturn("address");
    viewState = new OfferViewState(offerItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(offerItem.getAddress()).thenReturn("address");
    when(offerItem.getDistance()).thenReturn("123L");
    when(offerItem.getLoadPointMapUrl()).thenReturn("url");
    when(offerItem.getOfferComment()).thenReturn("comm");
    when(offerItem.getEstimatedPrice()).thenReturn("1000");
    when(offerItem.getOfferOptionsRequired()).thenReturn("1,2,3");
    when(offerItem.getProgressLeft()).thenReturn(new long[]{123, 4532});

    // Действие:
    viewState.apply(offerViewActions);

    // Результат:
    verify(offerViewActions).showLoadPoint("url");
    verify(offerViewActions).showDistance("123L");
    verify(offerViewActions).showLoadPointAddress("address");
    verify(offerViewActions).showEstimatedPrice("1000");
    verify(offerViewActions).showOfferOptionsRequirements("1,2,3");
    verify(offerViewActions).showOfferComment("comm");
    verify(offerViewActions).showTimeout(123, 4532);
    verifyNoMoreInteractions(offerViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OfferViewState(null);

    // Действие:
    viewState.apply(offerViewActions);

    // Результат:
    verifyZeroInteractions(offerViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OfferViewState(offerItem));
    assertNotEquals(viewState, new OfferViewState(offerItem2));
    assertNotEquals(viewState, new OfferViewState(null));
  }
}