package prathameshshetye.minesweeper.score;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import prathameshshetye.minesweeper.R;
import prathameshshetye.minesweeper.generic.RecyclerItemClickListener;


public class Scores extends AppCompatActivity {

    List<Score> mScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        RecyclerView recyclerView;
        ScoreAdapter adapter;
        Toolbar toolbar;

        mScores = ScoreDatabaseHelper.getInstance(this).getScores();
        recyclerView = (RecyclerView) findViewById(R.id.scoreRecycler);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new ScoreAdapter(mScores);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //Do nothing for now
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        shareScore(position);
                    }
                }));
    }

    private void shareScore(int position) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.your_score));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, mScores.get(position).toString(this));
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_score)));
    }
}
