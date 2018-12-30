package reminder.com.aleksandr.reminder;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import reminder.com.aleksandr.reminder.adapter.TabAdapter;
import reminder.com.aleksandr.reminder.alarm.AlarmHelper;
import reminder.com.aleksandr.reminder.database.DBHelper;
import reminder.com.aleksandr.reminder.dialog.AddingTaskDialogFragment;
import reminder.com.aleksandr.reminder.dialog.EditTaskDialogFragment;
import reminder.com.aleksandr.reminder.fragment.CurrentTaskFragment;
import reminder.com.aleksandr.reminder.fragment.DoneTaskFragment;
import reminder.com.aleksandr.reminder.fragment.SplashFragment;
import reminder.com.aleksandr.reminder.fragment.TaskFragment;
import reminder.com.aleksandr.reminder.model.ModelTask;

public class MainActivity extends AppCompatActivity implements AddingTaskDialogFragment.AddingTaskListener,
        CurrentTaskFragment.OnTaskDoneListener,
        DoneTaskFragment.OnTaskRestoreListener,
        EditTaskDialogFragment.EditingTaskListener{

    FragmentManager fragmentManager;

    PreferenceHelper preferenceHelper;

    TabAdapter tabAdapter;

    TaskFragment currentTaskFragment;
    TaskFragment doneTaskFragment;

    SearchView searchView;

    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        Ads.showBanner(this);

        PreferenceHelper.getInstatnce().init(getApplicationContext());

        preferenceHelper = PreferenceHelper.getInstatnce();

        AlarmHelper.getInstance().init(getApplicationContext());

        dbHelper = new DBHelper(getApplicationContext());

        fragmentManager = getFragmentManager();

        runSplash();

        setUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem splashItem = menu.findItem(R.id.action_splash);
        splashItem.setChecked(preferenceHelper.getBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_splash) {
            item.setChecked(!item.isChecked());
            preferenceHelper.putBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE, item.isChecked());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void runSplash() {
        if (!preferenceHelper.getBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE)) {
            SplashFragment splashFragment = new SplashFragment();

            fragmentManager.beginTransaction().replace(R.id.content_frame, splashFragment).addToBackStack(null).commit();
        }
    }

    private void setUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_Layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.current_task));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.done_task));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        tabAdapter = new TabAdapter(fragmentManager, 2);

        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener((View view) -> {
            DialogFragment addingTaskDialogFragment = new AddingTaskDialogFragment();
            addingTaskDialogFragment.show(fragmentManager, "AddingTaskDialogFragment");
        });

        currentTaskFragment = (CurrentTaskFragment) tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION);
        doneTaskFragment = (DoneTaskFragment) tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION);

        searchView = (SearchView) findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentTaskFragment.findTasks(newText);
                doneTaskFragment.findTasks(newText);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    public void onTaskAdded(ModelTask newTask) {
        currentTaskFragment.addTask(newTask, true);
    }

    @Override
    public void onTaskAddingCancel() {
        Toast.makeText(this, "Task adding canceled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskDone(ModelTask task) {
        doneTaskFragment.addTask(task, false);
    }

    @Override
    public void onTaskRestore(ModelTask modelTask) {
        currentTaskFragment.addTask(modelTask, false);
    }

    @Override
    public void onTaskEdited(ModelTask updateTask) {
        currentTaskFragment.updateTask(updateTask);
        dbHelper.update().task(updateTask);
    }
}
