package reminder.com.aleksandr.reminder.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.TimeUnit;

import reminder.com.aleksandr.reminder.R;

/**
 * Created by Aleksandr Aleksandrov
 * Date: 5/17/15
 * Time: 11:18 AM
 */
public class SplashFragment extends BaseFragment {


    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SplashTask splashTask = new SplashTask();
        splashTask.execute();
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    class SplashTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
            return null;
        }
    }
}
