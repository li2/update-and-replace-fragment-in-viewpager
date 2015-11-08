package me.li2.update_replace_fragment_in_viewpager;


public enum SimpleBackPage {

    PAGE3(3, R.string.page1_title, Page3Fragment.class);

    private int values;
    private int title;
    private Class<?> cls;

    private SimpleBackPage(int values, int title, Class<?> cls) {
        this.values = values;
        this.title = title;
        this.cls = cls;
    }

    public int getValues() {
        return values;
    }

    public void setValues(int values) {
        this.values = values;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public static SimpleBackPage getPageValue(int val) {
        for (SimpleBackPage p : values()) {
            if (p.getValues() == val) {
                return p;
            }
        }
        return null;
    }
}

/*

求问Fragment fragment = (Fragment) page.getCls().newInstance();
http://segmentfault.com/q/1010000003967198/a-1020000003967414

## 根据问题追加的代码Update

SimpleBackPage是enum类型，意图是**通过数字获取对应的Fragment类**：

```java
SimpleBackPage page = SimpleBackPage.getPageValue(pageValue);
// 如果pageValue=1，getCls返回的就是FeedBackFragment.class
// 如果pageVaule=2，getCls返回的就是AboutFrament.class
page.getCls();

public enum SimpleBackPage {
    FEEDBACK(1, R.string.setting_about, FeedBackFragment.class),
    ABOUT(2, R.string.setting_about, AboutFrament.class);
    
    private SimpleBackPage(int values, int title, Class<?> cls) {
        this.values = values;
        this.title = title;
        this.cls = cls;
    }
    public Class<?> getCls() {
        return cls;
    }    
```
------

## 当拿到Fragment类后

`Fragment.class.newInstance()` 通过调用该类的无参数构造器，创建并返回该类的一个实例。
从结果上看等价于：`new Fragment();`

> Returns a new instance of the class represented by this Class, created by invoking the default (that is, zero-argument) constructor. If there is no such constructor, or if the creation fails (either because of a lack of available memory or because an exception is thrown by the constructor), an InstantiationException is thrown. If the default constructor exists but is not accessible from the context where this method is invoked, an IllegalAccessException is thrown.

感觉上`Fragment.class.newInstance()`不如`new Fragment()`清楚明了。而且你还要为其添加try/catch `exception type IllegalAccessException`.
 
*/
