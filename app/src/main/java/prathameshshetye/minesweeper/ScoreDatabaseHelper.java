package prathameshshetye.minesweeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by p.shetye on 5/11/15.
 */
public class ScoreDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SweeperScores";

    // Table name
    private static final String TABLE_NAME = "MyScores";

    // Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TIME_TAKEN = "timeTaken";
    private static final String KEY_CELLS = "cells";
    private static final String KEY_MINES = "mines";
    private static final String KEY_DESCRIP = "descrip";

    private static ScoreDatabaseHelper mInstance = null;

    public static ScoreDatabaseHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new ScoreDatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private ScoreDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCORES =
                "CREATE TABLE " + TABLE_NAME + "("
                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + KEY_TIME_TAKEN + " INTEGER, "
                        + KEY_CELLS + " INTEGER, "
                        + KEY_MINES + " INTEGER, "
                        + KEY_DESCRIP + " TEXT "
                        + ")";
        db.execSQL(CREATE_SCORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing so far
    }
    
    public int saveScore(Score score) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME_TAKEN, score.getTimeTaken());
        values.put(KEY_CELLS, score.getCells());
        values.put(KEY_MINES, score.getMines());
        values.put(KEY_DESCRIP, score.getDescrip());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return (int)id;
    }

    public List<Score> getScores () {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Score> result = new ArrayList<>();

        String selectQuery = "SELECT "
                + KEY_ID + ","
                + KEY_TIME_TAKEN + ","
                + KEY_CELLS + ","
                + KEY_MINES + ","
                + KEY_DESCRIP + " FROM "
                + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                result.add(new Score(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                ));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    public Score getScore(int _id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{
                KEY_ID,
                KEY_TIME_TAKEN,
                KEY_CELLS,
                KEY_MINES,
                KEY_DESCRIP
        }, KEY_ID + "=?", new String[]{
                String.valueOf(_id)
        }, null, null, null, null);

        if (cursor == null) {
            cursor.close();
            db.close();
            return null;
        } else {
            cursor.moveToFirst();
        }

        Score score = new Score(
            cursor.getInt(0),
            cursor.getInt(1),
            cursor.getInt(2),
            cursor.getInt(3),
            cursor.getString(4)
        );
        cursor.close();
        db.close();
        return score;
    }

    public int updateScore(Score score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME_TAKEN, score.getTimeTaken());
        values.put(KEY_CELLS, score.getCells());
        values.put(KEY_MINES, score.getMines());
        values.put(KEY_DESCRIP, score.getDescrip());

        int id = db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[]{
                String.valueOf(score.getId())
        });
        db.close();
        return id;
    }

}
