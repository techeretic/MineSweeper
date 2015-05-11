package prathameshshetye.minesweeper;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class WelcomeActivity extends AppCompatActivity {

    private Button mNewGame;
    private Button mScores;
    private boolean mDoShowScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mNewGame = (Button) findViewById(R.id.btn_new_game);
        mNewGame.setTransitionName("NewGame");
        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
        mScores = (Button) findViewById(R.id.btn_score);
        mScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScores();
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
        showScoreButton(ScoreDatabaseHelper.getInstance(this).isTableEmpty());
    }

    private void startSettingsDialog() {
        Utilities.getInstance().showSettingsDialog(this);
    }

    private void showHowToPlay() {
        Utilities.getInstance().showHowToPlay(this);
    }

    private void startNewGame() {
        ActivityOptionsCompat options;
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, mNewGame, "NewGame");

        Intent intent = new Intent(this, GameActivity.class);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    private void showScores() {
        ActivityOptionsCompat options;
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, mScores, "Scores");

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
}
