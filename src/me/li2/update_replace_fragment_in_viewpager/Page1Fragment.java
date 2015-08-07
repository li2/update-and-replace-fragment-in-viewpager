package me.li2.update_replace_fragment_in_viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Page1Fragment extends Fragment {
    
    private static final String TAG = "UpdateFragment_Page1Fragment";
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_page1, container, false);
        mTextView = (TextView) view.findViewById(R.id.page1_content);
        mTextView.setText(mContent);
        return view;
    }
    
    // To update fragment in ViewPager, we should implement a public method for the fragment,
    // and do updating stuff in this method.
    public void updateContent(String content) {
        Log.d(TAG, "updateContent(" + content + ")");
        mContent = content;
        mTextView.setText(mContent);
    }
}
