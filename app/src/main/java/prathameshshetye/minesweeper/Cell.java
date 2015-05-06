package prathameshshetye.minesweeper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by p.shetye on 5/5/15.
 */
public class Cell {
    private int mNum;
    private boolean mIsMine;
    private boolean mIsClicked;
    private boolean mIsRevealed;
    private boolean mMineRecovered;
    private HashSet<Integer> mNeighbours;
    private int mMineCount;
    private static final int N = MainActivity.N;
    private static final int M = MainActivity.M;

    Cell(int num, boolean isMine) {
        mNum = num;
        mIsMine = isMine;
        mIsClicked = false;
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

    public boolean isClicked() {
        return mIsClicked;
    }

    public void setIsClicked(boolean mIsClicked) {
        this.mIsClicked = mIsClicked;
    }

    public boolean isMineRecovered() {
        return mMineRecovered;
    }

    public void setMineRecovered(boolean mMineRecovered) {
        this.mMineRecovered = mMineRecovered;
    }

    public void addToNeighbours(int val) {
        if (!mNeighbours.contains(val))
            mNeighbours.add(val);
    }
}
