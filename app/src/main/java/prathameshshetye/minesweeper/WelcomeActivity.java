package prathameshshetye.minesweeper;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import prathameshshetye.minesweeper.game.GameActivity;
import prathameshshetye.minesweeper.generic.Utilities;
import prathameshshetye.minesweeper.score.ScoreDatabaseHelper;
import prathameshshetye.minesweeper.score.Scores;


public class WelcomeActivity extends AppCompatActivity {

    private Button mNewGame;
    private Button mScores;
    private Button mLoadSave;
    private boolean mDoShowScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mDoShowScore = ScoreDatabaseHelper.getInstance(this).isTableEmpty();
        mNewGame = (Button) findViewById(R.id.btn_new_game);
        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame(false);
            }
        });
        mScores = (Button) findViewById(R.id.btn_score);
        mScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScores();
            }
        });
        mLoadSave = (Button) findViewById(R.id.btn_load_save);
        mLoadSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame(true);
            }
        });
        Button exit = (Button) findViewById(R.id.btn_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });
        Button settings = (Button) findViewById(R.id.btn_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSettingsDialog();
            }
        });
        Button howTo = (Button) findViewById(R.id.btn_how_to);
        howTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHowToPlay();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showScoreButton(mDoShowScore);
        showLoadGameButton(Utilities.getInstance().anyPriorSavedGame(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utilities.RESULT_SCORE) {
            if (resultCode == RESULT_OK) {
                mDoShowScore = true;
                showScoreButton(mDoShowScore);
            }
        }
    }

    private void startSettingsDialog() {
        Utilities.getInstance().showSettingsDialog(this);
    }

    private void showHowToPlay() {
        Utilities.getInstance().showHowToPlay(this);
    }

    private void startNewGame(boolean isSavedGame) {
        ActivityOptionsCompat options;
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, mNewGame, mNewGame.getTransitionName());

        Intent intent = new Intent(this, GameActivity.class);
        if (isSavedGame) {
            intent.putExtra(Utilities.SAVED_GAME, true);
        } else {
            Utilities.getInstance().clearSavedGame(this);
        }
        ActivityCompat.startActivityForResult(this, intent, Utilities.RESULT_SCORE, options.toBundle());
    }

    private void showScores() {
        ActivityOptionsCompat options;
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, mScores, mScores.getTransitionName());

        Intent intent = new Intent(this, Scores.class);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    private void showScoreButton(boolean dontShow) {
        if (dontShow) {
            mScores.setVisibility(View.GONE);
        } else {
            mScores.setVisibility(View.VISIBLE);
        }
    }

    private void showLoadGameButton(boolean doShow) {
        if (doShow) {
            mLoadSave.setVisibility(View.VISIBLE);
        } else {
            mLoadSave.setVisibility(View.GONE);
        }
    }
}
