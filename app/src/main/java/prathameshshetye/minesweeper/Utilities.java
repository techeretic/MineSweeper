package prathameshshetye.minesweeper;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by prathamesh on 5/9/15.
 */
public class Utilities {

    private static Utilities sInstance;
    private final int MaxGrid=12;
    private final int MinGrid=8;
    private final int MaxMines=30;
    private final int MinMines=10;

    private final int mDefaultMines = 10;
    private final int mDefaultGrid = 8;

    private int mSavedGrid;
    private int mSavedMine;

    public static final String spGrid = "grid";
    public static final String spMines = "mines";

    private TextView mTxtGridSize;
    private TextView mTxtMines;

    public static Utilities getInstance() {
        if (sInstance == null) {
            sInstance = new Utilities();
        }
        return sInstance;
    }

    public void showSettingsDialog(final Context context) {
        int [] vals = getSavedValues(context);
        mSavedGrid = vals[0];
        mSavedMine = vals[1];
        SeekBar gridSize;
        SeekBar mines;
        Button btnOk;
        Button btnCancel;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.settings);
        dialog.setCancelable(true);
        btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePrefs(context, mSavedGrid, mSavedMine);
                dialog.dismiss();
            }
        });
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        mTxtGridSize = (TextView) dialog.findViewById(R.id.grid_size);
        mTxtGridSize.setText(String.valueOf(mSavedGrid) + "x" + String.valueOf(mSavedGrid));
        gridSize = (SeekBar) dialog.findViewById(R.id.seekGridSize);
        gridSize.setMax(MaxGrid - MinGrid);
        gridSize.setProgress(mSavedGrid - MinGrid);
        gridSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTxtGridSize.setText(String.valueOf(i + MinGrid) + "x" + String.valueOf(i + MinGrid));
                mSavedGrid = i + MinGrid;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing Relevant
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Nothing Relevant
            }
        });
        mTxtMines = (TextView) dialog.findViewById(R.id.mine_count);
        mTxtMines.setText(String.valueOf(mSavedMine) + " mines");
        mines = (SeekBar) dialog.findViewById(R.id.seekMines);
        mines.setMax(MaxMines-MinMines);
        mines.setProgress(mSavedMine-MinMines);
        mines.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTxtMines.setText(String.valueOf(i+MinMines) + " mines");
                mSavedMine = i+MinMines;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing Relevant
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Nothing Relevant
            }
        });
        dialog.show();
    }

    public int[] getSavedValues(Context context) {
        boolean doSave = false;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int grids = sp.getInt(spGrid, -1);
        int mines = sp.getInt(spMines, -1);
        if (grids == -1) {
            grids = mDefaultGrid;
            doSave = true;
        }
        if (mines == -1) {
            mines = mDefaultMines;
            doSave = true;
        }
        if (doSave) {
            savePrefs(context, grids, mines);
        }
        return new int[] {grids, mines};
    }

    public void savePrefs(Context context, int grid, int mines) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(spGrid, grid).apply();
        sp.edit().putInt(spMines, mines).apply();
    }
}
