package com.cargopull.executor_driver.backend.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.utils.Pair;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@RunWith(Parameterized.class)
public class ServerResponseInterceptorTest {

  private final int code;
  private final String body;
  private final boolean isMyException;
  private final String resultCode;
  @Rule
  public MockitoRule rule = MockitoJUnit.rule();
  private ServerResponseInterceptor serverResponseInterceptor;
  @Mock
  private Interceptor.Chain chain;

  // Each parameter should be placed as an argument here
  // Every time runner triggers, it will pass the arguments
  // from parameters we defined in primeNumbers() method

  public ServerResponseInterceptorTest(
      Pair<Pair<Integer, String>, Pair<Boolean, String>> conditions) {
    code = conditions.first.first;
    body = conditions.first.second;
    isMyException = conditions.second.first;
    resultCode = conditions.second.second;
  }

  @Parameterized.Parameters
  public static Iterable<Pair<Pair<Integer, String>, Pair<Boolean, String>>> primeConditions() {
    List<Integer> codes = Arrays.asList(100, 101, 102, 103,
        200, 201, 202, 203, 204, 205, 206, 207, 208, 218, 226,
        300, 301, 302, 303, 304, 305, 306, 307, 308,
        400, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417,
        418, 419, 420, 421, 422, 423, 424, 426, 428, 429, 431, 440, 444, 449, 450, 451, 494, 495,
        496, 497, 498, 499,
        500, 501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 520, 521, 522, 523, 524, 525,
        526, 527, 530, 598);
    List<String> subCodes = Arrays.asList(null, ".1", ".2", ".3");
    List<Pair<Pair<Integer, String>, Pair<Boolean, String>>> conditions = new ArrayList<>();
    for (int code : codes) {
      for (String subCode : subCodes) {
        conditions.add(new Pair<>(
            new Pair<>(
                code,
                "{" + (subCode == null ? "" : "code=\"" + code + subCode + "\",")
                    + "message=\"Some Error\"}"
            ),
            new Pair<>(
                code >= 400 && code < 600,
                code + (subCode == null ? "" : subCode)
            )
        ));
      }
    }
    return conditions;
  }

  @Before
  public void setUp() {
    serverResponseInterceptor = new ServerResponseInterceptor();
  }

  /**
   * Не должен кидать исключение.
   *
   * @throws Exception error
   */
  @Test
  public void doNotThrowUnauthorizedError() throws Exception {
    // Given:
    when(chain.proceed(nullable(Request.class))).thenReturn(
        new Response.Builder()
            .code(code)
            .protocol(Protocol.HTTP_1_1)
            .message("")
            .body(ResponseBody.create(MediaType.get("application/json"), body))
            .request(new Request.Builder()
                .url("http://www.cargopull.com")
                .build()
            ).build()
    );

    try {
      // Action:
      Response response = serverResponseInterceptor.intercept(chain);
      // Effect:
      assertEquals(chain.proceed(chain.request()), response);
      assertFalse(isMyException);
    } catch (ServerResponseException sre) {
      assertEquals(sre.getCode(), resultCode);
      assertEquals(sre.getMessage(), "Some Error");
      assertTrue(isMyException);
    }
  }
}