package prathameshshetye.minesweeper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    public static final int N=8;
    public static final int M=10;
    private RecyclerView mRecView;
    private CellAdapter mAdapter;
    private Cell[] mCells = new Cell[N*N];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;

        Random rand = new Random();
        for(int i=0; i<(N*N); i++) {
            mCells.[i] = new Cell(i, false);
        }

        for(int i=0; i<M; i++) {
            mCells.get(rand.nextInt(N*N)).setIsMine(true);
        }
        mRecView = (RecyclerView) findViewById(R.id.RecyclerView);
        mAdapter = new CellAdapter(mCells);
        mRecView.setAdapter(mAdapter);
        mRecView.setLayoutManager(new GridLayoutManager(this, 8, GridLayoutManager.VERTICAL, false));

        mRecView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(mCells.get(position).getSurroundings())
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
