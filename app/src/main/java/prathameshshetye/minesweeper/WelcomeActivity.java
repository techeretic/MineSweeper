package prathameshshetye.minesweeper;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class WelcomeActivity extends AppCompatActivity {

    private Button mNewGame;

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
}
