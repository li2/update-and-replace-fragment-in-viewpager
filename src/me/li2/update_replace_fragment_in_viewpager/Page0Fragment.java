package me.li2.update_replace_fragment_in_viewpager;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Page0Fragment extends Fragment {
    
    private static final String TAG = "UpdateFragment_Page0Fragment";
    private static final String EXTRA_DATE = "me.li2.update_replace_fragment_in_viewpager.extra_date";
    private Date mDate;
    private TextView mTextView;

    public static Page0Fragment newInstance(Date date) {
        Log.d(TAG, "newInstance(" + date + ")");
        Bundle args = new Bundle();
        args.putLong(EXTRA_DATE, date.getTime());
        
        Page0Fragment fragment = new Page0Fragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        mDate = new Date(getArguments().getLong(EXTRA_DATE));
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_page0, container, false);
        mTextView = (TextView) view.findViewById(R.id.page0_date);
        mTextView.setText(mDate.toString());
        return view;
    }

    // To update fragment in ViewPager, we should implement a public method for the fragment,
    // and do updating stuff in this method.
    public void updateDate(Date date) {
        Log.d(TAG, "updateDate(" + date + ")");
        mDate = date;
        mTextView.setText(mDate.toString());
    }
}
