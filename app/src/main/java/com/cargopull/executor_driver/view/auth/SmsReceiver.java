package com.cargopull.executor_driver.view.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;
import com.cargopull.executor_driver.gateway.Mapper;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import javax.inject.Inject;

/**
 * Перехватываем входящие сообщения.
 */
public class SmsReceiver extends BroadcastReceiver {

  public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
  private final Subject<String> subject = PublishSubject.create();
  private final Mapper<String, String> smsCodeMapper;

  @Inject
  public SmsReceiver(@NonNull Mapper<String, String> smsCodeMapper) {
    this.smsCodeMapper = smsCodeMapper;
  }

  /**
   * Получаем коды из входящих СМС.
   *
   * @return {@link Observable<String>} публикует коды
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
          if ("FASTEN".equals(sms_from) || "VEZET".equals(sms_from)
              || "VEZETDOBRO".equals(sms_from)) {
            StringBuilder bodyText = new StringBuilder();
            for (SmsMessage message : messages) {
              bodyText.append(message.getMessageBody());
            }
            try {
              subject.onNext(smsCodeMapper.map(bodyText.toString()));
            } catch (Exception e) {
              subject.onError(e);
            }
          }
        }
      }
    }
  }
}