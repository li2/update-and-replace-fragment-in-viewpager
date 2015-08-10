To update fragment in ViewPager, we should
(1) implement a fragment public method to do updating stuff; and
(2) override getItemPosition() method, in this method, call the fragment's public method; then
(3) call PagerAdapter.notifyDataSetChanged() when data changed.        
     
To replace fragment in ViewPager, we should implement a fragment with a framelayout, we call it as ContainerFragment,
We pass a variable "fragmentToShow" to nofity the ContainerFragment, 
depending on "fragmentToShow", the ContainerFragment decide whether replace old fragment with new fragment, or update old fragment.

![Image](https://github.com/li2/Update_Replace_Fragment_In_ViewPager/blob/master/update_fragment_in_viewpager_demo.gif)


Refer
[Update Fragment from ViewPager](http://stackoverflow.com/a/18088509/2722270)
[ViewPager PagerAdapter not updating the View](http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view)
[为什么调用 FragmentPagerAdapter.notifyDataSetChanged() 并不能更新其 Fragment](http://www.cnblogs.com/dancefire/archive/2013/01/02/why-notifydatasetchanged-does-not-work.html)

[danilao/fragments-viewpager-example](https://github.com/danilao/fragments-viewpager-example)
[Replace one Fragment with another in ViewPager](http://stackoverflow.com/questions/18588944/replace-one-fragment-with-another-in-viewpager)
[Replace Fragment inside a ViewPager](http://stackoverflow.com/a/9127423/2722270)
[Dynamic inner Fragment in ViewPager overlapping](http://stackoverflow.com/questions/26079289/dynamic-inner-fragment-in-viewpager-overlapping)



You can access source code on [GitHub](https://github.com/li2/Update_Replace_Fragment_In_ViewPager)

Copyright (C) 2015 WeiYi Li
weiyi.just2@gmail.com
li2.me
