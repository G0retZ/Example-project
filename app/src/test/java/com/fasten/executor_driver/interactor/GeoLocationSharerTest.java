package com.fasten.executor_driver.interactor;

import com.fasten.executor_driver.entity.GeoLocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationSharerTest {

  private GeoLocationSharer geoLocationSharer;

  @Before
  public void setUp() throws Exception {
    geoLocationSharer = new GeoLocationSharer();
  }

  /**
   * Должен получить значение без изменений.
   *
   * @throws Exception error
   */
  @Test
  public void valueUnchangedForRead() throws Exception {
    // Дано:
    geoLocationSharer.share(new GeoLocation(0.1231, 123.1231, 239823901));

    // Действие и Результат:
    geoLocationSharer.get().test().assertValue(new GeoLocation(0.1231, 123.1231, 239823901));
  }
}