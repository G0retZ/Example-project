package com.cargopull.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.Problem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApiProblemTest {

  @Mock
  private Problem problem;
  @Mock
  private Problem problem1;
  @Mock
  private Problem problem2;
  @Mock
  private Problem problem3;

  @Test
  public void testConstructor() {
    ApiProblem cancelOrderReason = new ApiProblem(4, "name", "unused");
    assertEquals(cancelOrderReason.getId(), 4);
    assertEquals(cancelOrderReason.getDescription(), "name");
  }

  @Test
  public void testConstructorWithNull() {
    ApiProblem apiProblem = new ApiProblem(0, null, null);
    assertEquals(apiProblem.getId(), 0);
    assertNull(apiProblem.getDescription());
    assertNull(apiProblem.getName());
  }

  @Test
  public void testConstructorWithProblem() {
    // Дано:
    when(problem.getId()).thenReturn(7);
    when(problem.getName()).thenReturn("seven");
    when(problem.getUnusedName()).thenReturn("unused");

    // Действие:
    ApiProblem apiProblem = new ApiProblem(problem);

    // Результат:
    assertEquals(apiProblem.getId(), 7);
    assertEquals(apiProblem.getDescription(), "seven");
    assertEquals(apiProblem.getName(), "unused");
  }


  @Test
  public void testEquals() {
    // Дано:
    when(problem.getId()).thenReturn(7);
    when(problem.getName()).thenReturn("seven");
    when(problem.getUnusedName()).thenReturn("unused");
    when(problem1.getId()).thenReturn(6);
    when(problem1.getName()).thenReturn("seven");
    when(problem1.getUnusedName()).thenReturn("unused");
    when(problem2.getId()).thenReturn(7);
    when(problem2.getName()).thenReturn("six");
    when(problem2.getUnusedName()).thenReturn("unused");
    when(problem3.getId()).thenReturn(7);
    when(problem3.getName()).thenReturn("seven");
    when(problem3.getUnusedName()).thenReturn("nom");

    // Действие:
    ApiProblem apiProblem = new ApiProblem(problem);

    // Результат:
    assertEquals(apiProblem, apiProblem);
    assertEquals(new ApiProblem(7, "seven", "unused"), new ApiProblem(7, "seven", "unused"));
    assertEquals(apiProblem, new ApiProblem(7, "seven", "unused"));
    assertEquals(apiProblem, new ApiProblem(problem));
    assertNotEquals(new ApiProblem(7, "seven", "unused"), new ApiProblem(6, "seven", "unused"));
    assertNotEquals(new ApiProblem(7, "seven", "unused"), new ApiProblem(7, "six", "unused"));
    assertNotEquals(new ApiProblem(7, "seven", "unused"), new ApiProblem(7, "seven", "nom"));
    assertNotEquals(apiProblem, new ApiProblem(problem1));
    assertNotEquals(apiProblem, new ApiProblem(problem2));
    assertNotEquals(apiProblem, new ApiProblem(problem3));
    assertNotEquals(new ApiProblem(7, "seven", "unused"), new ApiProblem(problem1));
    assertNotEquals(new ApiProblem(7, "seven", "unused"), new ApiProblem(problem2));
    assertNotEquals(new ApiProblem(7, "seven", "unused"), new ApiProblem(problem3));
    assertNotEquals(apiProblem, new ApiProblem(6, "seven", "unused"));
    assertNotEquals(apiProblem, new ApiProblem(7, "six", "unused"));
    assertNotEquals(apiProblem, new ApiProblem(7, "seven", "nom"));
    assertNotEquals(apiProblem, "");
    assertNotEquals(apiProblem, null);
  }
}