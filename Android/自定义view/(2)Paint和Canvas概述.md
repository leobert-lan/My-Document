## paint和canvas

### Paint的基本设置：

* paint.setAntiAlias(true);//抗锯齿功能
* paint.setColor(Color.RED);  //设置画笔颜色    
* paint.setStyle(Style.FILL);//设置填充样式
* paint.setStrokeWidth(30);//设置画笔宽度
* paint.setShadowLayer (float radius, float dx, float dy, int color)  //设置阴影


### 填充样式的区别：

* Paint.Style.FILL    :填充内部
* Paint.Style.FILL_AND_STROKE  ：填充内部和描边
* Paint.Style.STROKE  ：仅描边

### canvas设置背景

* canvas.drawColor(int color)
* canvas.drawRGB(int r,int g,int b);


### 基本几何的绘制

#### 1、画直线

* void drawLine (float startX, float startY, float stopX, float stopY, Paint paint)

#### 2、多条直线

* void drawLines (float[] pts, Paint paint)
* void drawLines (float[] pts, int offset, int count, Paint paint)

> pts:是点的集合，大家下面可以看到，这里不是形成连接线，而是每两个点形成一条直线，pts的组织方式为｛x1,y1,x2,y2,x3,y3,……｝

#### 3、点

* void drawPoint (float x, float y, Paint paint)

#### 4、多个点

* void drawPoints (float[] pts, Paint paint)
* void drawPoints (float[] pts, int offset, int count, Paint paint)


> float[] pts:点的合集，与上面直线一直，样式为｛x1,y1,x2,y2,x3,y3,……｝
> 
> int offset:集合中跳过的数值个数，注意：一个点是两个值；
> 
> count:参与绘制的数值的个数，不是点的个数，一个点<=>两个数值

#### 5、矩形

* void drawRect (float left, float top, float right, float bottom, Paint paint)
* void drawRect (RectF rect, Paint paint)
* void drawRect (Rect r, Paint paint)

#### 6、圆角矩形

* void drawRoundRect (RectF rect, float rx, float ry, Paint paint)

>RectF rect:要画的矩形
>
>float rx:生成圆角的椭圆的X轴半径
>
>float ry:生成圆角的椭圆的Y轴半径

#### 7、圆形

* void drawCircle (float cx, float cy, float radius, Paint paint)

#### 8、椭圆

* void drawOval (RectF oval, Paint paint)


#### 9、弧

* void drawArc (RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)


>RectF oval:生成椭圆的矩形
>
>float startAngle：弧开始的角度，以X轴正方向为0度
>
>float sweepAngle：弧持续的角度
>
>boolean useCenter:是否利用椭圆中心点，当描边时，TRUE则连接弧顶点到中心点，FALSE只有弧，填充时，TRUE连接到中心点并填充，FALSE时连接弧顶点并填充

### 路径绘制
核心函数：

* void drawPath (Path path, Paint paint)

#### Path类关键API

>直线相关

* void moveTo(float x1, float y1); 将路径起始绘制点定在（x1,y1）的位置；
* void lineTo(float x2, float y2)：绘制到（x2,y2),可连续绘制，上一次的终点是本次的起始点
* void close()；将路径首尾点连接起来，形成封闭路径；

--

> 矩形

* void addRect (float left, float top, float right, float bottom, Path.Direction dir)
* void addRect (RectF rect, Path.Direction dir)


>Path.Direction.CCW：是counter-clockwise缩写，逆时针方向
>
>Path.Direction.CW：是clockwise的缩写，顺时针方向
>
>可用于排版，例如文字按照路径排版

--

> 圆角矩形

* void addRoundRect (RectF rect, float[] radii, Path.Direction dir)
* void addRoundRect (RectF rect, float rx, float ry, Path.Direction dir)


> float[] radii：必须传入8个数值，分四组，分别对应每个角所使用的椭圆的横轴半径和纵轴半径，从左上角顺时针排列？

--

> 圆形路径

* void addCircle (float x, float y, float radius, Path.Direction dir)

--

> 椭圆路径

* void addOval (RectF oval, Path.Direction dir)

--

> 弧形路径

* void addArc (RectF oval, float startAngle, float sweepAngle)

> RectF oval：弧是椭圆的一部分，这个参数就是生成椭圆所对应的矩形；
> 
> float startAngle：开始的角度，X轴正方向为0度
> 
> float sweepAngel：持续的度数；顺时针

--

> 线段轨迹

* lineto不在赘述
* void quadTo (float x1, float y1, float x2, float y2); *贝塞尔曲线，从起始点到（x2,y2）,(x1,y1)是一个偏移操作点*
* void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3); *贝塞尔曲线,从起始点到（x3,y3）,(x1,y1) 为控制点，(x2,y2)为控制点*
* void arcTo(RectF ovalRectF, float startAngle, float sweepAngle) 绘制弧线

---
