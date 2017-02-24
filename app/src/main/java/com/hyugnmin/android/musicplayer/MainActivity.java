package com.hyugnmin.android.musicplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import java.util.List;

import static com.hyugnmin.android.musicplayer.App.PLAY;
import static com.hyugnmin.android.musicplayer.App.position;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (App.playStatus == PLAY) {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("position", App.position);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_main); //앱을 껐다가 실행중이면 플레이어 액티비티로 이동한다.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermission();
            } else {
                init();
            }
        }
    }

    private final int REQ_CODE = 100;
    //1. 권한 체크
    @TargetApi(Build.VERSION_CODES.M) //target 지정 annotation
    private void checkPermission() {
        // 1.1 런타임 권한 체크
        if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //1.2 요청할 권한 목록 작성
            String permArr[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
            //1.3 시스템에 권한 요청
            requestPermissions(permArr, REQ_CODE);
        }else {
            init();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_CODE) {
            //배열에 넘긴 런타임권한을 체크해서 승인이 됐으면
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //프로그램 실행
                init();
            } else {
                Message.show("권한을 허용하지 않으시면 프로그램을 실행할 수 없습니다.", this);
                //선택 1 . 종료  / 2. 권한체크 다시 물어보기
                finish();
            }
        }
    }

    //데이터 로드 함수
    private void init() {
        Message.show("프로그램을 실행합니다.", this);
        listInit();
    }

    private void listInit() {
        //리사이클러뷰 세팅
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MusicAdapter adapter = new MusicAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}
