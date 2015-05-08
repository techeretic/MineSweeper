package prathameshshetye.minesweeper;

import java.util.HashSet;

/**
 * Created by p.shetye on 5/5/15.
 */
public class Cell {
    private int mNum;
    private boolean mIsMine;
    private boolean mIsAnimated;
    private boolean mIsRevealed;
    private boolean mMineRecovered;
    private boolean mIsMarkedAsMine;
    private HashSet<Integer> mNeighbours;
    private int mMineCount;
    private static final int N = GameActivity.N;
    private static final int M = GameActivity.M;

    Cell(int num, boolean isMine) {
        mNum = num;
        mIsMine = isMine;
        mIsAnimated = false;
        mNeighbours = new HashSet<>();
    }

    public int getNum() {
        return mNum;
    }

    public boolean isMine() {
        return mIsMine;
    }

    public void setIsMine(boolean mIsMine) {
        this.mIsMine = mIsMine;
    }

    public int getMineCount() {
        return mMineCount;
    }

    public void setMineCount(int mMineCount) {
        this.mMineCount = mMineCount;
    }

    public boolean isRevealed() {
        return mIsRevealed;
    }

    public void setIsRevealed(boolean mIsRevealed) {
        this.mIsRevealed = mIsRevealed;
    }

    public String getSurroundings() {
        StringBuffer buff = new StringBuffer();
        if (isMine()) {
            buff.append("Mine\n");
        }
        if (mNeighbours != null) {
            for(Integer i : mNeighbours) {
                buff.append(i + ",");
            }
        }
        return buff.toString();
    }

    public HashSet<Integer> getNeighbours() {
        return mNeighbours;
    }

    public boolean isAnimated() {
        return mIsAnimated;
    }

    public void setIsAnimated(boolean mIsAnimated) {
        this.mIsAnimated = mIsAnimated;
    }

    public boolean isMineRecovered() {
        return mMineRecovered;
    }

    public void setMineRecovered(boolean mMineRecovered) {
        this.mMineRecovered = mMineRecovered;
    }

    public boolean isMarkedAsMine() {
        return mIsMarkedAsMine;
    }

    public void setIsMarkedAsMine(boolean isMarkedAsMine) {
        this.mIsMarkedAsMine = isMarkedAsMine;
    }

    public void addToNeighbours(int val) {
        if (!mNeighbours.contains(val))
            mNeighbours.add(val);
    }
}
