package prathameshshetye.minesweeper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "Sweeper";
    public static final int N=8;
    public static final int M=10;
    private RecyclerView mRecView;
    private TextView mScore;
    private CellAdapter mAdapter;
    private Cell[] mCells = new Cell[N*N];
    private int mMineRecoveryCount;
    private int[][] mMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMineRecoveryCount = 0;

        mRecView = (RecyclerView) findViewById(R.id.RecyclerView);
        mScore = (TextView) findViewById(R.id.score);

        setupMineField();
        mRecView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (CellAdapter.sState == CellAdapter.PlayState.gameOver) {
                    return;
                }
                CellAdapter.sState = CellAdapter.PlayState.inPlay;
                revealCells(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, "Position = " + position + " & is it a MINE? " + mCells[position].isMine());
                if (mCells[position].isMineRecovered()) {
                    return;
                }
                if (mCells[position].isMine()) {
                    mMineRecoveryCount++;
                    mCells[position].setIsClicked(true);
                    mCells[position].setMineRecovered(true);
                    mAdapter.notifyDataSetChanged();
                    updateScore();
                }
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
            setupMineField();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupMineField() {
        Random rand = new Random();
        for(int i=0; i<(N*N); i++) {
            mCells[i] = new Cell(i, false);
        }

        for(int i=0; i<M; i++) {
            int r = rand.nextInt(N*N);
            if (mCells[r].isMine()) {
                i--;
            } else {
                mCells[r].setIsMine(true);
            }
        }
        prepareNeighbours();
        prepareMineCount();
        mAdapter = new CellAdapter(mCells);
        mRecView.setAdapter(mAdapter);
        mRecView.setLayoutManager(new GridLayoutManager(this, 8, GridLayoutManager.VERTICAL, false));
        CellAdapter.sState = CellAdapter.PlayState.start;
        mScore.setText("Mines Recovered : 0");
        mMineRecoveryCount=0;
    }

    private void prepareNeighbours() {
        mMatrix = new int [N][N];
        int k=0;
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                mMatrix[i][j]=mCells[k].getNum();
                k++;
            }
        }
        //Find Neighbours
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                if ((i-1) >= 0) {
                    if ((j-1) >= 0) {
                        mCells[mMatrix[i][j]].addToNeighbours(mMatrix[i-1][j-1]);
                    }
                    mCells[mMatrix[i][j]].addToNeighbours(mMatrix[i-1][j]);
                    if ((j+1)<N) {
                        mCells[mMatrix[i][j]].addToNeighbours(mMatrix[i-1][j+1]);
                    }
                }
                if ((j-1) >= 0) {
                    mCells[mMatrix[i][j]].addToNeighbours(mMatrix[i][j-1]);
                    if ((i+1) < N) {
                        mCells[mMatrix[i][j]].addToNeighbours(mMatrix[i+1][j-1]);
                    }
                }
                if ((i+1) < N) {
                    if ((j+1) < N) {
                        mCells[mMatrix[i][j]].addToNeighbours(mMatrix[i+1][j+1]);
                    }
                    mCells[mMatrix[i][j]].addToNeighbours(mMatrix[i+1][j]);
                }
                if ((j+1) < N) {
                    mCells[mMatrix[i][j]].addToNeighbours(mMatrix[i][j+1]);
                }
            }
        }
    }

    private void prepareMineCount() {
        int sum;
        for (int i=0; i<N*N; i++) {
            sum = 0;
            if (mCells[i].isMine()) {
                mCells[i].setMineCount(-1);
                continue;
            }
            for(Integer j : mCells[i].getNeighbours()) {
                if (mCells[j].isMine()) {
                    sum++;
                }
            }
            mCells[i].setMineCount(sum);
        }
    }

    private void updateScore() {
        mScore.setText("Mines Recovered : " + String.valueOf(mMineRecoveryCount));
    }

    private void revealCells(int position) {
        if (mCells[position].isMine()) {
            CellAdapter.sState = CellAdapter.PlayState.gameOver;
            return;
        }
        mCells[position].setIsRevealed(true);
        if (mCells[position].getMineCount() == 0) {
            checkNReveal(position, 0);
        }
    }

    private void checkNReveal(int pos, int count) {
        int i=pos/N;
        int j=pos%N;

        // TOP LEFT
        revealCell(i-1,j-1,count);

        //TOP
        revealCell(i,j-1,count);

        //TOP RIGHT
        revealCell(i+1,j-1,count);

        //LEFT
        revealCell(i-1,j,count);

        //RIGHT
        revealCell(i+1,j,count);

        //BOTTOM LEFT
        revealCell(i-1,j+1,count);

        //BOTTOM
        revealCell(i,j+1,count);

        //BOTTOM RIGHT
        revealCell(i+1,j+1,count);
    }

    private void revealCell(int k, int l, int count) {
        if (count < 0 || count > 0) {
            return;
        }
        if (k >= 0 && k < N && l >= 0 && l < N) {
            if (mCells[mMatrix[k][l]].isMine()) {
                return;
            }
            if (!mCells[mMatrix[k][l]].isRevealed()) {
                Log.d(TAG, "Revealing : " + k+ ","+l+" count = "+mCells[mMatrix[k][l]].getMineCount());
                mCells[mMatrix[k][l]].setIsRevealed(true);
                checkNReveal(mMatrix[k][l], mCells[mMatrix[k][l]].getMineCount());
            }
        }
    }
}
