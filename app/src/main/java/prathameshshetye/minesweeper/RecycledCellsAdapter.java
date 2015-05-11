package prathameshshetye.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by prathamesh on 5/8/15.
 */
public class RecycledCellsAdapter extends RecyclerView.Adapter<RecycledCellsAdapter.ViewHolder> {

    private final String mCheaterUri = "@drawable/ic_mine_cheat.png";
    private final String mFlagMine = "@drawable/ic_flag_mine.png";
    private final String mGameOver = "@drawable/ic_mine_explode.png";

    private Cell[] mCells;
    private Context mContext;

    public RecycledCellsAdapter(Cell[] cells, Context context) {
        mCells = cells;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mine_cell, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (GameActivity.sState == GameActivity.PlayState.start) {
            return;
        }
        if (GameActivity.sState == GameActivity.PlayState.cheat) {
            if (mCells[position].isMine()) {
                holder.mCell.setImageDrawable(mContext.getDrawable(R.drawable.ic_mine_cheat));
                //holder.mCell.setBackgroundColor(mContext.getResources().getColor(R.color.banish_this_mine));
                holder.mCellData.setText("");
                Animation slideInTop = AnimationUtils.loadAnimation(mContext,
                        R.anim.abc_fade_in);
                slideInTop.setDuration(750);
                holder.mCell.setAnimation(slideInTop);
                holder.mCell.animate();
            }
            return;
        }
        if (mCells[position].didCauseExplosion()) {
            holder.mCell.setImageDrawable(mContext.getDrawable(R.drawable.ic_blast));
            holder.mCell.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light));
            holder.mCellData.setText("");
            return;
        }
        if (mCells[position].isMineRecovered() || mCells[position].isMarkedAsMine()) {
            if (mCells[position].isMarkedAsMine() && GameActivity.sState == GameActivity.PlayState.gameOver) {
                holder.mCell.setImageDrawable(mContext.getDrawable(R.drawable.ic_wrong_mine));
                //holder.mCell.setBackgroundColor(mContext.getResources().getColor(R.color.wrongly_accused_mine));
            } else {
                holder.mCell.setImageDrawable(mContext.getDrawable(R.drawable.ic_flag_mine));
                //holder.mCell.setBackgroundColor(mContext.getResources().getColor(R.color.caught_mine));
            }
            holder.mCell.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light));
            holder.mCellData.setText("");
            return;
        }
        if (mCells[position].isRevealed()) {
            if (!mCells[position].isMine()) {
                if (mCells[position].getMineCount() != 0) {
                    holder.mCellData.setText(String.valueOf(mCells[position].getMineCount()));
                    holder.mCellData.setTextColor(mContext.getResources().getColor(getColor(mCells[position].getMineCount())));
                }
                holder.mCell.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light));
                if (!mCells[position].isAnimated()) {
                    mCells[position].setIsAnimated(true);
                    Animation slideInTop = AnimationUtils.loadAnimation(mContext,
                            R.anim.abc_fade_in);
                    slideInTop.setDuration(750);
                    holder.mCell.setAnimation(slideInTop);
                    holder.mCell.animate();
                }
                return;
            }
        }
        if (GameActivity.sState == GameActivity.PlayState.gameOver ||
                GameActivity.sState == GameActivity.PlayState.victory) {
            if (mCells[position].isMine()) {
                holder.mCell.setImageDrawable(mContext.getDrawable(R.drawable.ic_shown_mine));
                holder.mCell.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light));
                //holder.mCell.setBackgroundColor(mContext.getResources().getColor(R.color.banish_this_mine));
                holder.mCellData.setText("");
            }
            /*else {
                if (mCells[position].getMineCount() != 0) {
                    holder.mCellData.setText(String.valueOf(mCells[position].getMineCount()));
                }
                holder.mCellData.setTextColor(mContext.getResources().getColor(getColor(mCells[position].getMineCount())));
                holder.mCell.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light));
            }*/
        }
    }

    @Override
    public int getItemCount() {
        return mCells.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mCell;
        TextView mCellData;
        public ViewHolder(View view) {
            super(view);
            mCell = (ImageView) view.findViewById(R.id.cell);
            mCellData = (TextView) view.findViewById(R.id.cellData);
        }
    }

    private int getColor(int count) {
        int color = R.color.secondary_text;
        switch(count) {
            case 1:
                color = R.color.some_blue;
                break;
            case 2:
                color = R.color.caught_mine;
                break;
            case 3:
                color = R.color.banish_this_mine;
                break;
            case 4:
                color = R.color.wrongly_accused_mine;
                break;
            case 5:
                color = R.color.some_pink;
        }
        return color;
    }
}
