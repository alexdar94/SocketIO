package com.now.socketio;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class LeapMotion_Activity extends AppCompatActivity {
    private Socket socket;
    private DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        dv = new DrawingView(this, mPaint);
        setContentView(dv);

        try {
            socket = IO.socket("http://192.168.136.16:3000");
            socket.on("stabilizedPosition", onStabilizedPositionReceiver);
            socket.on("time", time);
            socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    boolean m = false;
    private Emitter.Listener onStabilizedPositionReceiver = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    double stabilizedPosition_x, stabilizedPosition_y, stabilizedPosition_z;
                    try {
                        stabilizedPosition_x = data.getDouble("stabilizedPosition_x");
                        Log.e("stabilizedPosition_x", stabilizedPosition_x + "");
                        stabilizedPosition_y = data.getDouble("stabilizedPosition_y");
                        Log.e("stabilizedPosition_y", stabilizedPosition_y + "");
                        stabilizedPosition_z = data.getDouble("stabilizedPosition_z");
                        Log.e("stabilizedPosition_z", stabilizedPosition_z + "");
                        //stabilizedPosition_y*=-1;
                        if (!m) {
                            dv.startDraw((float) stabilizedPosition_x+200, (float) ((float) 600-stabilizedPosition_y));
                            m = true;
                        } else {
                            dv.moveDraw((float) stabilizedPosition_x+200, (float) ((float) 600-stabilizedPosition_y));
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener time = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.e("time", data.getString("time"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.off("stabilizedPosition", onStabilizedPositionReceiver);
        socket.off("time", time);
        socket.disconnect();

    }
}