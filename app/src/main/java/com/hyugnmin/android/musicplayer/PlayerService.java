package com.hyugnmin.android.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static com.hyugnmin.android.musicplayer.App.ACTION_PAUSE;
import static com.hyugnmin.android.musicplayer.App.ACTION_PLAY;
import static com.hyugnmin.android.musicplayer.App.ACTION_STOP;
import static com.hyugnmin.android.musicplayer.App.PAUSE;
import static com.hyugnmin.android.musicplayer.App.PLAY;
import static com.hyugnmin.android.musicplayer.App.playStatus;
import static com.hyugnmin.android.musicplayer.App.player;

public class PlayerService extends Service {
    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_STOP :
                case ACTION_PLAY :
                    playStart();
                    break;

                case ACTION_PAUSE :
                    playPause();
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void playStart() {
        player.start();
        playStatus = PLAY;
    }

    private void playPause() {
        player.pause();
        playStatus = PAUSE;
    }

    private void playRestart() {
        player.start();
        playStatus = PLAY;
    }
}
