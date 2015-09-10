package me.li2.update_replace_fragment_in_viewpager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Page1Fragment extends Fragment {
    
    private static final String TAG = "Fragment1";
    private static final String EXTRA_CONTENT = "me.li2.update_replace_fragment_in_viewpager.extra_content";
    private String mContent;
    private TextView mTextView;

    public static Page1Fragment newInstance(String content) {
        Log.d(TAG, "newInstance(" + content + ")");
        Bundle args = new Bundle();
        args.putString(EXTRA_CONTENT, content);
        
        Page1Fragment fragment = new Page1Fragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        mContent = getArguments().getString(EXTRA_CONTENT);
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
        View view = inflater.inflate(R.layout.fragment_page1, container, false);
        mTextView = (TextView) view.findViewById(R.id.page1_content);
        mTextView.setText(mContent);
        return view;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }
    
    // To update fragment in ViewPager, we should implement a public method for the fragment,
    // and do updating stuff in this method.
    public void updateContent(String content) {
        Log.d(TAG, "updateContent(" + content + ")");
        mContent = content;
        mTextView.setText(mContent);
    }
}
