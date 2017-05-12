---
title: Android 资源文件维护规则
---

# Android 资源文件维护规则 #
author: leobert.lan<p>
time: 6/17/2016 8:46:40 AM 

## 字符串资源 ##

字符串资源牵涉到国际化，本篇中不涉及如何实现国际化。Android提供了国际化所需要的资源切换机制，本篇同样不做涉猎，只需要注意：

- 字符串资源必须定义在资源文件中，不要出现hardcode
- 国际化版本，字符串资源必须在每个字符串资源文件中进行定义（Android Studio编译强制要求）
- 必须按照命名规则命名

命名规则：


> vversion\_code\_Type\_ContentAttr\_subAttr\_name

enum Type: title,dialog,toast,menu,default 该分类不要缺省

ContentAttr： 文本的内容性质属性，例如dialog type下，title是dialog标题，content是正文内容，button是操作按钮的文本，

subAttr: 附加额外的属性

name: 直接和内容相关

examples：

- activity的title



> v100\_title\_settingactivity  设置

- dialog的content

> v100\_dialog\_content\_logoutalert 确定要退出吗？ 

- dialog的确认按钮

> v110\_dialog\_ button\_positive 确定
> 
> v120\_dialog\_ button\_positive\_quit 退出

- toast文本



> v100\_toast\_network\_unconnected 没有连接
> 
> v100\_toast\_login\_nullpwd 请填写密码
> 
> v100\_toast\_login\_unmatch 用户名密码不匹配

- 页面控件显示文字
> v100\_default\_login\_btntxt\_login 登录  contentAttr属性填写页面信息，subattr填写体现出所属控件类型
> 
> v100\_default\_login\_tv\_usr 用户名


- hint

> v100\_default\_login\_ethint\_pwd 请输入账户密码  subattr主要体现出hint

**关于加入版本号**

产品交付字符串资源时没有管理意识，除特定的文案修改都较为随意，对字符串资源维护造成难度，原则上版本升级新增的文案全部重新定义，与版本号挂钩，不对原有资源造成冲突。

## 图片资源 ##
Android发展至今，基本不需要再切 .9 的图了，UI一般交付3套资源：

- mdpi
- xhdpi
- xxhdpi

同样UI组交付的资源命名风格迥异，我们需要自己维护



> vversion\_code\_pageDes/widgetDes\_contentDes\_state


***再次强调***：Android图片资源命名不能以数字开头、不要加入特殊字符、不要使用大写，按照上面的命名规范，**仅需要注意不要使用大写**。

state 命名图片一般用不到，switcher之类的控件需要使用


## 配置相关常量 ##

一般的全局常量，在接口中进行定义，命名风格沿用常量命名风格

examples：
String DB_TABLE_DOWNLOAD = "download";
全部大写，下划线分割
