package com.hyugnmin.android.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by besto on 2017-02-01.
 */

public class DataLoader {

    //2. 데이터 컨텐츠 uri 정의
    final static Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    //3. 데이터에서 가져올 데이터 컬럼명을 string 배열에 담는다.
    //데이터 컬럼명은 Content uri 패키지에 들어있다.
    final static String PROJ[] = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
    };

    //datas를 두 개의 activity에서 공유하기 위해 static으로 변경
    private static List<Music> datas = new ArrayList<>();
    //static 변수인 datas를 체크해서 null 이면 load를 실행
    public static List<Music> get(Context context)
    {
        if(datas == null || datas.size() == 0) {
            load(context);
        }
        return datas;
    }
    // load함수는 get함수를 통해서만 접근한다.
    private static void load(Context context) {
        //1. 데이터에 접근하기 위해 ContentResolver를 불러오고
        ContentResolver resolver = context.getContentResolver();
        //4. ContentResolver로 쿼리한 데이터를 커서에 담는다.
        Cursor cursor = resolver.query(URI, PROJ, null, null, null);
        //5. Cursor에 담긴 데이터를 반복문을 돌면서 꺼낸다
        if(cursor != null) {
            while(cursor.moveToNext()) {
                Music music = new Music ();


                music.id = getValue(cursor, PROJ[0]);
                music.albumId = getValue(cursor, PROJ[1]);
                music.title = getValue(cursor, PROJ[2]);
                music.artist = getValue(cursor, PROJ[3]);

                music.album_image = getAlbumImageSimple(music.albumId);
                music.uri = getMusicUri(music.id);

                //주석처리..시스템다운..
                //music.bitmap_image = getAlbumImageBitmap(music.albumId);

                datas.add(music);
            }
        cursor.close(); //6. 처리 후 커서를 닫아준다.
        }
    }

    private  static String getValue(Cursor cursor, String columnName) {
        int idx = cursor.getColumnIndex(columnName);
        return cursor.getString(idx);
    }

    private static Uri getMusicUri(String music_id) {
        Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return Uri.withAppendedPath(contentUri, music_id);
    }
        //가장 간단하게 앨범이미지를 가져오는 방법
        //문제점 - 실제 앨범데이터만 있어서 이미지를 불러오지 못하는 경우가 있다.

    private static Uri getAlbumImageSimple(String albumId) {
        return Uri.parse("content://media/external/audio/albumart/" + albumId);
    }
    @Deprecated
    private static Bitmap getAlbumImageBitmap (String albumId, Context context) {
        //1. albumid로 uri 생성
        Uri uri = getAlbumImageSimple(albumId);
        //2. contentresolver 가져오기
        ContentResolver resolver = context.getContentResolver();
        try {
            //3. resolver에서 stream열기
            InputStream is = resolver.openInputStream(uri);
            //4. bitmapFactory를 통해 이미지 데이터를 가져온다
            Bitmap image = BitmapFactory.decodeStream(is);
            //5. 가져온 이미지 return
            return  image;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return  null;
    }


}
