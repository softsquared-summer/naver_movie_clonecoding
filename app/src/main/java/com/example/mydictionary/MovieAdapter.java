package com.example.mydictionary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class MovieAdapter extends BaseAdapter {

    private ArrayList<Movie> movieList = new ArrayList<>();
    LayoutInflater inflater;
     RequestManager mRequestManager;
    public MovieAdapter(ArrayList<Movie> mvList, Context context, RequestManager requestManager){
        movieList = mvList;
        inflater =LayoutInflater.from(context);
        mRequestManager = requestManager;
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int i) {
        return movieList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        final int pos = position;
        final Movie movie = movieList.get(pos);


        if(convertView==null){
            holder = new ViewHolder();
            convertView= inflater.inflate(R.layout.movie_layout,viewGroup,false);

            holder.lv_image = convertView.findViewById(R.id.movie_image);
            holder.tv_title = convertView.findViewById(R.id.movie_title);
            holder.tv_date = convertView.findViewById(R.id.movie_date);
            holder.tv_actor = convertView.findViewById(R.id.movie_actor);
            holder.tv_director = convertView.findViewById(R.id.movie_director);
            holder.pb_loading = convertView.findViewById(R.id.progress_loading);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        //if else 구문으로 view가 보일때만 가져오는 방식이라 효과적이다

        // 데이터 파싱

        mRequestManager.load(movie.getImage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.pb_loading.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.pb_loading.setVisibility(View.GONE);
                return false;
            }
        }).error(R.drawable.noimage).centerCrop().into(holder.lv_image);
        holder.tv_title.setText(movie.getTitle());
        holder.tv_director.setText(movie.getDirector());
        holder.tv_actor.setText(movie.getActor());
        holder.tv_date.setText(movie.getPubDate()+"");
        return convertView;
    }

    public class ViewHolder {
        ProgressBar pb_loading;
        ImageView lv_image;
        TextView tv_title;
        TextView tv_date;
        TextView tv_director;
        TextView tv_actor;
    }
}
