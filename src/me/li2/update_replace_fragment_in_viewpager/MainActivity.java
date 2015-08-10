/*
 *     Created by WeiYi Li
 *     2015-08-07
 *     weiyi.just2@gmail.com
 *     li2.me
 */
package me.li2.update_replace_fragment_in_viewpager;

import java.util.Date;

import com.viewpagerindicator.CirclePageIndicator;

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

public class MainActivity extends FragmentActivity implements OnClickListener {

    private static final String TAG = "UpdateFragment_main";
    private static final int PAGE_COUNT = 4;
    private ViewPager mViewPager;
    private Button mDayPlusButton;
    private Button mDayMinusButton;
    private EditText mEditorText;
    private CheckBox mCheckBox;
    private Switch mSwitch;
    
    private Date mDate;
    private String mContent;
    private boolean mChecked;
    private int mFragmentToShow;
    
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
        
        mDate = new Date();
        mContent = "Hello World, I'm li2.";
        mChecked = true;
        mFragmentToShow = 0;
    }
    
    private FragmentPagerAdapter mViewPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return Page0Fragment.newInstance(mDate);
            } else if (position == 1) {
                return Page1Fragment.newInstance(mContent);
            } else if (position == 2) {
                return Page2Fragment.newInstance(mChecked);
            } else if (position == 3) {
                return ContainerFragment.newInstance(0, mDate, mContent); 
            }
            return null;
        }
        
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
            notifyViewPagerDataSetChanged();
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
        Log.d(TAG, "notifyDataSetChanged()");
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
}
