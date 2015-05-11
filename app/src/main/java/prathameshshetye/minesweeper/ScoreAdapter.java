package prathameshshetye.minesweeper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by p.shetye on 5/11/15.
 */
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    List<Score> mScores;
    Context mContext;

    ScoreAdapter(List<Score> scores) {
        mScores = scores;
        Collections.sort(mScores, new Comparator<Score>() {
            @Override
            public int compare(Score lhs, Score rhs) {
                if (lhs.getTimeTaken() > rhs.getTimeTaken()) {
                    return 1;
                }
                if (lhs.getTimeTaken() < rhs.getTimeTaken()) {
                    return -1;
                }
                return 0;
            }
        });
        Log.d("TAG","mScores.size = " + mScores.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.scores_item, parent,
                false);
        mContext = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int timeTaken = mScores.get(position).getTimeTaken();
        String strTimeTaken = "";
        if (timeTaken < 60) {
            strTimeTaken = String.format("%02d" + "%s", timeTaken % 60,
                    mContext.getString(R.string.append_seconds));
        } else {
            strTimeTaken = String.format("%02d" + "%s" + "%02d" + "%s",
                    timeTaken / 60, mContext.getString(R.string.append_minutes),
                    timeTaken % 60, mContext.getString(R.string.append_seconds));
        }
        holder.mTimeElapsed.setText(mContext.getString(R.string.completed_in) + strTimeTaken);
        holder.mDescrip.setText(mScores.get(position).getDescrip());
        holder.mMines.setText(String.valueOf(mScores.get(position).getMines()) + mContext.getString(R.string.cant_validate_2));
        int gridLength = mScores.get(position).getCells();
        holder.mGridSize.setText(String.valueOf(gridLength) + "x" + String.valueOf(gridLength) + mContext.getString(R.string.append_grid));
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(mScores.get(position).getWhen());
        holder.mWhen.setText(sdf.format(resultdate));
    }

    @Override
    public int getItemCount() {
        return mScores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTimeElapsed;
        TextView mGridSize;
        TextView mMines;
        TextView mDescrip;
        TextView mWhen;
        ViewHolder(View v) {
            super(v);

            mTimeElapsed = (TextView) v.findViewById(R.id.time_taken);
            mGridSize = (TextView) v.findViewById(R.id.grid);
            mMines = (TextView) v.findViewById(R.id.mines);
            mDescrip = (TextView) v.findViewById(R.id.descrip);
            mWhen = (TextView) v.findViewById(R.id.when);
        }
    }
}
