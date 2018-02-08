package com.fasten.executor_driver.view.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.fasten.executor_driver.entity.CodeExtractor;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Перехватываем входящие сообщения
 */
public class SmsReceiver extends BroadcastReceiver {

  public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
  private final Subject<String> subject = PublishSubject.create();
  private final CodeExtractor smsCodeExtractor;

  @Inject
  public SmsReceiver(@Named("smsExtractor") CodeExtractor smsCodeExtractor) {
    this.smsCodeExtractor = smsCodeExtractor;
  }

  /**
   * Получаем коды из входящих СМС.
   *
   * @return {@link Observable<String>} публикует коды.
   */
  public Observable<String> getCodeFromSms() {
    return subject;
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent != null && intent.getAction() != null
        && ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
      Bundle extras = intent.getExtras();
      if (extras != null) {
        Object[] pduArray = (Object[]) extras.get("pdus");
        if (pduArray != null) {
          SmsMessage[] messages = new SmsMessage[pduArray.length];
          for (int i = 0; i < pduArray.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
          }
          // Проверяем отправителя
          String sms_from = messages[0].getDisplayOriginatingAddress();
          if (sms_from.equals("FASTEN")) {
            StringBuilder bodyText = new StringBuilder();
            for (SmsMessage message : messages) {
              bodyText.append(message.getMessageBody());
            }
            String body = bodyText.toString();
            body = smsCodeExtractor.extractCode(body);
            if (body != null) {
              subject.onNext(body);
            }
          }
        }
      }
    }
  }
}