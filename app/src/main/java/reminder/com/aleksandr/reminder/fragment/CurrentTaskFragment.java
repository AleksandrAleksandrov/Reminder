package reminder.com.aleksandr.reminder.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import reminder.com.aleksandr.reminder.R;
import reminder.com.aleksandr.reminder.adapter.CurrentTaskAdapter;
import reminder.com.aleksandr.reminder.database.DBHelper;
import reminder.com.aleksandr.reminder.model.ModelSeparator;
import reminder.com.aleksandr.reminder.model.ModelTask;


/**
 * Created by Aleksandr Aleksandrov
 * Date: 4/17/15
 * Time: 11:18 AM
 */
public class CurrentTaskFragment extends TaskFragment {

    public CurrentTaskFragment() {
        // Required empty public constructor
    }

    OnTaskDoneListener onTaskDoneListener;

    public interface OnTaskDoneListener {
        void onTaskDone(ModelTask task);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTaskDoneListener = (OnTaskDoneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTaskDoneListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_task, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvCurrentTasks);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CurrentTaskAdapter(this);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_LIKE_TITLE + " AND " + DBHelper.SELECTION_STAUS + " OR " + DBHelper.SELECTION_STAUS,
                new String[]{"%" + title + "%", Integer.toString(ModelTask.STATUS_CURRENT), Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void checkAdapter() {
        if (adapter == null) {
            adapter = new CurrentTaskAdapter(this);
            addTaskFromDB();
        }
    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_STAUS + " OR " + DBHelper.SELECTION_STAUS,
                new String[] {Integer.toString(ModelTask.STATUS_CURRENT), Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;

        ModelSeparator separator = null;

        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isTask()) {
                ModelTask task = (ModelTask) adapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }

        if (newTask.getDate() !=0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());

            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_OVERDUE);
                if (!adapter.containsSeparatorOverdue) {
                    adapter.containsSeparatorOverdue = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_OVERDUE);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                if (!adapter.containsSeparatorToday) {
                    adapter.containsSeparatorToday = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TODAY);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                if (!adapter.containsSeparatorTomorrow) {
                    adapter.containsSeparatorTomorrow = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TOMORROW);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                if (!adapter.containsSeparatorFuture) {
                    adapter.containsSeparatorFuture = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_FUTURE);
                }
            }
        }

        if (position != -1) {

            if (!adapter.getItem(position - 1).isTask()) {
                if (position - 2 >= 0 && adapter.getItem(position - 2).isTask()) {
                    ModelTask task = (ModelTask) adapter.getItem(position - 2);
                    if (task.getDateStatus() == newTask.getDateStatus()) {
                        position -= 1;
                    }
                } else if (position - 2 < 0 && newTask.getDate() == 0) {
                    position -= 1;
                }
            }

            if (separator != null) {
                adapter.addItem(position - 1, separator);
            }

            adapter.addItem(position, newTask);
        } else {
            if (separator != null) {
                adapter.addItem(separator);
            }
            adapter.addItem(newTask);
        }

        if (saveToDB) {
            activity.dbHelper.saveTask(newTask);
        }
    }

    @Override
    public void moveTask(ModelTask modelTask) {
        alarmHelper.removeAlarm(modelTask.getTimeStamp());
        onTaskDoneListener.onTaskDone(modelTask);
    }
}
