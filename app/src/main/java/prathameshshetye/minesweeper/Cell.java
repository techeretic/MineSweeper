package prathameshshetye.minesweeper;

import java.util.List;

/**
 * Created by p.shetye on 5/5/15.
 */
public class Cell {
    private int mNum;
    private boolean mIsMine;
    private int edge; //0 - Left, 1 - Right, -1 - NoEdge
    private Coordinate mCoordinate;
    private static final int N = MainActivity.N;
    private static final int M = MainActivity.M;

    Cell(int num, boolean isMine) {
        mNum = num;
        mIsMine = isMine;
        switch(num%N) {
            case 0: edge = 0;
                    break;
            case N-1: edge = 1;
                    break;
            default: edge = -1;
        }
    }

    public int getNum() {
        return mNum;
    }

    public boolean isMine() {
        return mIsMine;
    }

    public Coordinate getCoordinate() {
        return mCoordinate;
    }

    public void setIsMine(boolean mIsMine) {
        this.mIsMine = mIsMine;
    }

    public class Coordinate {
        int x,y;

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public String getSurroundings() {
        StringBuffer buff = new StringBuffer();
        if (isMine()) {
            buff.append("Mine");
        } else {
            int sum = 0, i=1;

            //top row
            while(i>-2) {
                sum = mNum-(N+i);
                if (sum < (N*N) && sum >= 0) {
                    buff.append(sum + ",");
                }
                i--;
            }
            buff.append("\n");
            //middle row
            if(mNum-1 < N*N && mNum-1 > 0) {
                if (mNum%N!=0) {
                    buff.append(mNum - 1 + ",");
                }
            }
            if(mNum+1 < N*N) {
                if (mNum%N!=(N-1)) {
                    buff.append(mNum + 1 + ",");
                }
            }
            buff.append("\n");
            sum=0;i=-1;
            //bottom row
            while(i<2) {
                sum = mNum+(N+i);
                if (sum < (N*N) && sum >= 0) {
                    buff.append(sum + ",");
                }
                i++;
            }
        }
        return buff.toString();
    }
}
