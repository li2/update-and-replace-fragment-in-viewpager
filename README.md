# How to update and replace fragment in viewpager?

## ListView的工作原理

在了解ViewPager的工作原理之前，先回顾ListView的工作原理：

> 1. ListView只有在需要显示某些列表项时，它才会去申请可用的视图对象；如果为所有的列表项数据创建视图对象，会浪费内存；
> 2. ListView找谁去申请视图对象呢？ 答案是adapter。**adapter是一个控制器对象，负责从模型层获取数据，创建并填充必要的视图对象，将准备好的视图对象返回给ListView**； 
> 3. 首先，通过调用adapter的getCount()方法，ListView询问数组列表中包含多少个对象（为避免出现数组越界的错误)；紧接着ListView就调用adapter的getView(int, View, ViewGroup)方法。

ViewPager某种程度上类似于ListView，区别在于：ListView通过`ArrayAdapter.getView(int position, View convertView, ViewGroup parent)`填充视图；ViewPager通过`FragmentPagerAdapter.getItem(int position)`生成指定位置的fragment.

而我们需要关注的是:


## ViewPager和它的adapter是如何配合工作的？

**声明：本文内容针对android.support.v4.app.***
ViewPager有两个adapter：FragmentPagerAdapter和FragmentStatePagerAdapter：
- [android.support.v4.app.FragmentPagerAdapter](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/support/v4/app/FragmentPagerAdapter.java#FragmentPagerAdapter) 
> 继承自android.support.v4.view.PagerAdapter，每页都是一个Fragment，并且所有的Fragment实例一直保存在Fragment manager中。所以它适用于少量固定的fragment，比如一组用于分页显示的标签。除了当Fragment不可见时，它的视图层（view hierarchy）有可能被销毁外，每页的Fragment都会被保存在内存中。（翻译自代码文件的注释部分）
- [android.support.v4.app.FragmentStatePagerAdapter](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/support/v4/app/FragmentStatePagerAdapter.java#FragmentStatePagerAdapter)
> 继承自android.support.v4.view.PagerAdapter，每页都是一个Fragment，当Fragment不被需要时（比如不可见），整个Fragment都会被销毁，除了saved state被保存外（保存下来的bundle用于恢复Fragment实例）。所以它适用于很多页的情况。（翻译自代码文件的注释部分）

它俩的子类，需要实现`getItem(int)` 和 `android.support.v4.view.PagerAdapter.getCount()`.

### 先通过一段代码了解ViewPager和FragmentPagerAdapter的典型用法

稍后做详细分析：

```java
  // Set a PagerAdapter to supply views for this pager.
  ViewPager viewPager = (ViewPager) findViewById(R.id.my_viewpager_id);
  viewPager.setAdapter(mMyFragmentPagerAdapter);
 
  private FragmentPagerAdapter mMyFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
    @Override
    public int getCount() {
      return 2; // Return the number of views available.
    }
 
    @Override
    public Fragment getItem(int position) {
      return new MyFragment(); // Return the Fragment associated with a specified position.
    }
 
    // Called when the host view is attempting to determine if an item's position has changed.
    @Override
    public int getItemPosition(Object object) {
      if (object instanceof MyFragment) {
        ((MyFragment)object).updateView();
      }
      return super.getItemPosition(object);
    }
  };
 
  private class MyFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // do something such as init data
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_my, container, false);
      // init view in the fragment
      return view;
    }
 
    public void updateView() {
      // do something to update the fragment
    }
  }
```

FragmentPagerAdapter和FragmentStatePagerAdapter对Fragment的管理略有不同，在详细考察二者区别之前，我们通过两种较为直观的方式先感受下：

### 通过两张图片直观的对比FragmentPagerAdapter和FragmentStatePagerAdapter的区别

*说明：这两张图片来自于《Android权威编程指南》，原图有3个Fragment，我增加了1个Fragment，以及被调到的方法。*
FragmentPagerAdapter的Fragment管理：

![image-11-4-FragmentPagerAdapter的fragment管理-方法调用](https://github.com/li2/Update_Replace_Fragment_In_ViewPager/blob/master/图11-4-FragmentPagerAdapter的fragment管理-方法调用.png)


FragmentStatePageAdapter的Fragment管理：
![image-11-3FragmentStatePagerAdapter的fragment管理-方法调用](https://github.com/li2/Update_Replace_Fragment_In_ViewPager/blob/master/图11-3FragmentStatePagerAdapter的fragment管理-方法调用.png)


### 详细分析 adapter method和fragment lifecycle method 的调用情况

好啦，感受完毕，我们需要探究其详情，梳理adapter创建、销毁Fragment的过程，过程中adapter method和fragment lifecycle method哪些被调到，有哪些一样，有哪些不一样。

最开始处于第0页时，adapter不仅为第0页创建Fragment实例，还为相邻的第1页创建了Fragment实例：

```java
// 刚开始处在page0
D/Adapter (25946): getItem(0)
D/Fragment0(25946): newInstance(2015-09-10)  // 注释：newInstance()调用了Fragment的构造器方法，下同。
D/Adapter (25946): getItem(1)
D/Fragment1(25946): newInstance(Hello World, I'm li2.)
D/Fragment0(25946): onAttach()
D/Fragment0(25946): onCreate()
D/Fragment0(25946): onCreateView()
D/Fragment1(25946): onAttach()
D/Fragment1(25946): onCreate()
D/Fragment1(25946): onCreateView()
```
第1次从第0页滑到第1页，adapter同样会为相邻的第2页创建Fragment实例；

```java
// 第1次滑到page1
D/Adapter (25946): onPageSelected(1)
D/Adapter (25946): getItem(2)
D/Fragment2(25946): newInstance(true)
D/Fragment2(25946): onAttach()
D/Fragment2(25946): onCreate()
D/Fragment2(25946): onCreateView()
```

> FragmentPagerAdapter和FragmentStatePagerAdapter齐声说：呐，请主公贰放心，属下定会为您准备好相邻的下一页视图哒！么么哒！
它俩对待下一页的态度是相同的，但对于上上页，它俩做出了不一样的事情：
> FragmentPagerAdapter说：**上上页的实例还保留着，只是销毁了它的视图**：

```java
// 第N次（N不等于1）向右滑动选中page2
D/Adapter (25946): onPageSelected(2)
D/Adapter (25946): destroyItem(0)  // 销毁page0的视图
D/Fragment0(25946): onDestroyView()
D/Fragment3(25946): onCreateView()  // page3的Fragment实例仍保存在FragmentManager中，所以只需创建它的视图
```
> FragmentStatePagerAdapter说：**上上页的实例和视图都被俺销毁啦**：

```java
// 第N次（N不等于1）向右滑选中page2
D/Adapter (27880): onPageSelected(2)
D/Adapter (27880): destroyItem(0)  // 销毁page0的实例和视图
D/Adapter (27880): getItem(3)  // 创建page3的Fragment
D/Fragment3(27880): newInstance()
D/Fragment0(27880): onDestroyView()
D/Fragment0(27880): onDestroy()
D/Fragment0(27880): onDetach()
D/Fragment3(27880): onAttach()
D/Fragment3(27880): onCreate()
D/Fragment3(27880): onCreateView()
```

### Fragment getItem(int position)

```java
// Return the Fragment associated with a specified position.
public abstract Fragment getItem(int position);
```
当adapter需要一个指定位置的Fragment，并且这个Fragment不存在时，getItem就被调到，返回一个Fragment实例给adapter。
所以，有必要再次强调，**getItem是创建一个新的Fragment，但是这个方法名可能会被误认为是返回一个已经存在的Fragment**。
对于FragmentPagerAdapter，当每页的Fragment被创建后，这个函数就不会被调到了。对于FragmentStatePagerAdapter，由于Fragment会被销毁，所以它仍会被调到。
由于我们必须在getItem中实例化一个Fragment，所以当getItem()被调用后，Fragment相应的生命周期函数也就被调到了：

```java
D/Adapter (25946): getItem(1)
D/Fragment1(25946): newInstance(Hello World, I'm li2.)  // newInstance()调用了Fragment的构造器方法；
D/Fragment1(25946): onAttach()
D/Fragment1(25946): onCreate()
D/Fragment1(25946): onCreateView()
```

### void destroyItem(ViewGroup container, int position, Object object)

```java
// Remove a page for the given position. 
public void FragmentPagerAdapter.destroyItem(ViewGroup container, int position, Object object) {
    mCurTransaction.detach((Fragment)object);
}

public void FragmentStatePagerAdapter.destroyItem(ViewGroup container, int position, Object object) {
    mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(fragment));
    mFragments.set(position, null);
    mCurTransaction.remove(fragment);
}
```
销毁指定位置的Fragment。从源码中可以看出二者的区别，一个detach，一个remove，这将调用到不同的Fragment生命周期函数：

```java
// 对于FragmentPagerAdapter
D/Adapter (25946): onPageSelected(2)
D/Adapter (25946): destroyItem(0)
D/Fragment0(25946): onDestroyView()  // 销毁视图

// 对于FragmentStatePagerAdapter
D/Adapter (27880): onPageSelected(2)
D/Adapter (27880): destroyItem(0)
D/Fragment0(27880): onDestroyView()  // 销毁视图
D/Fragment0(27880): onDestroy()  // 销毁实例
D/Fragment0(27880): onDetach()
```
### FragmentPagerAdapter和FragmentStatePagerAdapter对比总结
> 二者使用方法基本相同，唯一的区别就在卸载不再需要的fragment时，采用的处理方式不同：
> 
> - 使用FragmentStatePagerAdapter会**销毁掉不需要的fragment**。事务提交后，可将fragment从activity的FragmentManager中彻底移除。类名中的“state”表明：在销毁fragment时，它会将其onSaveInstanceState(Bundle) 方法中的Bundle信息保存下来。用户切换回原来的页面后，保存的实例状态可用于恢复生成新的fragment. 
> - FragmentPagerAdapter的做法大不相同。对于不再需要的fragment，FragmentPagerAdapter则选择调用事务的detach(Fragment) 方法，而非remove(Fragment)方法来处理它。也就是说，FragmentPagerAdapter**只是销毁了fragment的视图**，但仍将fragment实例保留在FragmentManager中。因此， FragmentPagerAdapter创建的fragment永远不会被销毁。
> 
> (摘抄自《Android权威编程指南11.1.4》)

### 更新ViewPager中的Fragment
调用`notifyDataSetChanged()`时，2个adapter的方法的调用情况相同，**当前页和相邻的两页的getItemPosition都会被调用到**。

```java
// Called when the host view is attempting to determine if an item's position has changed. Returns POSITION_UNCHANGED if the position of the given item has not changed or POSITION_NONE if the item is no longer present in the adapter.
public int getItemPosition(Object object) {
    return POSITION_UNCHANGED;
}
```

[从网上找到的解决办法是](http://stackoverflow.com/questions/18088076/update-fragment-from-viewpager/)，覆写getItemPosition使其返POSITION_NONE，以触发Fragment的销毁和重建。可是这将导致Fragment频繁的销毁和重建，并不是最佳的方法。
后来我把注意力放在了入口参数`object`上，"representing an item", 实际上就是Fragment，只需要为Fragment提供一个更新view的public方法：

```
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
    } else if (...) {
    }
    return super.getItemPosition(object);
};

// 更新界面时方法的调用情况
// 当前页为0时
D/Adapter (21517): notifyDataSetChanged(+0)
D/Adapter (21517): getItemPosition(Page0Fragment)
D/Fragment0(21517): updateDate(2015-09-12)
D/Adapter (21517): getItemPosition(Page1Fragment)
D/Fragment1(21517): updateContent(Hello World, I am li2.)

// 当前页为1时
D/Adapter (21517): notifyDataSetChanged(+1)
D/Adapter (21517): getItemPosition(Page0Fragment)
D/Fragment0(21517): updateDate(2015-09-13)
D/Adapter (21517): getItemPosition(Page1Fragment)
D/Fragment1(21517): updateContent(Hello World, I am li2.)
D/Adapter (21517): getItemPosition(Page2Fragment)
D/Fragment2(21517): updateCheckedStatus(true)
```

**在最开始调用notifyDataSetChanged试图更新Fragment时，我是这样做的：用arraylist保存所有的Fragment，当需要更新时，就从arraylist中取出Fragment，然后调用该Fragment的update方法。这种做法非常鱼唇，当时完全不懂得adapter的Fragment manager在替我管理所有的Fragment**。而我只需要：

- 覆写getCount告诉adapter有几个Fragment；
- 覆写getItem以实例化一个指定位置的Fragment返回给adapter；
- 覆写getItemPosition，把入口参数强制转型成自定义的Fragment，然后调用该Fragment的update方法以完成更新。

**只需要覆写这几个adapter的方法，adapter会为你完成所有的管理工作，不需要自己保存、维护Fragment**。

### 替换ViewPager中的Fragment
应用场景可能是这样，比如有一组按钮，Day/Month/Year，有一个包含几个Fragment的ViewPager。点击不同的按钮，需要秀出不同的Fragment。
具体怎么实现，请参考下面的代码：
[github.com/li2/Update_Replace_Fragment_In_ViewPager/ContainerFragment.java](https://github.com/li2/Update_Replace_Fragment_In_ViewPager/blob/master/src/me/li2/update_replace_fragment_in_viewpager/ContainerFragment.java)

### 一些误区
`ViewPager.getChildCount()` 返回的是当前ViewPager所管理的没有被销毁视图的Fragment，并不是所有的Fragment。想要获取所有的Fragment数量，应该调用`ViewPager.getAdapter().getCount()`.

### 一个Demo
为了总结ViewPager的用法，以及写这篇笔记，我写了一个demo，[你可以从这里获取它的源码 github.com/li2/](https://github.com/li2/Update_Replace_Fragment_In_ViewPager)

这一张gif图片，演示了一个包含4个Fragment的ViewPager，通过上面的date+-1 button、EditText、Checkbox来更新前3个Fragment的界面；最后一个Fragment嵌套着2个Fragment，通过ToggleButton来切换。

![image-update_fragment_in_viewpager_demo](https://github.com/li2/Update_Replace_Fragment_In_ViewPager/blob/master/update_fragment_in_viewpager_demo.gif)


这一张gif演示了切换ViewPager页以及更新Fragment时，相关的方法调用。通过一个ScrollView和TextView展示出来。

![image-update_fragment_in_viewpager_withlog](https://github.com/li2/Update_Replace_Fragment_In_ViewPager/blob/master/update_fragment_in_viewpager_log.gif)


## 参考
- [为什么调用 FragmentPagerAdapter.notifyDataSetChanged() 并不能更新其 Fragment？ - Dancefire](http://www.cnblogs.com/dancefire/archive/2013/01/02/why-notifydatasetchanged-does-not-work.html)
**这篇博文详细地解释了notifyDataSetChanged不能更新Fragment的原因，非常好。**

- [android - Update Fragment from ViewPager - Stack Overflow](http://stackoverflow.com/a/18088509/2722270)
**这个stackoverflow问答给出了两种更新Fragment的方法，非常好。**

- [HowTo: ListView, Adapter, getView and different list items’ layouts in one ListView | Android Tales](http://android.amberfog.com/?p=296)
**这篇博文详细地解释了ListView和它的Adapter是如何配合工作的，以及实现不同的listitem layout. 非常好。**

- [android - R etrieve a fragment from ViewPager - Stack Overflow](http://stackoverflow.com/questions/8785221/retrieve-a-fragment-from-a-viewpager)
这个statckoverflow问答讨论怎样从ViewPager中获取一个Fragment，但我目前还不知道拿到Fragment要做什么。

- [android Fragments详解四:管理fragment - nkmnkm的csdn博客](http://blog.csdn.net/niu_gao/article/details/7172483)
这是一篇翻译文章。

- 这些博文、问答的思路类似，讨论Fragment的tag，通过一个list<String> 存储所有Fragment的tag，然后再adapter里通过fm.findFragmentByTag获取Fragment，然后调用Fragment的update方法更新；
或者是通过一个list<Fragment>自己来管理所有的Fragment：
[ViewPager Fragment 数据更新问题 - shadow066的csnd专栏](http://blog.csdn.net/shadow066/article/details/17298675)
[关于ViewPager的数据更新问题小结 - leo8573的csdn专栏](http://blog.csdn.net/leo8573/article/details/7893841)
[Viewpager+fragment数据更新问题解析 | 姜糖水](http://www.cnphp6.com/archives/61068)
[android - How to get existing fragments when using FragmentPagerAdapter - Stack Overflow](http://stackoverflow.com/questions/14035090/how-to-get-existing-fragments-when-using-fragmentpageradapter)
[android - Retrieve a Fragment from a ViewPager - Stack Overflow](http://stackoverflow.com/questions/8785221/retrieve-a-fragment-from-a-viewpager)
[android - support FragmentPagerAdapter holds reference to old fragments - Stack Overflow](
http://stackoverflow.com/questions/9727173/support-fragmentpageradapter-holds-reference-to-old-fragments/9745935#9745935)
**其中，这个问题的答案解释的特别好：However, the ones that are added to the fragment manager now are NOT the ones you have in your fragments list in your Activity. 企图这样更新界面是行不通的：`pagerAdapter.getItem(1)).update(id, name)`**

- [android - Display fragment viewpager within a fragment - Stack Overflow](http://stackoverflow.com/questions/7700226/display-fragment-viewpager-within-a-fragment)
Fragment里有一个ViewPager，ViewPager里有多个Fragment.

- 这些是**Android Fragment相关的源码文件**：
[Android源码仓库](http://grepcode.com/project/repository.grepcode.com/java/ext/com.google.android/android/)
[android.support.v4.app.Fragment](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/support/v4/app/Fragment.java)
[android.support.v4.view.PagerAdapter](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/support/v4/view/PagerAdapter.java)
[android.support.v4.app.FragmentPagerAdapter](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/support/v4/app/FragmentPagerAdapter.java)
[android.support.v4.app.FragmentStatePagerAdapter](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/support/v4/app/FragmentStatePagerAdapter.java)

## 关于作者

Copyright (C) 2015 WeiYi Li (li21)    weiyi.just2@gmail.com    li2.me
**特别声明：禁止转载**