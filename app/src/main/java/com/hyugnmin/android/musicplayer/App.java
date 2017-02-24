package com.hyugnmin.android.musicplayer;

import android.app.Application;
import android.media.MediaPlayer;

/**
 * Created by besto on 2017-02-24.
 */

public class App {

    public static MediaPlayer player = null;

    //액션플래그
    public static final String ACTION_PLAY = "com.hyugnmin.android.musicplayer.Action.Play";
    public static final String ACTION_PAUSE = "com.hyugnmin.android.musicplayer.Action.Pause";
    public static final String ACTION_STOP = "com.hyugnmin.android.musicplayer.Action.Stop";



    //상태
    public static int position = 0;

    public static final int PLAY = 0;
    public static final int PAUSE = 1;
    public static final int STOP = 2;
    public static int playStatus = STOP; //플레이어상태

}
