# TensorFlow Android 移植

mac平台

编译依靠bazel
使用HomeBrew做包管理

* 安装Java环境，brew cask install java
* brew install bazel   慢慢装吧
* brew install automake ，c++的，慢慢装吧
* brew install libtool ,不晓得什么东西，

建议Python3，

* brew install python3
* pip3 install numpy,安装Python3的数学库


## 将tf当做ndk库进行依赖
首先你要有tf源码

在项目的 settings.gradle文件中添加

```
include ':TensorFlow-Android-Inference'
findProject(":TensorFlow-Android-Inference").projectDir = 
            new File("${/path/to/tensorflow_repo}/contrib/android/cmake")
```

注意，TensorFlow的ropo中还有一个tensorflow的目录，确认包路径正确。

在application's build.gradle (adding dependency):

```
debugCompile project(path: ':tensorflow_inference', configuration: 'debug')
releaseCompile project(path: ':tensorflow_inference', configuration: 'release')
```

注意项目名，去掉无关的标识


在TensorFlow-Android-Inference的gradle中，注释掉自动编译任务