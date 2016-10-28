package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class CreateDatabase extends SQLiteOpenHelper {

    private static int CURRENT_DB_VERSION = 1;

    CreateDatabase(Context context, String dbName) {
        super(context, dbName, null, CURRENT_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE if not exists bit_foo_coins(name string, visible_home string);");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            onUpgrade(db, 1, CURRENT_DB_VERSION);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (newVersion) {
            case 1:
                break;
        }
    }

    SQLiteDatabase getDatabase(boolean isRead) {
        if (!isRead)
            return this.getWritableDatabase();
        else
            return this.getReadableDatabase();
    }

}
