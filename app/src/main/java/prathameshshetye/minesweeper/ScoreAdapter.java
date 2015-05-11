package prathameshshetye.minesweeper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by p.shetye on 5/11/15.
 */
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    List<Score> mScores;

    ScoreAdapter(List<Score> scores) {
        mScores = scores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.scores_item, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int timeTaken = mScores.get(position).getTimeTaken();
        String strTimeTaken = String.format("%02d"+"%s"+"%02d", timeTaken/60, ":", timeTaken%60);
        holder.mTimeElapsed.setText(strTimeTaken);
        holder.mDescrip.setText(mScores.get(position).getDescrip());
        holder.mMines.setText(String.valueOf(mScores.get(position).getMines()) + " Mines");

        StringBuffer buff = new StringBuffer();
        int gridLength = mScores.get(position).getCells();
        holder.mGridSize.setText(String.valueOf(gridLength) + "x" + String.valueOf(gridLength) + " Grid");
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
        ViewHolder(View v) {
            super(v);

            mTimeElapsed = (TextView) v.findViewById(R.id.time_taken);
            mGridSize = (TextView) v.findViewById(R.id.grid);
            mMines = (TextView) v.findViewById(R.id.mines);
            mDescrip = (TextView) v.findViewById(R.id.descrip);
        }
    }
}
