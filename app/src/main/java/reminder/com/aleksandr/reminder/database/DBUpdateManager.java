package reminder.com.aleksandr.reminder.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import reminder.com.aleksandr.reminder.model.ModelTask;

/**
 * Created by aleksandr on 9/18/15.
 */
public class DBUpdateManager {

    SQLiteDatabase database;

    DBUpdateManager(SQLiteDatabase database) {
        this.database = database;
    }

    public void title(long timeStamp, String title) {
        update(DBHelper.TASK_TITLE_COLUMN, timeStamp, title);
    }

    public void date(long timeStamp, long date) {
        update(DBHelper.TASK_DATE_COLUMN, timeStamp, date);
    }

    public void priority(long timeStamp, int priority) {
        update(DBHelper.TASK_PRIORITY_COLUMN, timeStamp, priority);
    }

    public void status(long timeStamp, int status) {
        update(DBHelper.TASK_STATUS_COLUMN, timeStamp, status);
    }

    public void task(ModelTask modelTask) {
        title(modelTask.getTimeStamp(), modelTask.getTitle());
        date(modelTask.getTimeStamp(), modelTask.getDate());
        priority(modelTask.getTimeStamp(), modelTask.getPriority());
        status(modelTask.getStatus(), modelTask.getStatus());
    }

    private void update(String column, long key, String values) {
        ContentValues cv = new ContentValues();
        cv.put(column, values);
        database.update(DBHelper.TASK_TABLE, cv, DBHelper.TASK_TIME_STAMP_COLUMN + " = " + key, null);
    }

    private void update(String column, long key, long value) {
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        database.update(DBHelper.TASK_TABLE, cv, DBHelper.TASK_TIME_STAMP_COLUMN + " = " + key, null);
    }
}
