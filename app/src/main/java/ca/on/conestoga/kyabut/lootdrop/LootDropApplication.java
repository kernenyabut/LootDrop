package ca.on.conestoga.kyabut.lootdrop;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LootDropApplication extends Application {

    //Variables
    private static final String DB_NAME = "db_loot_drop";
    private static final int DB_VERSION = 1;

    private SQLiteOpenHelper helper;

    //On Create
    @Override
    public void onCreate() {
        helper = new SQLiteOpenHelper(this, DB_NAME, null, DB_VERSION) {

            //Creates table
            @Override
            public void onCreate(SQLiteDatabase db) {
                /*
                chestType - INT - 1, 2 , or 3
                reward - INT - 1-5
                score - INT
                 */
                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_ld_stats(" +
                        "chestType INT, reward INT)");

                //Table for high scores
                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_ld_high_scores(" +
                        "score INT)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        super.onCreate();
    }

    //Adds game result
    public void addGameResult(int chestType, int reward)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("INSERT INTO tbl_ld_stats (chestType, reward) "
                + "VALUES (" + chestType + ", " + reward +")");
    }

    //Adds to highscore
    public void addHighScore(int score)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("INSERT INTO tbl_ld_high_scores (score) "
                + "VALUES (" + score +")");

    }

    //Gets the amount of loot crates opened
    public int getLootCratesOpened(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT COUNT(chestType) FROM tbl_ld_stats",
                null);
        int ret;

        cursor.moveToFirst();
        ret = cursor.getInt( 0);
        cursor.close();

        return(ret);
    }

    //Gets the highest score
    public int getHighestScore(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT MAX(score) FROM tbl_ld_high_scores",
                null);
        int ret;

        cursor.moveToFirst();
        ret = cursor.getInt( 0);
        cursor.close();

        return(ret);
    }

    //Gets users' favourite loot crate opened
    public int getFavouriteLootCrate(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT chestType FROM tbl_ld_stats GROUP BY chestType ORDER BY COUNT(*) DESC LIMIT 1",
                null);
        int ret;

        //If there are no values
        try
        {
            cursor.moveToFirst();
            ret = cursor.getInt( 0);
            cursor.close();
        }
        catch(Exception ex)
        {
            ret = 0;
        }

        return(ret);
    }

    //Gets the amount of rewards received
    public int getRewardsReceived(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT COUNT(reward) FROM tbl_ld_stats WHERE reward <> 1",
                null);
        int ret;

        cursor.moveToFirst();
        ret = cursor.getInt( 0);
        cursor.close();

        return(ret);
    }

    //Gets the amount of bronze coins they received
    public int getBronzeCoinAmount(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT COUNT(reward) FROM tbl_ld_stats WHERE reward == 2",
                null);
        int ret;

        cursor.moveToFirst();
        ret = cursor.getInt( 0);
        cursor.close();

        return(ret);
    }

    //Gets the amount of silver coins they received
    public int getSilverCoinAmount(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT COUNT(reward) FROM tbl_ld_stats WHERE reward == 3",
                null);
        int ret;

        cursor.moveToFirst();
        ret = cursor.getInt( 0);
        cursor.close();

        return(ret);
    }

    //Gets the amount of gold coins they received
    public int getGoldCoinAmount(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT COUNT(reward) FROM tbl_ld_stats WHERE reward == 4",
                null);
        int ret;

        cursor.moveToFirst();
        ret = cursor.getInt( 0);
        cursor.close();

        return(ret);
    }

    //Gets the amount of diamond gems they received
    public int getDiamondGemAmount(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT COUNT(reward) FROM tbl_ld_stats WHERE reward == 5",
                null);
        int ret;

        cursor.moveToFirst();
        ret = cursor.getInt( 0);
        cursor.close();

        return(ret);
    }

    //Resets stats
    public void resetTableStats(){
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("DELETE FROM tbl_ld_stats;");
        db.execSQL("DELETE FROM tbl_ld_high_scores;");
    }

}
