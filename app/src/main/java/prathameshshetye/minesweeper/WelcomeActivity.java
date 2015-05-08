package prathameshshetye.minesweeper;

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
    }

    private void startNewGame() {
        ActivityOptionsCompat options;
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, mNewGame, "NewGame");

        Intent intent = new Intent(this, GameActivity.class);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}
