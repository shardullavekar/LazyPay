package lazypay.app.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by shardullavekar on 03/07/17.
 */

public class ReadSms extends BroadcastReceiver {

    private static SMSListener smsListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        String otp = getVerificationCode(msgBody);
                        smsListener.onOTPReceived(otp);
                    }
                } catch(Exception e){
//                 Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    public static void bindListener(SMSListener listener) {
        smsListener = listener;
    }

    private String getVerificationCode(String message) {
        String code = null;
        int index = 0;

        if (index != -1) {
            int start = index;
            int length = 4;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
