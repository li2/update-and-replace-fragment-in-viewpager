/*
 *     Created by WeiYi Li
 *     2015-08-07
 *     weiyi.just2@gmail.com
 *     li2.me
 */
package me.li2.update_replace_fragment_in_viewpager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ContainerFragment extends Fragment {
    
    private static final String TAG = "Fragment4";
    private static final String EXTRA_FRAGMENT_TO_SHOW = "me.li2.update_replace_fragment_in_viewpager.extra_fragment_to_show";
    private static final String EXTRA_DATE = "me.li2.update_replace_fragment_in_viewpager.extra_date";
    private static final String EXTRA_CONTENT = "me.li2.update_replace_fragment_in_viewpager.extra_content";
    
    private int mFragmentToShow;
    private Date mDate;
    private String mContent;
    private int mFragmentContainerId;

    public static ContainerFragment newInstance(int fragmentToShow, Date date, String content) {
        Log.d(TAG, String.format("newInstance(fragmentToShow=%d, Date=%s, Content=%s", fragmentToShow, formatDate(date), content));
        Bundle args = new Bundle();
        args.putInt(EXTRA_FRAGMENT_TO_SHOW, fragmentToShow);
        args.putLong(EXTRA_DATE, date.getTime());
        args.putString(EXTRA_CONTENT, content);
        
        ContainerFragment fragment = new ContainerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        mFragmentToShow = getArguments().getInt(EXTRA_FRAGMENT_TO_SHOW);
        mDate = new Date(getArguments().getLong(EXTRA_DATE));
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
        View view = inflater.inflate(R.layout.fragment_container, container, false);
        // To avoid overlapping, every fragment in the container in ViewPager should has an unique ID. http://stackoverflow.com/a/26079878/2722270
        FrameLayout fragmentContainer = (FrameLayout) view.findViewById(R.id.fragmentContainer);
        int fragmentContainerId = R.id.fragmentContainer0;
        mFragmentContainerId = fragmentContainerId;
        fragmentContainer.setId(fragmentContainerId);
        
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment oldFragment = fm.findFragmentById(fragmentContainerId);
        Fragment newFragment;
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        if (mFragmentToShow == 0) {
            newFragment = Page0Fragment.newInstance(mDate);
        } else {
            newFragment = Page1Fragment.newInstance(mContent);
        }
        ft.add(fragmentContainerId, newFragment);
        ft.commit();
        Log.d(TAG, "add fragment " + newFragment.getClass().getSimpleName());
        
        return view;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }
    
    // To replace fragment in ViewPager, we implement a fragment with a framelayout, we call it as ContainerFragment,
    // We pass a variable "fragmentToShow" to nofity the ContainerFragment,
    // depending on "fragmentToShow", the ContainerFragment decide whether replace old fragment with new fragment, or
    // update old fragment.
    public void updateData(int fragmentToShow, Date date, String content) {
        Log.d(TAG, String.format("updateData(fragmentToShow=%d, Date=%s, Content=%s", fragmentToShow, formatDate(date), content));
        mDate = date;
        mContent = content;

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (mFragmentToShow != fragmentToShow) {
            Log.d(TAG, "replace fragment");
            mFragmentToShow = fragmentToShow;
            if (mFragmentToShow == 0) {
                ft.replace(mFragmentContainerId, Page0Fragment.newInstance(mDate));
            } else {
                ft.replace(mFragmentContainerId, Page1Fragment.newInstance(mContent));
            }
            ft.commit();        
        } else {
            Fragment oldFragment = fm.findFragmentById(mFragmentContainerId);
            if (oldFragment != null) {
                Log.d(TAG, "update fragment: " + oldFragment.getClass().getSimpleName());
                if (oldFragment instanceof Page0Fragment) {
                    ((Page0Fragment) oldFragment).updateDate(mDate);
                } else if (oldFragment instanceof Page1Fragment) {
                    ((Page1Fragment) oldFragment).updateContent(mContent);
                }
            }
        }
    }
    
    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(date);
    }
}
