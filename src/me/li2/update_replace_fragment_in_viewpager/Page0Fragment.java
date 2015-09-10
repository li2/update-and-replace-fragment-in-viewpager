package me.li2.update_replace_fragment_in_viewpager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Page0Fragment extends Fragment {
    
    private static final String TAG = "Fragment0";
    private static final String EXTRA_DATE = "me.li2.update_replace_fragment_in_viewpager.extra_date";
    private Date mDate;
    private TextView mTextView;

    public static Page0Fragment newInstance(Date date) {
        Log.d(TAG, "newInstance(" + formatDate(date) + ")");
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
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach()");
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_page0, container, false);
        mTextView = (TextView) view.findViewById(R.id.page0_date);
        mTextView.setText(mDate.toString());
        return view;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }    

    // To update fragment in ViewPager, we should implement a public method for the fragment,
    // and do updating stuff in this method.
    public void updateDate(Date date) {
        Log.d(TAG, "updateDate(" + formatDate(date) + ")");
        mDate = date;
        mTextView.setText(mDate.toString());
    }
    
    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(date);
    }
}
