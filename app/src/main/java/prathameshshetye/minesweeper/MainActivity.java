package prathameshshetye.minesweeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Sweeper";
    public static final int N=8;
    public static final int M=10;
    private GridView mGridView;
    private Toolbar mToolbar;
    private TextView mScore;
    private TextView mInfo;
    private TextView mAnnouncement;
    private ImageView mValidate;
    private GridAdapter mAdapter;
    private Button mReset;
    private Button mCheat;
    private Cell[] mCells = new Cell[N*N];
    private int mMineRecoveryCount;
    private int mMineErrorCount;
    private int[][] mMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMineRecoveryCount = 0;
        mMineErrorCount = 0;

        mGridView = (GridView) findViewById(R.id.gridview);
        mScore = (TextView) findViewById(R.id.score);
        mInfo = (TextView) findViewById(R.id.info);
        mAnnouncement = (TextView) findViewById(R.id.announcement);
        mToolbar = (Toolbar) findViewById(R.id.top_toolbar);
        mToolbar.setTitle(R.string.app_name);
        mReset = (Button) findViewById(R.id.btn_reset);
        mCheat = (Button) findViewById(R.id.btn_cheat);
        mValidate = (ImageView) findViewById(R.id.btn_validate);

        mGridView.setNumColumns(N);
        setupMineField();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (GridAdapter.sState == GridAdapter.PlayState.gameOver) {
                    showToResetDialog();
                    return;
                }
                if (GridAdapter.sState == GridAdapter.PlayState.validation_pending) {
                    showToValidateDialog();
                    return;
                }
                if (mCells[position].isMarkedAsMine() || mCells[position].isMineRecovered()) {
                    return;
                }
                GridAdapter.sState = GridAdapter.PlayState.inPlay;
                revealCells(position);
                setAnnouncement();
                refreshCells();
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Position = " + position + " & is it a MINE? " + mCells[position].isMine());
                if (mCells[position].isMineRecovered()
                        || mCells[position].isMarkedAsMine()
                        || mCells[position].isRevealed()) {
                    if (GridAdapter.sState == GridAdapter.PlayState.gameOver) {
                        showToResetDialog();
                        return true;
                    }
                    if (mCells[position].isMineRecovered()) {
                        mCells[position].setMineRecovered(false);
                    }
                    if (mCells[position].isMarkedAsMine()) {
                        mCells[position].setIsMarkedAsMine(false);
                        mMineErrorCount--;
                    }
                    if (mCells[position].isRevealed()) {
                        return true;
                    }
                    mMineRecoveryCount--;
                } else {
                    if (GridAdapter.sState == GridAdapter.PlayState.validation_pending) {
                        showToValidateDialog();
                        return true;
                    }
                    mMineRecoveryCount++;
                    if (mCells[position].isMine()) {
                        mCells[position].setMineRecovered(true);
                    } else {
                        mCells[position].setIsMarkedAsMine(true);
                        mMineErrorCount++;
                    }
                }
                updateScore();
                if (M == mMineRecoveryCount) {
                    GridAdapter.sState = GridAdapter.PlayState.validation_pending;
                } else {
                    GridAdapter.sState = GridAdapter.PlayState.inPlay;
                }
                setAnnouncement();
                refreshCells();
                return true;
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GridAdapter.sState == GridAdapter.PlayState.inPlay) {
                    showResetDialog();
                } else {
                    setupMineField();
                }
            }
        });

        mValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GridAdapter.sState == GridAdapter.PlayState.gameOver) {
                    showToResetDialog();
                    return;
                }
                if (mMineRecoveryCount == M) {
                    if (mMineErrorCount == 0) {
                        GridAdapter.sState = GridAdapter.PlayState.victory;
                    } else {
                        GridAdapter.sState = GridAdapter.PlayState.gameOver;
                    }
                    refreshCells();
                    setAnnouncement();
                } else {
                    showIncompleteDialog();
                }
            }
        });

        mCheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheaterTask(5).execute();
            }
        });
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
        mAdapter = new GridAdapter(mCells);
        mGridView.setAdapter(mAdapter);
        GridAdapter.sState = GridAdapter.PlayState.start;
        mMineRecoveryCount=0;
        mMineErrorCount=0;
        mScore.setText(getString(R.string.score) + " : " + String.valueOf(mMineRecoveryCount));
        mInfo.setText(getString(R.string.info) + " : " + M);
        setAnnouncement();
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
        mScore.setText(getString(R.string.score) + " : " + String.valueOf(mMineRecoveryCount));
    }

    private void revealCells(int position) {
        if (mCells[position].isMine()) {
            GridAdapter.sState = GridAdapter.PlayState.gameOver;
            mInfo.setText(getString(R.string.gameover) + " : " + String.valueOf(mMineErrorCount));
            return;
        }
        mCells[position].setIsRevealed(true);
        checkNReveal(position, mCells[position].getMineCount());
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
                mCells[mMatrix[k][l]].setIsRevealed(true);
                checkNReveal(mMatrix[k][l], mCells[mMatrix[k][l]].getMineCount());
            }
        }
    }

    private void setAnnouncement() {
        switch(GridAdapter.sState) {
            case victory:
                mAnnouncement.setText(getString(R.string.result_victory));
                mAnnouncement.setTextColor(getResources().getColor(R.color.caught_mine));
                break;
            case gameOver:
                mAnnouncement.setText(getString(R.string.result_gameover));
                mAnnouncement.setTextColor(getResources().getColor(R.color.banish_this_mine));
                break;
            case validation_pending:
                mAnnouncement.setText(getString(R.string.need_validation));
                mAnnouncement.setTextColor(getResources().getColor(R.color.secondary_text));
                break;
            default:
                mAnnouncement.setText("");
                mAnnouncement.setTextColor(getResources().getColor(R.color.primary_text));
        }
    }

    private void showIncompleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.wait))
                .setMessage(getString(R.string.cant_validate_1) + " "
                        + String.valueOf(M-mMineRecoveryCount) + " "
                        + getString(R.string.cant_validate_2))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showToValidateDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.wait))
                .setMessage(getString(R.string.need_validation))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showToResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.done_here))
                .setMessage(getString(R.string.resetting))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setupMineField();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.giving_up))
                .setMessage(getString(R.string.all_lost))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setupMineField();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void refreshCells() {
        mAdapter = new GridAdapter(mCells);
        mGridView.setAdapter(mAdapter);
    }

    private class CheaterTask extends AsyncTask<Void, Void, Void> {
        int mTimer;
        GridAdapter.PlayState mOldState;

        CheaterTask(int timer) {
            mTimer = timer;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mOldState = GridAdapter.sState;
            GridAdapter.sState = GridAdapter.PlayState.cheat;
            Log.d(TAG, "About to start cheating");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "Cheating in progress");
            while(mTimer > 0) {
                try {
                    publishProgress();
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    Log.d(TAG, "Cant cheat anymore");
                }
                mTimer--;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            refreshCells();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            GridAdapter.sState = mOldState;
            refreshCells();
        }
    }
}
