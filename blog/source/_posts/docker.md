一、创建

```
docker create
```

创建容器，处于停止状态。 


centos:latest:centos容器：最新版本(也可以指定具体的版本号)。
本地有就使用本地镜像，没有则从远程镜像库拉取。
创建成功后会返回一个容器的ID。
docker run:创建并启动容器。
交互型容器：运行在前台，容器中使用exit命令或者调用docker stop、docker kill命令，容器停止。

如下图已经在前台开启一个docker容器： 
这里写图片描述

i:打开容器的标准输入。
t:告诉docker为容器建立一个命令行终端。
name:指定容器名称，可以不填(随机)，建议根据具体使用功能命名，便于管理。
centos:告诉我们使用什么镜像来启动容器。
/bin/bash:告诉docker要在容器里面执行此命令。
后台型容器：运行在后台，创建后与终端无关，只有调用docker stop、docker kill命令才能使容器停止。

这里写图片描述

d:使用-d参数，使容器在后台运行。
c: 通过-c可以调整容器的CPU优先级。默认情况下，所有的容器拥有相同的CPU优先级和CPU调度周期，但你可以通过Docker来通知内核给予某个或某几个容器更多的CPU计算周期。比如，我们使用-c或者–cpu-shares =0启动了C0、C1、C2三个容器，使用-c/–cpu-shares=512启动了C3容器。这时，C0、C1、C2可以100%的使用CPU资源（1024），但C3只能使用50%的CPU资源（512）。如果这个主机的操作系统是时序调度类型的，每个CPU时间片是100微秒，那么C0、C1、C2将完全使用掉这100微秒，而C3只能使用50微秒。
-c后的命令是循环，从而保持容器的运行。
docker ps：可以查看正在运行的docker容器。
二、查看

docker ps: 查看当前运行的容器
docker ps -a:查看所有容器，包括停止的。
这里写图片描述

标题含义：

CONTAINER ID:容器的唯一表示ID。
IMAGE:创建容器时使用的镜像。
COMMAND:容器最后运行的命令。
CREATED:创建容器的时间。
STATUS:容器状态。
PORTS:对外开放的端口。
NAMES:容器名。可以和容器ID一样唯一标识容器，同一台宿主机上不允许有同名容器存在，否则会冲突。
docker ps -l :查看最新创建的容器，只列出最后创建的。

docker ps -n=2:-n=x选项，会列出最后创建的x个容器。
这里写图片描述

三、启动

通过docker start来启动之前已经停止的docker_run镜像。
1
2
容器名：docker start docker_run，或者ID：docker start 43e3fef2266c。
–restart(自动重启)：默认情况下容器是不重启的，–restart标志会检查容器的退出码来决定容器是否重启容器。 
docker run --restart=always --name docker_restart -d centos /bin/sh -c "while true;do echo hello world; sleep;done":
--restart=always:不管容器的返回码是什么，都会重启容器。
--restart=on-failure:5:当容器的返回值是非0时才会重启容器。5是可选的重启次数。 
这里写图片描述
四、终止

docker stop [NAME]/[CONTAINER ID]:将容器退出。
docker kill [NAME]/[CONTAINER ID]:强制停止一个容器。
这里写图片描述

五、删除

容器终止后，在需要的时候可以重新启动，确定不需要了，可以进行删除操作。
1
2
docker rm [NAME]/[CONTAINER ID]:不能够删除一个正在运行的容器，会报错。需要先停止容器。 
这里写图片描述

一次性删除：docker本身没有提供一次性删除操作，但是可以使用如下命令实现：

docker rm 'docker ps -a -q'：-a标志列出所有容器，-q标志只列出容器的ID，然后传递给rm命令，依次删除容器。
目前您尚未登录，请 登录 或 注册 后进行评论
相关文章推荐
Docker 新建及使用、修改容器

1.检查Docker信息$sudo docker info 2.运行第一个容器$sudo docker run --name 容器名 -i -t ubuntu /bin/bash 新建容器基于ubun...