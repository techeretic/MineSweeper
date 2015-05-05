package prathameshshetye.minesweeper;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by p.shetye on 5/5/15.
 */
public class CellAdapter extends RecyclerView.Adapter<CellAdapter.ViewHolder>{

    //List<Cell> mCells;
    Cell[] mCells;

    CellAdapter(Cell[] cells) {
        mCells = cells;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mine_cell, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mCells[position].isMine()) {
            holder.mCell.setBackgroundColor(Color.RED);
            holder.mCellData.setText("M");
        } else {
            holder.mCellData.setText(String.valueOf(position));
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
}
