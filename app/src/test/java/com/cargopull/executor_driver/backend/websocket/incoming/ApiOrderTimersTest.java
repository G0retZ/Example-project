package com.cargopull.executor_driver.backend.websocket.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApiOrderTimersTest {

  @Test
  public void testConstructor() {
    // Дано:
    ApiOrderTimers apiOrderTimers = new ApiOrderTimers(9876543210L, 12312412L);

    // Результат
    assertEquals(apiOrderTimers.getOverPackageTimer(), new Long(9876543210L));
    assertEquals(apiOrderTimers.getOverPackagePeriod(), new Long(12312412L));
  }

  @Test
  public void testConstructorWithFirstNull() {
    // Дано:
    ApiOrderTimers apiOrderTimers = new ApiOrderTimers(null, 12312412L);

    // Результат
    assertNull(apiOrderTimers.getOverPackageTimer());
    assertEquals(apiOrderTimers.getOverPackagePeriod(), new Long(12312412L));
  }

  @Test
  public void testConstructorWithSecondNull() {
    // Дано:
    ApiOrderTimers apiOrderTimers = new ApiOrderTimers(9876543210L, null);

    // Результат
    assertEquals(apiOrderTimers.getOverPackageTimer(), new Long(9876543210L));
    assertNull(apiOrderTimers.getOverPackagePeriod());
  }

  @Test
  public void testConstructorWithNulls() {
    // Дано:
    ApiOrderTimers apiOrderTimers = new ApiOrderTimers(null, null);

    // Результат
    assertNull(apiOrderTimers.getOverPackageTimer());
    assertNull(apiOrderTimers.getOverPackagePeriod());
  }
}