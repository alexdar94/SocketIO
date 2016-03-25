package com.now.socketio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            socket = IO.socket("http://192.168.136.21:3000");
            socket.on("message from pc", onNewMessage);
            socket.on("time", onTimeReceiver);
            socket.connect();
        }catch (URISyntaxException e){throw new RuntimeException(e);}

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("received","message");
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                        Log.e("username",username);
                        Log.e("message",message);
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onTimeReceiver = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("received","message");
                    JSONObject data = (JSONObject) args[0];
                    String time;
                    try {
                        time = data.getString("time");
                        Log.e("time",time);
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off("message from pc", onNewMessage);
        socket.off("time", onTimeReceiver);
    }
}
