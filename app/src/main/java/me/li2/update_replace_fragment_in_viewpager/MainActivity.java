/*
 *     Created by WeiYi Li
 *     2015-08-07
 *     weiyi.just2@gmail.com
 *     li2.me
 */
package me.li2.update_replace_fragment_in_viewpager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends FragmentActivity implements OnClickListener {

    private static final String TAG = "Adapter";
    private static final int PAGE_COUNT = 5;
    private static final int LOG_COLLECT_INTERVAL = 500; // ms
    private ViewPager mViewPager;
    private Button mDayPlusButton;
    private Button mDayMinusButton;
    private EditText mEditorText;
    private CheckBox mCheckBox;
    private Switch mSwitch;
    private TextView mLogView;
    
    private Date mDate;
    private String mContent;
    private boolean mChecked;
    private int mFragmentToShow;
    private boolean mShouldExitLogThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mViewPager = (ViewPager) findViewById(R.id.main_viewpPager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.main_viewPager_indicator);
        indicator.setViewPager(mViewPager);
        indicator.setOnPageChangeListener(mPageChangeListener);        
        
        mDayPlusButton = (Button) findViewById(R.id.main_dayPlusButton);
        mDayMinusButton = (Button) findViewById(R.id.main_dayMinusButton);
        mDayPlusButton.setOnClickListener(this);
        mDayMinusButton.setOnClickListener(this);
        mEditorText = (EditText) findViewById(R.id.main_editContent);
        mEditorText.addTextChangedListener(mTextWatcher);
        mCheckBox = (CheckBox) findViewById(R.id.main_checkbox);
        mCheckBox.setOnCheckedChangeListener(mPage2CheckedChangeListener);
        mSwitch = (Switch) findViewById(R.id.main_switch);
        mSwitch.setOnCheckedChangeListener(mPage3CheckedChangeListener);
        mLogView = (TextView) findViewById(R.id.main_logView);
        
        mDate = new Date();
        mContent = "Hello World, I'm li2.";
        mChecked = true;
        mFragmentToShow = 0;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mShouldExitLogThread = false;
        startCollectLogcatThread();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mShouldExitLogThread = true;
    }
    
    private FragmentPagerAdapter mViewPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        // Return the Fragment associated with a specified position.
        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem(" + position + ")");
            if (position == 0) {
                return Page0Fragment.newInstance(mDate);
            } else if (position == 1) {
                return Page1Fragment.newInstance(mContent);
            } else if (position == 2) {
                return Page2Fragment.newInstance(mChecked);
            } else if (position == 3) {                
                // return Page3Fragment.newInstance();
                return getFragment(3);
            } else if (position == 4) {
                return ContainerFragment.newInstance(0, mDate, mContent); 
            }
            
            return null;
        }
        
        private Fragment getFragment(int pageValue) {
            SimpleBackPage page = SimpleBackPage.getPageValue(pageValue);
            Fragment fragment;
            try {
                fragment = (Fragment) page.getCls().newInstance();
                return fragment;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
        
        // Remove a page for the given position. The adapter is responsible for removing the view from its container.
        @Override
        public void destroyItem(android.view.ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            Log.d(TAG, "destroyItem(" + position + ")");
        };
        
        @Override
        // To update fragment in ViewPager, we should override getItemPosition() method,
        // in this method, we call the fragment's public updating method.
        public int getItemPosition(Object object) {
            Log.d(TAG, "getItemPosition(" + object.getClass().getSimpleName() + ")");
            if (object instanceof Page0Fragment) {
                ((Page0Fragment) object).updateDate(mDate);
            } else if (object instanceof Page1Fragment) {
                ((Page1Fragment) object).updateContent(mContent);
            } else if (object instanceof Page2Fragment) {
                ((Page2Fragment) object).updateCheckedStatus(mChecked);
            } else if (object instanceof ContainerFragment) {
                ((ContainerFragment) object).updateData(mFragmentToShow, mDate, mContent);
            }
            return super.getItemPosition(object);
        };
    };
    
    
    private ViewPager.OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "\nonPageSelected(" + position + ")");
            // notifyViewPagerDataSetChanged();
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        
        @Override
        public void onPageScrollStateChanged(int state) {}
    };
    
    
    /** 
     * To update fragment in ViewPager, we should call PagerAdapter.notifyDataSetChanged() when data changed.
     * we should also override FragmentPagerAdapter.getItemPosition(), and
     * implement a public method for updating fragment.  
     * Refer to [Update Fragment from ViewPager](http://stackoverflow.com/a/18088509/2722270) 
     */    
    private void notifyViewPagerDataSetChanged() {
        Log.d(TAG, "\nnotifyDataSetChanged()");
        mViewPagerAdapter.notifyDataSetChanged();
    }

    
    @Override
    // page 0 data changed
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.main_dayPlusButton) {
            mDate = new Date((mDate.getTime() + 24*3600*1000));
            notifyViewPagerDataSetChanged();
        } else if (id == R.id.main_dayMinusButton) {
            mDate = new Date((mDate.getTime() - 24*3600*1000));
            notifyViewPagerDataSetChanged();
        }
    }
    
    // page 1 data changed
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        
        @Override
        public void afterTextChanged(Editable s) {
            mContent = s.toString();
            notifyViewPagerDataSetChanged();
        }
    };
    
    // page 2 data changed
    private OnCheckedChangeListener mPage2CheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mChecked = isChecked;
            notifyViewPagerDataSetChanged();
        }
    };
    
    // page 3 data changed
    private OnCheckedChangeListener mPage3CheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mFragmentToShow = (isChecked) ? 1 : 0;
            notifyViewPagerDataSetChanged();
        }
    };
    
    
    // Collect all logcat of this app then display in scroll textview,
    // to show the detail of PagerAdapter method and Fragment life cycle
    // when user scroll or change page.
    private void startCollectLogcatThread() {
        clearLogcat();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!mShouldExitLogThread) {
                    collectLogcat();
                    try {
                        Thread.sleep(LOG_COLLECT_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();;
    }
    
    private void clearLogcat() {
        String cmd = "logcat -c";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Colllect logcat and display in TextView
    // http://tanxiaoya105.blog.163.com/blog/static/2103280192012101392053176
    // [logcat with mutiple tags at one time](http://stackoverflow.com/a/16995884/2722270)
    private void collectLogcat() {
        try {
            String cmd = "logcat -d Adapter:D Fragment0:D Fragment1:D Fragment2:D Fragment3:D Fragment4:D *:S";
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log =  new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line + "\n");
            }
            updateLogView(log.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void updateLogView(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLogView.setText(log);
            }
        });
    }
}
