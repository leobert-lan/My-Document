# TensorFlow 安装 mac


> mark :
> 启动
> 
> ```
> $ cd ~/tensorflow
> $ source bin/activate  
> ```
> 关闭
> 
> ```
> (tensorflow)$ deactivate  # 停用 virtualenv
> ```

## 安装HomeBrew
```
$ /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```
## 安装Python环境 -- 推荐Python3
```
$ brew install python3
```

并安装运行环境

```
$ sudo pip3 install --upgrade virtualenv


$ virtualenv --system-site-packages ~/tensorflow
$ cd ~/tensorflow
$ source bin/activate  # 如果使用 bash
(tensorflow)$  # 终端提示符应该发生变化
```


## 安装tf
```
(tensorflow)$ pip3 install --upgrade https://storage.googleapis.com/tensorflow/mac/tensorflow-0.8.0-py3-none-any.whl


(tensorflow)$ pip3 install --upgrade tensorflow
//CPU 版本；

pip3 install --upgrade tensorflow-gpu 
//#GPU 版本
```

## 当使用完 TensorFlow
```
(tensorflow)$ deactivate  # 停用 virtualenv
```





