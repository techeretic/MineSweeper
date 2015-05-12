package prathameshshetye.minesweeper.score;

import android.content.Context;

import prathameshshetye.minesweeper.R;

/**
 * Created by p.shetye on 5/11/15.
 */
public class Score {
    private int mId;
    private int mTimeTaken;
    private int mCells;
    private int mMines;
    private long mWhen;
    private String mDescrip;

    public Score(int mTimeTaken, int mCells, int mMines, long when, String mDescrip) {
        this.mTimeTaken = mTimeTaken;
        this.mCells = mCells;
        this.mMines = mMines;
        this.mWhen = when;
        this.mDescrip = mDescrip;
    }

    public Score(int id, int mTimeTaken, int mCells, int mMines, long when, String mDescrip) {
        this.mId = id;
        this.mTimeTaken = mTimeTaken;
        this.mCells = mCells;
        this.mMines = mMines;
        this.mWhen = when;
        this.mDescrip = mDescrip;
    }

    public int getTimeTaken() {
        return mTimeTaken;
    }

    public void setTimeTaken(int mTimeTaken) {
        this.mTimeTaken = mTimeTaken;
    }

    public int getCells() {
        return mCells;
    }

    public void setCells(int mCells) {
        this.mCells = mCells;
    }

    public int getMines() {
        return mMines;
    }

    public void setMines(int mMines) {
        this.mMines = mMines;
    }

    public String getDescrip() {
        return mDescrip;
    }

    public void setDescrip(String mDescrip) {
        this.mDescrip = mDescrip;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public long getWhen() {
        return mWhen;
    }

    public void setWhen(long mWhen) {
        this.mWhen = mWhen;
    }

    public String getTimeDescrip(Context context) {
        String strTimeTaken = "";
        if (mTimeTaken < 60) {
            strTimeTaken = String.format("%02d" + "%s", mTimeTaken % 60,
                    context.getString(R.string.append_seconds));
        } else {
            strTimeTaken = String.format("%02d" + "%s" + "%02d" + "%s",
                    mTimeTaken / 60, context.getString(R.string.append_minutes),
                    mTimeTaken % 60, context.getString(R.string.append_seconds));
        }
        return strTimeTaken;
    }

    public String toString(Context context) {
        return context.getString(R.string.share_1)
                + mMines
                + context.getString(R.string.share_2)
                + mCells + "X" + mCells
                + context.getString(R.string.share_3)
                + getTimeDescrip(context)
                + context.getString(R.string.share_4);
    }
}
