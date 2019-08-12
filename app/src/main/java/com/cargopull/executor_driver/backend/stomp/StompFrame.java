package com.cargopull.executor_driver.backend.stomp;

import androidx.annotation.NonNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class StompFrame {

  private static final String TERMINATE_MESSAGE_SYMBOL = "\u0000";
  private final Map<String, String> headers = new HashMap<>();
  private Command command;
  private String body;

  public StompFrame(Command command, String body) {
    this.command = command;
    this.body = body;
  }

  public StompFrame(Command command, Map<String, String> headers, String body) {
    this.command = command;
    this.headers.putAll(headers);
    this.body = body;
  }

  static StompFrame fromString(String data) throws IllegalArgumentException, NullPointerException {
    if (data.trim().isEmpty()) {
      return new StompFrame(Command.HEARTBEAT, null);
    }
    if (data.lastIndexOf(TERMINATE_MESSAGE_SYMBOL) != data.length() - 1) {
      throw new IllegalArgumentException("Invalid frame for parsing! Frame was: \"" + data + "\"");
    }
    String[] splitCommand = data.split("\\n", 2);
    StompFrame result = new StompFrame(Command.valueOf(splitCommand[0]), "");
    data = splitCommand[1];
    String[] splitBody = data.split("\\n\\n", 2);
    result.body = splitBody[1];
    String[] headers = splitBody[0].split("\\n");
    for (String header : headers) {
      String[] keyValue = header.split(":");
      result.headers.put(keyValue[0], keyValue[1]);
    }
    String a = result.headers.get("content-length");
    int contentLenght = a == null ? -1 : Integer.valueOf(a);
    if (contentLenght >= 0) {
      byte[] bytes = result.body.getBytes();
      bytes = Arrays.copyOfRange(bytes, 0, contentLenght);
      result.body = new String(bytes);
    } else {
      result.body = result.body.substring(0, result.body.indexOf(TERMINATE_MESSAGE_SYMBOL));
    }
    return result;
  }

  Command getCommand() {
    return command;
  }

  public void addHeader(String key, String value) {
    headers.put(key, value);
  }

  @NonNull
  public Map<String, String> getHeaders() {
    return Collections.unmodifiableMap(headers);
  }

  public String getBody() {
    return body;
  }

  @NonNull
  @Override
  public String toString() {
    StringBuilder res = new StringBuilder(command.name());
    for (Entry<String, String> entry : headers.entrySet()) {
      res.append("\n")
          .append(entry.getKey())
          .append(":")
          .append(entry.getValue());
    }
    res.append("\n\n")
        .append(body)
        .append(TERMINATE_MESSAGE_SYMBOL);
    return res.toString();
  }
}
