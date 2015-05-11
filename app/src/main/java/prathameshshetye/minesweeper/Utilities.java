package prathameshshetye.minesweeper;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by prathamesh on 5/9/15.
 */
public class Utilities {

    private static Utilities sInstance;
    private final int MaxGrid=10;
    private final int MinGrid=6;
    private final int MaxMines=25;
    private final int MinMines=5;

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

    public String getTimeString(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;

        return String.valueOf(minutes) + ":" + String.valueOf(seconds) + " minutes";
    }

    public void showSummaryDialog(Context context, int total_mines, int caught, int wrong, String strTimeSpent, boolean isVictory) {
        final Dialog dialog = new Dialog(context);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(context.getString(R.string.summary));
        dialog.setContentView(R.layout.summary);
        dialog.setCancelable(true);
        TextView recoveredMines = (TextView) dialog.findViewById(R.id.recovered_mines);
        recoveredMines.setText(String.valueOf(caught));
        TextView wrongMines = (TextView) dialog.findViewById(R.id.wrong_ones);
        wrongMines.setText(String.valueOf(wrong));
        TextView totalMines = (TextView) dialog.findViewById(R.id.allmines);
        totalMines.setText(String.valueOf(total_mines));
        TextView timeSpent = (TextView) dialog.findViewById(R.id.time_sp);
        timeSpent.setText(strTimeSpent);
        TextView banner = (TextView) dialog.findViewById(R.id.banner);
        if (isVictory) {
            banner.setText(context.getString(R.string.result_victory));
            banner.setTextColor(context.getResources().getColor(R.color.caught_mine));
        } else {
            banner.setText(context.getString(R.string.result_gameover));
            banner.setTextColor(context.getResources().getColor(R.color.banish_this_mine));
        }
        final int layoutTagKey = 1;
        RelativeLayout summaryLayout = (RelativeLayout) dialog.findViewById(R.id.summary_layout);
        summaryLayout.setTag(R.id.summary_layout, dialog);
        summaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dialog)view.getTag(R.id.summary_layout)).dismiss();
            }
        });
        dialog.show();
    }

    public void showHowToPlay(Context context) {
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.how_to_play);
        d.setTitle(context.getString(R.string.how_to));
        d.setCancelable(true);
        WebView webView = (WebView) d.findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/how_to_play.html");
        d.show();
    }

}
