package com.example.a.gomusic.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a.gomusic.MainActivity;
import com.example.a.gomusic.Object.Song;
import com.example.a.gomusic.R;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.myViewHolder> {
    private final Context context;
    private List<Song> songList;
    private MainActivity mainActivity;

    public SongListAdapter(Context mContext) {
        this.context = mContext;
    }


    @Override
    public SongListAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.song, parent, false);
        return new SongListAdapter.myViewHolder(view);
    }

    public void setArraylist(List<Song> songList1, MainActivity mainActivity2) {
        this.songList = songList1;
        this.mainActivity = mainActivity2;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(SongListAdapter.myViewHolder holder, int position) {
        final int j = position;
        holder.artistView.setText(songList.get(j).getMusic_Artist());
        holder.albumView.setText(songList.get(j).getMusic_Title());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("code:101", v.getVerticalScrollbarPosition() + "");
                Log.d("code:102", j + "");
                mainActivity.songPicked(j);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (songList == null) {
            return 0;
        } else
            return songList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView artistView, albumView;

        View mView;

        myViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            artistView = mView.findViewById(R.id.song_title_slide);
            albumView = mView.findViewById(R.id.song_artist);

        }
    }
}
