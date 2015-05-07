package prathameshshetye.minesweeper;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by p.shetye on 5/7/15.
 */
public class GridAdapter extends BaseAdapter {

    public static final int N=MainActivity.N;
    public static final int M=MainActivity.M;
    private final String TAG = "Sweeper";
    public enum PlayState {
        start,
        inPlay,
        gameOver,
        victory,
        cheat;
    }

    public static PlayState sState = PlayState.start;
    Cell[] mCells;

    GridAdapter(Cell[] cells) {
        mCells = cells;
    }

    public void setCells(Cell[] cells) {
        mCells = cells;
    }

    @Override
    public int getCount() {
        return mCells.length;
    }

    @Override
    public Cell getItem(int position) {
        return mCells[position];
    }

    @Override
    public long getItemId(int position) {
        return position/MainActivity.N;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mine_cell, parent, false);
        ImageView cell;
        TextView cellData;
        cell = (ImageView) v.findViewById(R.id.cell);
        cellData = (TextView) v.findViewById(R.id.cellData);

        if (sState == PlayState.start) {
            return v;
        }
        if (sState == PlayState.gameOver ||
                sState == PlayState.victory ||
                sState == PlayState.cheat) {
            if (mCells[position].isMine()) {
                cell.setBackgroundColor(Color.RED);
                cellData.setText("M");
            } else {
                if (mCells[position].getMineCount() != 0) {
                    cellData.setText(String.valueOf(mCells[position].getMineCount()));
                } else {
                    cellData.setText("-");
                }
            }
            return v;
        }
        if (mCells[position].isMineRecovered()) {
            cell.setBackgroundColor(Color.BLUE);
            cellData.setText("M");
            return v;
        }
        if (mCells[position].isRevealed()) {
            if (!mCells[position].isMine()) {
                if (mCells[position].getMineCount() != 0) {
                    cellData.setText(String.valueOf(mCells[position].getMineCount()));
                } else {
                    cellData.setText("-");
                }
                if (!mCells[position].isAnimated()) {
                    mCells[position].setIsAnimated(true);
                    Animation slideInTop = AnimationUtils.loadAnimation(parent.getContext(),
                            R.anim.abc_fade_in);
                    slideInTop.setDuration(750);
                    v.setAnimation(slideInTop);
                    v.animate();
                }
                return v;
            }
        }
        return v;
    }
}
