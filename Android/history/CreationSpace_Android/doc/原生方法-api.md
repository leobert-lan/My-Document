# 原生方法API文档 #

## v1.0.00 ##

### 跳转到文章详情 ###

    APP_N_CZSPACE_START_ARTICLEDETAIL
参数：

- article_id 文章id

### 跳转到项目详情 ###

    APP_N_CZSPACE_START_PROJECTDETAIL

参数：

- project_id 项目id


### 跳转到圈子详情 ###

    APP_N_CZSPACE_START_CIRCLEDETAIL

参数：

- circle_id 圈子id

### 用户中心-关闭原生的批量操作状态 ###


> 用于更新原生的UI元素状态，例如完成编辑后


    APP_N_CZSPACE_UCENTER_BATCHOP_CLOSE

无参数


### 跳转到个人主页 ###

    APP_N_CZSPACE_START_UCENTER

参数：

- target_user 跳转目标用户id或者username，因不确定接口的具体业务，使用较为抽象的参数名

### 跳转到评论列表 ###
**todo 业务参数不明**

### 进行评论 ###
TODO

###  项目筛选 ###
(接口给数据，比较复杂，等接口全部好了再处理)

### 跳转到活动详情页 ###


    APP_N_CZSPACE_START_VSOACTIVITYDETAIL

参数：

- vso\_ac\_link 活动链接地址




## 通用 ##

### 拨号 ###

### 登录 ###

### 登录信息获取 ###

###  分享 ###

### 跳出输入弹窗 ###

 `APP_N_PUBLIC_INPUT`
 
 参数：
 
- text_cancel  取消按钮的文字，不传默认为取消
- text_submit  提交按钮的文字，不传默认为完成
- text_hint    原生输入框的输入提示，不传默认为空
- text_title   仿照网易输入框上部中间的title，默认为空
- max_length   最大输入长度，默认200
 
 //暂不使用
 key_cache    如果该文字需要做一个缓存，缓存的key，该缓存仅仅存储在内存中，使用取消、完成均会清除，
 
** 返回：**  

 - text_input 返回输入文字

 
### 跳转到评论列表 ###

`APP_N_CZSPACE_START_COMMENTLIST`

参数：

- url 列表页url

### 弹出自定义toast ###

`APP_N_PUBLIC_CUSTOM_TOAST`

参数：

* type int类型 0代表显示勾，1代表显示叉
* content 通知的内容


----
以下内容待定：

 评论

 弹出dialog（多种样式，需要联调，目前iOS能截取alert和confirm）

 弹出actionsheet

 弹出toast
