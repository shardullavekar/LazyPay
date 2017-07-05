package lazypay.app.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/**
 * Created by shardullavekar on 03/07/17.
 */

public class ReadSms extends BroadcastReceiver {

    private static SMSListener smsListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage;

            if (Build.VERSION.SDK_INT >= 19) { //KITKAT
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                smsMessage = msgs[0];
            } else {
                pdus = (Object[]) data.get("pdus");
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
            }

            String sender = smsMessage.getDisplayOriginatingAddress();

            String messageBody = smsMessage.getMessageBody();

            smsListener.onOTPReceived(messageBody);
        }
    }

    public static void bindListener(SMSListener listener) {
        smsListener = listener;
    }
}
