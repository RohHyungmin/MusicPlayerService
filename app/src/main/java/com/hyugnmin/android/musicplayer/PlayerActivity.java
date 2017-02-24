package com.hyugnmin.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.hyugnmin.android.musicplayer.App.ACTION_PAUSE;
import static com.hyugnmin.android.musicplayer.App.ACTION_PLAY;
import static com.hyugnmin.android.musicplayer.App.ACTION_STOP;
import static com.hyugnmin.android.musicplayer.App.PAUSE;
import static com.hyugnmin.android.musicplayer.App.PLAY;
import static com.hyugnmin.android.musicplayer.App.STOP;
import static com.hyugnmin.android.musicplayer.App.playStatus;
import static com.hyugnmin.android.musicplayer.App.player;
import static com.hyugnmin.android.musicplayer.App.position;

public class PlayerActivity extends AppCompatActivity {

    ImageButton btnFf, btnPlay,btnRw;
    ViewPager viewPager;
    List<Music> datas;
    PlayerAdapter adapter;
    SeekBar seekBar;
    TextView textSec, textCurrent;

    Intent service;



    // 핸들러 상태 플래그
    public static final int PROGRESS_SET = 101;

    //핸들러
    Handler handler = new Handler () {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS_SET:
                    if(player != null) {
                        seekBar.setProgress(player.getCurrentPosition());
                        textCurrent.setText(convertMiliToTime(player.getCurrentPosition()) + "");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        service = new Intent(this, PlayerService.class);

        //볼륨조절버튼으로 미디어 음량 조절
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        getWidget(); //위젯 찾아오기
        listenerSetiing(); //리스너 등록
        viewPagerSetting(); //뷰페이저 세팅
        goPage(getIntent()); //특정 페이지 호출
    }

    private void getWidget() {
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        textSec = (TextView) findViewById(R.id.textSec);
        textCurrent = (TextView) findViewById(R.id.textCurrent);
        btnFf = (ImageButton)findViewById(R.id.btnFf);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnRw = (ImageButton) findViewById(R.id.btnRw);
    }

    private void listenerSetiing() {
        //seekBar의 변경사항을 체크하는 리스너 등록
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        btnFf.setOnClickListener(clickListener);
        btnPlay.setOnClickListener(clickListener);
        btnRw.setOnClickListener(clickListener);
    }

    private void viewPagerSetting () {
        //1.데이터 가져오기
        datas = DataLoader.get(this);
        //2. 뷰페이저 가져오기
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //3.뷰페이저용 아답터 생성
        adapter = new PlayerAdapter(datas, this);
        //4. 뷰페이저 아답터 연결
        viewPager.setAdapter(adapter);
        //4.1 뷰페이저 리스너 연결
        viewPager.addOnPageChangeListener(viewPagerListener);
        //페이지 트랜스포머 연결
        viewPager.setPageTransformer(false, pageTransformer);
    }

    private void goPage(Intent intent) {
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            position = bundle.getInt("position");
            //음악 기본 정보를 설정해준다.(음원 길이..)
            // 첫 페이지일 경우만 init 호출
            // - 첫 페이지가 아닐 경우 위의 setCurrentItem에 의해서 ViewPager의 onPageSelected가 호출된다.
            if(position == 0) {
                init();
            } else {
                viewPager.setCurrentItem(position);//페이지이동
            }

        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btnFf:
                    next();
                    break;
                case R.id.btnRw :
                    prev();
                    break;
                case R.id.btnPlay:
                    play();
                    break;
            }
        }
    };

    private void init() {
        //뷰페이저로 이동할 경우 플레이어에 세팅된 값을 해제한 후 로직을 실행한다
        if(player != null && playStatus != PLAY) {
            //플레이어 상태를 STOP으로 변경
            playStatus = STOP;
            //아이콘을 플레이 버튼으로 변경
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
            player.release();
        }
        playerInit();
        controllerInit();

        if(App.playStatus != PLAY) {
            play();
        }else {

        }

    }


    private void playerInit() {
        Uri musicUri = datas.get(position).uri;
        player = MediaPlayer.create(this, musicUri);
        player.setLooping(false); //반복여부
        //미디어 플레이어에 완료체크 리스너를 등록한다
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }

    private void controllerInit() {
        //seekbar  길이
        seekBar.setMax(player.getDuration());
        //seekbar 현재값 0으로
        seekBar.setProgress(0);
        textSec.setText(convertMiliToTime(player.getDuration()) + "");
        textCurrent.setText("0"); //현재 실행시간을 0으로 설정
    }

    private void play() {

        switch (playStatus) {
            case STOP: //플레이어에 음원 세팅
                playStart();
                break;
            case PLAY : //플레이중이면 멈춤
                playPause();
                break;
            case PAUSE: //멈춤 상태면 거기서부터 재생
                playRestart();
                break;
        }
    }

    private void playStart() {

        playStatus = PLAY;
        //서비스 액티비티보다 쓰레드가 훨씬 가벼워서 서비스가 실행될 준비를 하는 동안 아래 로직이 실행되버려서
        //스테이터스 체크를 하지 못한다 때문에 위에서 중복으로 해준다.
        service.setAction(ACTION_PLAY); //서비스측으로 명령어를 보내준다
        startService(service);

        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        //sub thread를 생성해서 mediaplayer의 현재 포지션 값으로 seekbar를 변경해준다 매 1초마다
        Thread thread = new TimerThread();
        thread.start();
    }

    private void playPause() {

        service.setAction(ACTION_PAUSE);
        startService(service);

        btnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    private void playRestart() {

        service.setAction(ACTION_STOP);
        startService(service);

        player.seekTo(player.getCurrentPosition());
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
    }

    private  String convertMiliToTime(long mili){
        long min = mili/1000/60;
        long sec = mili/1000%60;
        return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }

    private void prev() {
        if(position > 0) {
            viewPager.setCurrentItem(position -1);
        }
    }

    private void next() {
        if(position < datas.size()) {
            viewPager.setCurrentItem(position +1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //뷰페이지 체인지 리스너
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            App.position = position;
            init();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(player != null && fromUser)
            player.seekTo(progress);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    class TimerThread extends Thread {

        @Override
        public void run() {
            //super.run();
            while(playStatus < STOP) {
                handler.sendEmptyMessage(PROGRESS_SET);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer(){

        @Override
        public void transformPage(View page, float position) {

            //현재 Page의 위치가 조금이라도 바뀔때마다 호출되는 메소드
            //첫번째 파라미터 : 현재 존재하는 View 객체들 중에서 위치가 변경되고 있는 View들
            //두번째 파라미터 : 각 View 들의 상대적 위치( 0.0 ~ 1.0 : 화면 하나의 백분율)

            //           1.현재 보여지는 Page의 위치가 0.0
            //           Page가 왼쪽으로 이동하면 값이 -됨. (완전 왼쪽으로 빠지면 -1.0)
            //           Page가 오른쪽으로 이동하면 값이 +됨. (완전 오른쪽으로 빠지면 1.0)

            //주의할 것은 현재 Page가 이동하면 동시에 옆에 있는 Page(View)도 이동함.
            //첫번째와 마지막 Page 일때는 총 2개의 View가 메모리에 만들어져 잇음.
            //나머지 Page가 보여질 때는 앞뒤로 2개의 View가 메모리에 만들어져 총 3개의 View가 instance 되어 있음.
            //ViewPager 한번에 1장의 Page를 보여준다면 최대 View는 3개까지만 만들어지며
            //나머지는 메모리에서 삭제됨.-리소스관리 차원.

            //position 값이 왼쪽, 오른쪽 이동방향에 따라 음수와 양수가 나오므로 절대값 Math.abs()으로 계산
            //position의 변동폭이 (-2.0 ~ +2.0) 사이이기에 부호 상관없이 (0.0~1.0)으로 변경폭 조절
            //주석으로 수학적 연산을 설명하기에는 한계가 있으니 코드를 보고 잘 생각해 보시기 바랍니다.
            float normalizedposition = Math.abs( 1 - Math.abs(position) );

            page.setAlpha(normalizedposition);  //View의 투명도 조절
            page.setScaleX(normalizedposition/2 + 0.5f); //View의 x축 크기조절
            page.setScaleY(normalizedposition/2 + 0.5f); //View의 y축 크기조절
            page.setRotationY(position * 80); //View의 Y축(세로축) 회전 각도

        }
    };



}