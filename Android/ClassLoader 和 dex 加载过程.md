# ClassLoader 和 dex 加载过程

## dex
先扯一下dex，我们知道exe是可执行文件的一种，dex是dalvik-exe意思，android中有dalvik虚拟机，这是Google自行开发优化的一个虚拟机，旨在：在小内存，相对有限的计算能力的CPU上保障运行性能，而dex包就是相对应的可执行文件，他的编译、打包都进行了一定的优化。



## ClassLoader

![uml](http://upload-images.jianshu.io/upload_images/1437930-bb9d359f4c7e9935.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



### BootClassLoader

和java虚拟机中不同的是BootClassLoader是ClassLoader内部类,由java代码实现而不是c++实现,是Android平台上所有ClassLoader的最终parent,这个内部类是包内可见,所以我们没法使用。

### URLClassLoader

只能用于加载jar文件，但是由于 dalvik 不能直接识别jar，所以在 Android 中无法使用这个加载器。

### BaseDexClassLoader

BaseDexClassLoader的构造函数包含四个参数，分别为：

* dexPath,指目标类所在的APK或jar文件的路径,类装载器将从该路径中寻找指定的目标类,**该类必须是APK或jar的全路径**.*如果要包含多个路径,路径之间必须使用特定的分割符分隔,特定的分割符可以使用`System.getProperty(“path.separtor”)`获得*。上面"支持加载APK、DEX和JAR，也可以从SD卡进行加载"指的就是这个路径，最终做的是将dexPath路径上的文件ODEX优化到内部位置optimizedDirectory，然后，再进行加载的。
* File optimizedDirectory,由于dex文件被包含在APK或者Jar文件中,因此在装载目标类之前需要先从APK或Jar文件中解压出dex文件,**该参数就是指定解压出的dex 文件存放的路径**。这也是对apk中dex根据平台进行ODEX优化的过程。其实APK是一个程序压缩包，里面包含dex文件，ODEX优化就是把包里面的执行程序提取出来，就变成ODEX文件，因为你提取出来了，系统**第一次**启动的时候就不用去解压程序压缩包的程序，少了一个解压的过程。这样的话系统启动就加快了。为什么说是第一次呢？是因为DEX版本的也只有第一次会解压执行程序到 /data/dalvik-cache（针对PathClassLoader）或者optimizedDirectory(针对DexClassLoader）目录，之后也是直接读取目录下的的dex文件，所以第二次启动就和正常的差不多了。当然这只是简单的理解，实际生成的ODEX还有一定的优化作用。**ClassLoader只能加载内部存储路径中的dex文件，所以这个路径必须为内部路径**。
* libPath,指目标类中所使用的C/C++库存放的路径
* classload,是指该装载器的父装载器,一般为当前执行类的装载器，例如在Android中以context.getClassLoader()作为父装载器。


### DexClassLoader
DexClassLoader支持加载APK、DEX和JAR，也可以从SD卡进行加载。
上面说dalvik不能直接识别jar,DexClassLoader却可以加载jar文件,这难道不矛盾吗?其实在BaseDexClassLoader里对".jar",".zip",".apk",".dex"后缀的文件最后都会生成一个对应的dex文件,所以最终处理的还是dex文件,而URLClassLoader并没有做类似的处理。
**一般都是用这个DexClassLoader来作为动态加载的加载器**。



### PathClassLoader
Android系统通过`PathClassLoader`来加载系统类和主dex中的类。我们是用不到的

ClassLoader加载class的过程



### #DexFile

```
public Class loadClassBinaryName(String name, ClassLoader loader) { 
    return defineClass(name, loader, mCookie);
}
private native static Class defineClass(String name, 
												ClassLoader loader, 
												int cookie);
```

BaseDexClassLoader中有个pathList对象，

pathList中包含一个DexFile的数组dexElements

由上面分析知道，dexPath传入的原始dex(.apk,.zip,.jar等)文件在optimizedDirectory文件夹中生成相应的优化后的odex文件，dexElements数组就是这些odex文件的集合，如果不分包一般这个数组只有一个Element元素，也就只有一个DexFile文件，而对于类加载呢，就是遍历这个集合，通过DexFile去寻找。最终调用native方法的defineClass。


### 部分源码

BaseDexClassLoader:

```
 public class BaseDexClassLoader extends ClassLoader {
        private final DexPathList pathList;

        public BaseDexClassLoader(String dexPath, File optimizedDirectory,
                String libraryPath, ClassLoader parent) {
            super(parent);
            this.pathList = new DexPathList(this, dexPath, libraryPath, optimizedDirectory);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            List<Throwable> suppressedExceptions = new ArrayList<Throwable>();
            Class c = pathList.findClass(name, suppressedExceptions);
            if (c == null) {
                ClassNotFoundException cnfe = new ClassNotFoundException("Didn't find class \"" + name + "\" on path: " + pathList);
                for (Throwable t : suppressedExceptions) {
                    cnfe.addSuppressed(t);
                }
                throw cnfe;
            }
            return c;
        }
    }
```

DexPathList：

```
public Class findClass(String name) { 
    for (Element element : dexElements) { 
        DexFile dex = element.dexFile;
        if (dex != null) { 
            Class clazz = dex.loadClassBinaryName(name, definingContext); 
          if (clazz != null) { 
              return clazz; 
          } 
        } 
    } 
    return null;
}
```


PathClassLoader：

``` java

/**
 * Provides a simple {@link ClassLoader} implementation that operates on a list
 * of files and directories in the local file system, but does not attempt to
 * load classes from the network. Android uses this class for its system class
 * loader and for its application class loader(s).
 */
public class PathClassLoader extends BaseDexClassLoader {
    /**
     * Creates a {@code PathClassLoader} that operates on a given list of files
     * and directories. This method is equivalent to calling
     * {@link #PathClassLoader(String, String, ClassLoader)} with a
     * {@code null} value for the second argument (see description there).
     *
     * @param dexPath the list of jar/apk files containing classes and
     * resources, delimited by {@code File.pathSeparator}, which
     * defaults to {@code ":"} on Android
     * @param parent the parent class loader
     */
    public PathClassLoader(String dexPath, ClassLoader parent) {
        super(dexPath, null, null, parent);
    }

    /**
     * Creates a {@code PathClassLoader} that operates on two given
     * lists of files and directories. The entries of the first list
     * should be one of the following:
     *
     * <ul>
     * <li>JAR/ZIP/APK files, possibly containing a "classes.dex" file as
     * well as arbitrary resources.
     * <li>Raw ".dex" files (not inside a zip file).
     * </ul>
     *
     * The entries of the second list should be directories containing
     * native library files.
     *
     * @param dexPath the list of jar/apk files containing classes and
     * resources, delimited by {@code File.pathSeparator}, which
     * defaults to {@code ":"} on Android
     * @param libraryPath the list of directories containing native
     * libraries, delimited by {@code File.pathSeparator}; may be
     * {@code null}
     * @param parent the parent class loader
     */
    public PathClassLoader(String dexPath, String libraryPath,
            ClassLoader parent) {
        super(dexPath, null, libraryPath, parent);
    }
}
```


DexClassLoader：

``` java
 /**
 * A class loader that loads classes from {@code .jar} and {@code .apk} files
 * containing a {@code classes.dex} entry. This can be used to execute code not
 * installed as part of an application.
 *
 * <p>This class loader requires an application-private, writable directory to
 * cache optimized classes. Use {@code Context.getDir(String, int)} to create
 * such a directory: <pre>   {@code
 *   File dexOutputDir = context.getDir("dex", 0);
 * }</pre>
 *
 * <p><strong>Do not cache optimized classes on external storage.</strong>
 * External storage does not provide access controls necessary to protect your
 * application from code injection attacks.
 */
public class DexClassLoader extends BaseDexClassLoader {
    /**
     * Creates a {@code DexClassLoader} that finds interpreted and native
     * code.  Interpreted classes are found in a set of DEX files contained
     * in Jar or APK files.
     *
     * <p>The path lists are separated using the character specified by the
     * {@code path.separator} system property, which defaults to {@code :}.
     *
     * @param dexPath the list of jar/apk files containing classes and
     *     resources, delimited by {@code File.pathSeparator}, which
     *     defaults to {@code ":"} on Android
     * @param optimizedDirectory directory where optimized dex files
     *     should be written; must not be {@code null}
     * @param libraryPath the list of directories containing native
     *     libraries, delimited by {@code File.pathSeparator}; may be
     *     {@code null}
     * @param parent the parent class loader
     */
    public DexClassLoader(String dexPath, String optimizedDirectory,
            String libraryPath, ClassLoader parent) {
        super(dexPath, new File(optimizedDirectory), libraryPath, parent);
    }
}
```

