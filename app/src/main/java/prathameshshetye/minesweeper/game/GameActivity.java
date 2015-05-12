package prathameshshetye.minesweeper.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import prathameshshetye.minesweeper.R;
import prathameshshetye.minesweeper.generic.Cell;
import prathameshshetye.minesweeper.generic.RecyclerItemClickListener;
import prathameshshetye.minesweeper.generic.Utilities;
import prathameshshetye.minesweeper.score.Score;
import prathameshshetye.minesweeper.score.ScoreDatabaseHelper;

public class GameActivity extends AppCompatActivity {

    private final String TAG = "Sweeper";
    private int N=10;
    private int M=20;
    private int C=3;
    private long mElapsedTime;
    private String mFinalChronoText;
    private boolean mIsVictorious;
    private RecyclerView mRecView;
    private RecycledCellsAdapter mAdapter;
    private Toolbar mToolbar;
    private TextView mScore;
    private TextView mInfo;
    private ImageButton mValidate;
    private Button mReset;
    private Button mCheat;
    private Button mSummary;
    private Toast mToast;
    private CheaterTask mCheater;
    private MenuItem mMenuTimer;
    private Chronometer mChronoTimer;
    private Cell[] mCells;
    private int mMineRecoveryCount;
    private int mMineErrorCount;
    private int[][] mMatrix;
    private boolean mLoadSavedGame;
    private boolean mSaveGame;
    private enum ForDialog {
        doCheat,
        doReset,
        doNothing
    }
    public enum PlayState {
        start,
        inPlay,
        gameOver,
        victory,
        validation_pending,
        cheat
    }
    public static PlayState sState;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenuTimer = menu.getItem(0);
        mMenuTimer.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                Utilities.getInstance().showHowToPlay(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadSavedGame = getIntent().getBooleanExtra(Utilities.SAVED_GAME, false);
        setContentView(R.layout.activity_main);
        int [] val = Utilities.getInstance().getSavedValues(this);
        N = val[0];
        M = val[1];
        mCells = new Cell[N*N];
        mMineRecoveryCount = 0;
        mMineErrorCount = 0;

        mChronoTimer = (Chronometer) findViewById(R.id.mytimer);
        mRecView = (RecyclerView) findViewById(R.id.recycleCells);
        mScore = (TextView) findViewById(R.id.score);
        mInfo = (TextView) findViewById(R.id.info);
        mToolbar = (Toolbar) findViewById(R.id.top_toolbar);
        mToolbar.setTitle(R.string.app_name);
        mReset = (Button) findViewById(R.id.btn_reset);
        mCheat = (Button) findViewById(R.id.btn_cheat);
        mSummary = (Button) findViewById(R.id.btn_summary);
        mValidate = (ImageButton) findViewById(R.id.btn_validate);

        sState = PlayState.start;
        setSupportActionBar(mToolbar);
        setupMineField();

        mElapsedTime = 0;
        mChronoTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (mMenuTimer != null) {
                    mMenuTimer.setVisible(true);
                    mMenuTimer.setTitle(chronometer.getText());
                }
                mElapsedTime++;
            }
        });

        mRecView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                        Log.d(TAG, "Position = " + position + " & is it a MINE? " + mCells[position].isMine());
                        if (sState == PlayState.cheat) {
                            return;
                        }
                        if (checkIfGameOver()) {
                            return;
                        }
                        if (mCells[position].isMineRecovered()
                                || mCells[position].isMarkedAsMine()
                                || mCells[position].isRevealed()) {
                            if (mCells[position].isMineRecovered()) {
                                mCells[position].setMineRecovered(false);
                            }
                            if (mCells[position].isMarkedAsMine()) {
                                mCells[position].setIsMarkedAsMine(false);
                                mMineErrorCount--;
                            }
                            if (mCells[position].isRevealed()) {
                                return;
                            }
                            mMineRecoveryCount--;
                        } else {
                            if (checkIfValidationPending()) {
                                return;
                            }
                            mMineRecoveryCount++;
                            if (mCells[position].isMine()) {
                                mCells[position].setMineRecovered(true);
                            } else {
                                mCells[position].setIsMarkedAsMine(true);
                                mMineErrorCount++;
                            }
                        }
                        mScore.setText(getString(R.string.score) + " : " + String.valueOf(mMineRecoveryCount));
                        if (M == mMineRecoveryCount) {
                            sState = PlayState.validation_pending;
                        } else {
                            sState = PlayState.inPlay;
                        }
                        setAnnouncement();
                        refreshCells();
                        return;
                    }

                    @Override
                    public void onItemClick(View view, int position) {
                        if (sState == PlayState.cheat) {
                            return;
                        }
                        if (checkIfGameOver()
                                || checkIfValidationPending()
                                || mCells[position].isMarkedAsMine()
                                || mCells[position].isMineRecovered()) {
                            return;
                        }
                        sState = PlayState.inPlay;
                        revealCells(position);
                        setAnnouncement();
                        refreshCells();
                    }
                }));
    }

    @Override
    protected void onStop() {
        if (!Utilities.getInstance().anyPriorSavedGame(this) && mSaveGame) {
            startSaveGameTask(N, mCells, false);
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSaveGame = true;
    }

    @Override
    public void onBackPressed() {
        mSaveGame = false;
        switch(sState) {
            case inPlay:
                AlertDialog ad = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(getString(R.string.confirm_exit))
                        .setMessage(getString(R.string.confirm_exit_message))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finishAfterTransition();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                ad.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.save_game), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        startSaveGameTask(N, mCells, true);
                    }
                });
                ad.show();
                break;
            case cheat:
                return;
            default:
                super.onBackPressed();
        }
    }

    private void setupMineField() {
        mElapsedTime = 0;
        setButtonVisibilities(false);
        if (mLoadSavedGame) {
            new LoadSavedGameTask(this).execute();
        } else {
            new LoadMineFieldTask(this).execute();
        }
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

    private void revealCells(int position) {
        if (mCells[position].isMine()) {
            sState = PlayState.gameOver;
            mCells[position].setCausedExplosion(true);
            mChronoTimer.stop();
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
        switch(sState) {
            case victory:
                mIsVictorious = true;
                createGameSummary(mIsVictorious);
                break;
            case gameOver:
                mIsVictorious = false;
                mInfo.setText(getString(R.string.gameover) + " : " + String.valueOf(mMineErrorCount));
                mScore.setText(getString(R.string.score) + " : " + String.valueOf(mMineRecoveryCount-mMineErrorCount));
                createGameSummary(mIsVictorious);
                break;
            case validation_pending:
                announceToast(getString(R.string.need_validation));
                break;
        }
    }

    private void refreshCells() {
        mAdapter = new RecycledCellsAdapter(mCells, this);
        mRecView.setAdapter(mAdapter);
        mRecView.setHasFixedSize(true);
        mRecView.setLayoutManager(new GridLayoutManager(this, N, GridLayoutManager.VERTICAL, false));
    }

    private class CheaterTask extends AsyncTask<Void, Void, Void> {
        int mTimer;
        PlayState mOldState;

        CheaterTask(int timer) {
            mTimer = timer;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mOldState = sState;
            sState = PlayState.cheat;
            announceToast(getString(R.string.cheater_announce)
                    + String.valueOf(mTimer)
                    + getString(R.string.cheater_announce_2));
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(mTimer > 0) {
                try {
                    publishProgress();
                    Thread.sleep(1000);
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
            announceToast(getString(R.string.cheater_announce)
                    + String.valueOf(mTimer)
                    + getString(R.string.cheater_announce_2));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sState = mOldState;
            refreshCells();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            sState = mOldState;
            refreshCells();
        }
    }

    private void showAlertDialog(String title, String message, boolean negativeButton, final ForDialog diagBehavior) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (diagBehavior) {
                            case doReset:
                                setupMineField();
                                break;
                            case doCheat:
                                mCheater = new CheaterTask(C);
                                mCheater.execute();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        if (negativeButton) {
            ad.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        ad.show();
    }

    private boolean checkIfGameOver() {
        if (sState == PlayState.gameOver
                || sState == PlayState.victory) {
            showAlertDialog(getString(R.string.done_here), getString(R.string.resetting), false, ForDialog.doReset);
            return true;
        }
        return false;
    }

    private boolean checkIfValidationPending() {
        if (sState == PlayState.validation_pending) {
            showAlertDialog(getString(R.string.wait), getString(R.string.need_validation), false, ForDialog.doNothing);
            return true;
        }
        return false;
    }

    private void showLegendDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.legend);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void announceToast(String toast) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, toast, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void OnButtonClick(View v) {
        if (sState == PlayState.cheat) {
            return;
        }
        if (v.getTag().equals(getString(R.string.cheat))) {
            if (checkIfGameOver()) {
                return;
            }
            showAlertDialog(getString(R.string.cheater_head), getString(R.string.cheater_body), true, ForDialog.doCheat);
        }
        if (v.getTag().equals(getString(R.string.validate))) {
            if (checkIfGameOver()) {
                return;
            }
            if (mMineRecoveryCount == M) {
                if (mMineErrorCount == 0) {
                    sState = PlayState.victory;
                } else {
                    sState = PlayState.gameOver;
                }
                refreshCells();
                setAnnouncement();
            } else {
                showAlertDialog(getString(R.string.wait),
                        getString(R.string.cant_validate_1)
                                + String.valueOf(M - mMineRecoveryCount)
                                + getString(R.string.cant_validate_2), false, ForDialog.doNothing);
            }
        }
        if (v.getTag().equals(getString(R.string.reset))) {
            if (sState == PlayState.inPlay) {
                showAlertDialog(getString(R.string.giving_up), getString(R.string.all_lost), true, ForDialog.doReset);
            } else {
                setupMineField();
            }
        }
        if (v.getTag().equals(getString(R.string.summary))) {

            Utilities.getInstance().showSummaryDialog(this, M,
                    mMineRecoveryCount - mMineErrorCount, mMineErrorCount,
                    mFinalChronoText, mIsVictorious);
        }
    }

    private class LoadMineFieldTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog mPDiag;
        Context mContext;

        LoadMineFieldTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPDiag = new ProgressDialog(mContext);
            mPDiag.setCancelable(false);
            mPDiag.setMessage(getString(R.string.loading));
            mPDiag.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mPDiag != null) {
                mPDiag.dismiss();
                mPDiag = null;
            }
            refreshCells();
            sState = PlayState.start;
            mMineRecoveryCount=0;
            mMineErrorCount=0;
            mScore.setText(getString(R.string.score) + " : " + String.valueOf(mMineRecoveryCount));
            mInfo.setText(getString(R.string.info) + " : " + M);
            setAnnouncement();
            mElapsedTime = 0;
            mFinalChronoText="00:00";
            mChronoTimer.setBase(SystemClock.elapsedRealtime());
            mChronoTimer.start();
        }
    }

    private void setButtonVisibilities(boolean showSummary) {
        if (showSummary) {
            if (mIsVictorious) {
                mSummary.setText(getString(R.string.result_victory) + " " + getString(R.string.summary));
            } else {
                mSummary.setText(getString(R.string.result_gameover) + " " + getString(R.string.summary));
            }
            mSummary.setVisibility(View.VISIBLE);
            mCheat.setVisibility(View.GONE);
            mValidate.setVisibility(View.GONE);
        } else {
            mSummary.setVisibility(View.GONE);
            mCheat.setVisibility(View.VISIBLE);
            mValidate.setVisibility(View.VISIBLE);
        }
    }

    private void createGameSummary(boolean isVictorious) {
        if (mChronoTimer != null) {
            mChronoTimer.stop();
            mFinalChronoText = mChronoTimer.getText().toString();
            mElapsedTime = (SystemClock.elapsedRealtime() - mChronoTimer.getBase())/1000;
        }
        Utilities.getInstance().showSummaryDialog(this, M,
                mMineRecoveryCount - mMineErrorCount, mMineErrorCount,
                mFinalChronoText, isVictorious);
        setButtonVisibilities(true);
        if (isVictorious) {
            ScoreDatabaseHelper.getInstance(this).saveScore(new Score(
                    (int) mElapsedTime,
                    N,
                    M,
                    System.currentTimeMillis(),
                    ""
            ));
            setResult(RESULT_OK);
        }
    }

    private class LoadSavedGameTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;
        private ProgressDialog mPDiag;
        private int mElapse;

        LoadSavedGameTask(Context context) {
            this.mContext = context;
            mElapse = 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPDiag = new ProgressDialog(mContext);
            mPDiag.setCancelable(false);
            mPDiag.setMessage(getString(R.string.loading));
            mPDiag.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mCells = Utilities.getInstance().retrieveSavedGame(mContext);
            Bundle b = Utilities.getInstance().getSavedProperties(mContext);
            N = b.getInt(Utilities.SP_KEY_GRID);
            M = b.getInt(Utilities.SP_KEY_MINES);
            mMineRecoveryCount = b.getInt(Utilities.SP_KEY_FOUND_MINES);
            mMineErrorCount = b.getInt(Utilities.SP_KEY_MARKED_MINES);
            mElapse = b.getInt(Utilities.SP_KEY_TIME);
            String state = b.getString(Utilities.SP_KEY_STATE);
            PlayState[] values = GameActivity.PlayState.values();
            for(int i=0; i<values.length; i++) {
                if (state.equals(values[i].name())) {
                    sState = values[i];
                    break;
                } else {
                    sState = PlayState.start;
                }
            }
            prepareNeighbours();
            clearSavedGame();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mLoadSavedGame=false;
            if (mPDiag != null) {
                mPDiag.dismiss();
                mPDiag = null;
            }
            mScore.setText(getString(R.string.score) + " : " + String.valueOf(mMineRecoveryCount));
            mInfo.setText(getString(R.string.info) + " : " + M);
            setAnnouncement();
            mChronoTimer.setBase(SystemClock.elapsedRealtime() - mElapse);
            mChronoTimer.start();
            refreshCells();
        }
    }

    private void startSaveGameTask(int grid, Cell[] cells, boolean noDiag) {
        int elapsedTime = 0;
        if (mChronoTimer != null) {
            mChronoTimer.stop();
            mFinalChronoText = mChronoTimer.getText().toString();
            elapsedTime = (int)(SystemClock.elapsedRealtime() - mChronoTimer.getBase());
        }
        new SaveGameTask(this, grid, cells, elapsedTime, noDiag).execute();
    }

    private class SaveGameTask extends AsyncTask<Void, Void, Void> {
        private int mGridN;
        private Cell[] mCells;
        private Context mContext;
        private int mTime;
        private boolean mShowDiag;
        private ProgressDialog mPDiag;

        public SaveGameTask(Context context, int grid, Cell[] cells, int elapsedTime, boolean noDiag) {
            this.mContext = context;
            this.mCells = cells;
            this.mGridN = grid;
            this.mTime = elapsedTime;
            this.mShowDiag = noDiag;
            mPDiag = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mShowDiag) {
                mPDiag = new ProgressDialog(mContext);
                mPDiag.setCancelable(false);
                mPDiag.setMessage(getString(R.string.saving));
                mPDiag.show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Bundle b = new Bundle();
            b.putInt(Utilities.SP_KEY_GRID, N);
            b.putInt(Utilities.SP_KEY_MINES, M);
            b.putInt(Utilities.SP_KEY_FOUND_MINES, mMineRecoveryCount);
            b.putInt(Utilities.SP_KEY_MARKED_MINES, mMineErrorCount);
            b.putInt(Utilities.SP_KEY_TIME, mTime);
            b.putString(Utilities.SP_KEY_STATE, sState.name());
            Utilities.getInstance().saveGame(mContext, this.mCells, b);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mPDiag != null) {
                mPDiag.dismiss();
                mPDiag = null;
            }
            finishAfterTransition();
        }
    }

    private void clearSavedGame() {
        Utilities.getInstance().clearSavedGame(this);
    }
}
