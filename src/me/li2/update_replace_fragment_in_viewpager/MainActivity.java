package me.li2.update_replace_fragment_in_viewpager;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity {

    private static final int PAGE_COUNT = 3;
    private ViewPager mViewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mViewPager = (ViewPager) findViewById(R.id.viewpPager);
        mViewPager.setAdapter(mViewPagerAdapter);
    }
    
    private FragmentPagerAdapter mViewPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return Page0Fragment.newInstance(new Date());
            } else if (position == 1) {
                return Page1Fragment.newInstance("Hello World, I'm li2.");
            } else if (position == 2) {
                return Page2Fragment.newInstance(true);
            }
            return null;
        }
        
        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        };
    };
}
