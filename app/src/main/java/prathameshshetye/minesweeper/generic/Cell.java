package prathameshshetye.minesweeper.generic;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;

/**
 * Created by p.shetye on 5/5/15.
 */
public class Cell {
    @SerializedName("number")
    private int mNum;

    @SerializedName("IsMine")
    private boolean mIsMine;

    @SerializedName("IsAnimated")
    private boolean mIsAnimated;

    @SerializedName("IsRevealed")
    private boolean mIsRevealed;

    @SerializedName("IsMineRecovered")
    private boolean mMineRecovered;

    @SerializedName("IsMarkedAsMine")
    private boolean mIsMarkedAsMine;

    @SerializedName("HasCausedExplosion")
    private boolean mCausedExplosion;

    private HashSet<Integer> mNeighbours;

    @SerializedName("MinesCount")
    private int mMineCount;

    public Cell(int num, boolean isMine) {
        mNum = num;
        mIsMine = isMine;
        mIsAnimated = false;
        mIsRevealed = false;
        mIsMarkedAsMine = false;
        mMineRecovered = false;
        mCausedExplosion = false;
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

    public boolean didCauseExplosion() {
        return mCausedExplosion;
    }

    public void setCausedExplosion(boolean mCausedExplosion) {
        this.mCausedExplosion = mCausedExplosion;
    }

    public void addToNeighbours(int val) {
        if (!mNeighbours.contains(val))
            mNeighbours.add(val);
    }

    public static Cell fromJSONString(String json) {
        Cell c = new Gson().fromJson(json, Cell.class);
        c.mNeighbours = new HashSet<>();
        return c;
    }

    public String toJSONString() {
        return new Gson().toJson(this);
    }

}
