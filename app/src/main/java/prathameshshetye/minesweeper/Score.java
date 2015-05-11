package prathameshshetye.minesweeper;

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
}
