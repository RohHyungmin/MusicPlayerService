package com.hyugnmin.android.musicplayer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


/**
 * Created by besto on 2017-02-02.
 */
public class PlayerAdapter extends PagerAdapter{

    List<Music> datas;
    Context context;
    LayoutInflater inflater;

    public PlayerAdapter (List<Music>datas, Context context) {
        this.datas = datas;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datas.size();
    }


    //listView 의 getView와 같은 역할
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = inflater.inflate(R.layout.player_card_item, null);

        ImageView imageView2 = (ImageView) view.findViewById(R.id.imageView2);
        TextView textTitle = (TextView) view.findViewById(R.id.textTitle);
        TextView textArtist = (TextView) view.findViewById(R.id.textArtist);

        //실제 음악 data 가져오기
        Music music = datas.get(position);
        textTitle.setText(music.title);
        textArtist.setText(music.artist);

        Glide.with(context).load(music.album_image).placeholder(android.R.drawable.ic_menu_gallery).into(imageView2);

        //생성한 뷰를 컨테이너에 담아준다. 뷰 페이저를 생성한 최외곽 레이아웃 개념
        container.addView(view);

        return view;
    }
    //화면에서 사라진 뷰를 메모리에서 제거하기 위한 함수
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View)object);
    }
    @Override
    public boolean isViewFromObject (View view, Object object) {
        return view == object;
    }


}
