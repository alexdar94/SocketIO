package com.now.socketio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class LeapMotion_Activity extends AppCompatActivity {
    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leap_motion);

        try{
            socket = IO.socket("http://192.168.136.40:3000");
            socket.on("stabilizedPosition", onStabilizedPositionReceiver);
            socket.connect();
        }catch (URISyntaxException e){throw new RuntimeException(e);}
    }

    private Emitter.Listener onStabilizedPositionReceiver = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    double stabilizedPosition_x, stabilizedPosition_y, stabilizedPosition_z;
                    try {
                        stabilizedPosition_x = data.getDouble("stabilizedPosition_x");
                        Log.e("stabilizedPosition_x", stabilizedPosition_x+"");
                        stabilizedPosition_y = data.getDouble("stabilizedPosition_y");
                        Log.e("stabilizedPosition_y", stabilizedPosition_y+"");
                        stabilizedPosition_z = data.getDouble("stabilizedPosition_z");
                        Log.e("stabilizedPosition_z", stabilizedPosition_z+"");
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
        socket.off("stabilizedPosition", onStabilizedPositionReceiver);
    }
}