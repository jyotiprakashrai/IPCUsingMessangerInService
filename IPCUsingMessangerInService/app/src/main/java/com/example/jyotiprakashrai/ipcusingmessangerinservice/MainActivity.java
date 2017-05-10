package com.example.jyotiprakashrai.ipcusingmessangerinservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



/*

This example demonstrates the IPC between component and service running in different process
 */
public class MainActivity extends AppCompatActivity {
    private final int JOB_1=1;
    private final int JOB_2=2;
    private final int JOB_1_RESPONSE=3;
    private final int JOB_2_RESPONSE=4;

    Messenger messenger=null;
    Boolean isBind=false;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=new Intent(this,MyService.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);

        textView= (TextView) findViewById(R.id.textView);
    }


    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            messenger=new Messenger(service);
            isBind=true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            isBind=false;
        }
    };


    public void getMessage(View view){
        String button_text= (String) ((Button)view).getText();
        if (button_text.equals("GET FIRST MESSAGE")){

            Message msg=Message.obtain(null,JOB_1);
            msg.replyTo=new Messenger(new ResponseHandler());
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
        else if (button_text.equals("GET SECOND MESSAGE")){
            Message msg=Message.obtain(null,JOB_2);
            msg.replyTo=new Messenger(new ResponseHandler());
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onStop() {

        unbindService(serviceConnection);
        isBind=false;
        messenger=null;
        super.onStop();
    }


    class ResponseHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            String message;
            switch (msg.what){

                case JOB_1_RESPONSE:

                    message=msg.getData().getString("response_message");
                    textView.setText(message);

                    break;


                case JOB_2_RESPONSE:
                    message=msg.getData().getString("response_message");
                    textView.setText(message);
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
