package prathameshshetye.minesweeper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.List;


public class Scores extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ScoreAdapter mAdapter;
    private List<Score> mScores;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        mScores = ScoreDatabaseHelper.getInstance(this).getScores();
        Log.d("TAG","mScores.size() = " + mScores.size());
                mRecyclerView = (RecyclerView) findViewById(R.id.scoreRecycler);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new ScoreAdapter(mScores);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

}
