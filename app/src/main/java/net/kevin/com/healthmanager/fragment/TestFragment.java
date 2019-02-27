package net.kevin.com.healthmanager.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.kevin.com.healthmanager.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {

    private static final String TAG = "PLZ";
    private TextView tv;


    public static TestFragment newInstance(String name) {
        
        Bundle args = new Bundle();
        args.putString("name", name);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv = (TextView) view.findViewById(R.id.fragment_test_tv);
        Bundle bundle = getArguments();
        final String name = bundle.get("name").toString();
        if (bundle != null) {
            tv.setText(name);
        }
    }
}
