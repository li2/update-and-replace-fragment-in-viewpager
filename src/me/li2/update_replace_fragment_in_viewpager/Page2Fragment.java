package me.li2.update_replace_fragment_in_viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class Page2Fragment extends Fragment {
    
    private static final String TAG = "UpdateFragment_Page2Fragment";
    private static final String EXTRA_CHECKED = "me.li2.update_replace_fragment_in_viewpager.extra_checked";
    private boolean mChecked;
    private CheckBox mCheckBox;

    public static Page2Fragment newInstance(boolean checked) {
        Log.d(TAG, "newInstance(" + checked + ")");
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_CHECKED, checked);
        
        Page2Fragment fragment = new Page2Fragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        mChecked = getArguments().getBoolean(EXTRA_CHECKED);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_page2, container, false);
        mCheckBox = (CheckBox) view.findViewById(R.id.page2_selected);
        mCheckBox.setChecked(mChecked);
        return view;
    }

    // To update fragment in ViewPager, we should implement a public method for the fragment,
    // and do updating stuff in this method.
    public void updateCheckedStatus(boolean checked) {
        Log.d(TAG, "updateCheckedStatus(" + checked + ")");
        mChecked = checked;
        mCheckBox.setChecked(mChecked);
    }
}
