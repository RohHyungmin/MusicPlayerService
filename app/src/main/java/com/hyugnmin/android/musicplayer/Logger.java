package com.hyugnmin.android.musicplayer;

import android.util.Log;

/** 로깅객체
 * Created by besto on 2017-01-26.
 */

public class Logger {
    public final static boolean DEBUG_MODE = true; //BuildConfig.DEBUG; <- 표준

    /**로그 내용을 콘솔에 출력
     *
     * @param string
     * @param className
     */

    public static void print(String string, String className) {
        if(DEBUG_MODE)
            Log.d(className, string);

        //로그내용을 파일에 저장
        //File.append...()

    }
}
