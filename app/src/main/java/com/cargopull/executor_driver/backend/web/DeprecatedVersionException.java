package com.cargopull.executor_driver.backend.web;

import java.io.IOException;

/**
 * Исключение об ошибке несоответствующей версии.
 */

public class DeprecatedVersionException extends IOException {

  DeprecatedVersionException() {
    super();
  }
}
