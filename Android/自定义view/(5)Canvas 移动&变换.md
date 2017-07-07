## canvas移动和变换
常识：

屏幕空间以左上角为原点，X轴正方向向右，Y轴正方向向下

屏幕显示的内容是从canvas来的，canvas描述在屏幕空间坐标系中，通过在canvas上绘制呈现到屏幕，但是已经呈现的东西不会随着canvas的移动和变换而改变

超过屏幕区域的内容不会显示

### 平移（translate）



平移画布的函数原型：

void translate(float dx, float dy)

> * float dx：水平方向平移的距离，正数向右,负数向左
> * flaot dy：垂直方向平移的距离，正数向下,负数向上


### 旋转（Rotate）

* void rotate(float degrees)
* void rotate (float degrees, float px, float py)


### 缩放（scale ）
* public void scale (float sx, float sy) 
* public final void scale (float sx, float sy, float px, float py)

> * @param sx The amount to scale in X
> * @param sy The amount to scale in Y
> * @param px The x-coord for the pivot point (unchanged by the scale)
> * @param py The y-coord for the pivot point (unchanged by the scale)


五、扭曲（skew）
其实我觉得译成斜切更合适，在PS中的这个功能就差不多叫斜切。但这里还是直译吧，大家都是这个名字。看下它的构造函数：
void skew (float sx, float sy)

参数说明：
float sx:将画布在x方向上倾斜相应的角度，sx倾斜角度的tan值，
float sy:将画布在y轴方向上倾斜相应的角度，sy为倾斜角度的tan值，

注意，这里全是倾斜角度的tan值哦，比如我们打算在X轴方向上倾斜60度，tan60=根号3，小数对应1.732

[java] view plain copy
protected void onDraw(Canvas canvas) {  
    // TODO Auto-generated method stub  
    super.onDraw(canvas);  
      
    //skew 扭曲  
    Paint paint_green = generatePaint(Color.GREEN, Style.STROKE, 5);  
    Paint paint_red   = generatePaint(Color.RED, Style.STROKE, 5);  
      
    Rect rect1 = new Rect(10,10,200,100);  
  
    canvas.drawRect(rect1, paint_green);  
    canvas.skew(1.732f,0);//X轴倾斜60度，Y轴不变  
    canvas.drawRect(rect1, paint_red);  
}   

五、裁剪画布（clip系列函数）
裁剪画布是利用Clip系列函数，通过与Rect、Path、Region取交、并、差等集合运算来获得最新的画布形状。除了调用Save、Restore函数以外，这个操作是不可逆的，一但Canvas画布被裁剪，就不能再被恢复！
Clip系列函数如下：
boolean	clipPath(Path path)
boolean	clipPath(Path path, Region.Op op)
boolean	clipRect(Rect rect, Region.Op op)
boolean	clipRect(RectF rect, Region.Op op)
boolean	clipRect(int left, int top, int right, int bottom)
boolean	clipRect(float left, float top, float right, float bottom)
boolean	clipRect(RectF rect)
boolean	clipRect(float left, float top, float right, float bottom, Region.Op op)
boolean	clipRect(Rect rect)
boolean	clipRegion(Region region)
boolean	clipRegion(Region region, Region.Op op)

以上就是根据Rect、Path、Region来取得最新画布的函数，难度都不大，就不再一一讲述。利用ClipRect（）来稍微一讲。

[java] view plain copy
protected void onDraw(Canvas canvas) {  
    // TODO Auto-generated method stub  
    super.onDraw(canvas);  
      
    canvas.drawColor(Color.RED);  
    canvas.clipRect(new Rect(100, 100, 200, 200));  
    canvas.drawColor(Color.GREEN);  
}   
先把背景色整个涂成红色。显示在屏幕上
然后裁切画布，最后最新的画布整个涂成绿色。可见绿色部分，只有一小块，而不再是整个屏幕了。
关于两个画布与屏幕合成，我就不再画图了，跟上面的合成过程是一样的。


六、画布的保存与恢复（save()、restore()）
前面我们讲的所有对画布的操作都是不可逆的，这会造成很多麻烦，比如，我们为了实现一些效果不得不对画布进行操作，但操作完了，画布状态也改变了，这会严重影响到后面的画图操作。如果我们能对画布的大小和状态（旋转角度、扭曲等）进行实时保存和恢复就最好了。
这小节就给大家讲讲画布的保存与恢复相关的函数——Save（）、Restore（）。
int save ()
void	restore()

这两个函数没有任何的参数，很简单。
Save（）：每次调用Save（）函数，都会把当前的画布的状态进行保存，然后放入特定的栈中；
restore（）：每当调用Restore（）函数，就会把栈中最顶层的画布状态取出来，并按照这个状态恢复当前的画布，并在这个画布上做画。
为了更清晰的显示这两个函数的作用，下面举个例子：

[java] view plain copy
protected void onDraw(Canvas canvas) {  
    // TODO Auto-generated method stub  
    super.onDraw(canvas);  
      
    canvas.drawColor(Color.RED);  
      
    //保存当前画布大小即整屏  
    canvas.save();   
      
    canvas.clipRect(new Rect(100, 100, 800, 800));  
    canvas.drawColor(Color.GREEN);  
      
    //恢复整屏画布  
    canvas.restore();  
      
    canvas.drawColor(Color.BLUE);  
}   
他图像的合成过程为：（最终显示为全屏幕蓝色）

下面我通过一个多次利用Save（）、Restore（）来讲述有关保存Canvas画布状态的栈的概念：代码如下：

[java] view plain copy
protected void onDraw(Canvas canvas) {  
    // TODO Auto-generated method stub  
    super.onDraw(canvas);  
      
    canvas.drawColor(Color.RED);  
    //保存的画布大小为全屏幕大小  
    canvas.save();  
      
    canvas.clipRect(new Rect(100, 100, 800, 800));  
    canvas.drawColor(Color.GREEN);  
    //保存画布大小为Rect(100, 100, 800, 800)  
    canvas.save();  
      
    canvas.clipRect(new Rect(200, 200, 700, 700));  
    canvas.drawColor(Color.BLUE);  
    //保存画布大小为Rect(200, 200, 700, 700)  
    canvas.save();  
      
    canvas.clipRect(new Rect(300, 300, 600, 600));  
    canvas.drawColor(Color.BLACK);  
    //保存画布大小为Rect(300, 300, 600, 600)  
    canvas.save();  
      
    canvas.clipRect(new Rect(400, 400, 500, 500));  
    canvas.drawColor(Color.WHITE);  
}   
显示效果为：



在这段代码中，总共调用了四次Save操作。上面提到过，每调用一次Save（）操作就会将当前的画布状态保存到栈中，所以这四次Save（）所保存的状态的栈的状态如下：

注意在，第四次Save（）之后，我们还对画布进行了canvas.clipRect(new Rect(400, 400, 500, 500));操作，并将当前画布画成白色背景。也就是上图中最小块的白色部分，是最后的当前的画布。

如果，现在使用Restor（），会怎样呢，会把栈顶的画布取出来，当做当前画布的画图，试一下：

[java] view plain copy
protected void onDraw(Canvas canvas) {  
    // TODO Auto-generated method stub  
    super.onDraw(canvas);  
      
    canvas.drawColor(Color.RED);  
    //保存的画布大小为全屏幕大小  
    canvas.save();  
      
    canvas.clipRect(new Rect(100, 100, 800, 800));  
    canvas.drawColor(Color.GREEN);  
    //保存画布大小为Rect(100, 100, 800, 800)  
    canvas.save();  
      
    canvas.clipRect(new Rect(200, 200, 700, 700));  
    canvas.drawColor(Color.BLUE);  
    //保存画布大小为Rect(200, 200, 700, 700)  
    canvas.save();  
      
    canvas.clipRect(new Rect(300, 300, 600, 600));  
    canvas.drawColor(Color.BLACK);  
    //保存画布大小为Rect(300, 300, 600, 600)  
    canvas.save();  
      
    canvas.clipRect(new Rect(400, 400, 500, 500));  
    canvas.drawColor(Color.WHITE);  
      
    //将栈顶的画布状态取出来，作为当前画布，并画成黄色背景  
    canvas.restore();  
    canvas.drawColor(Color.YELLOW);  
}   
上段代码中，把栈顶的画布状态取出来，作为当前画布，然后把当前画布的背景色填充为黄色

那如果我连续Restore（）三次，会怎样呢？
我们先分析一下，然后再看效果：Restore（）三次的话，会连续出栈三次，然后把第三次出来的Canvas状态当做当前画布，也就是Rect(100, 100, 800, 800)，所以如下代码：
[java] view plain copy
protected void onDraw(Canvas canvas) {  
    // TODO Auto-generated method stub  
    super.onDraw(canvas);  
      
    canvas.drawColor(Color.RED);  
    //保存的画布大小为全屏幕大小  
    canvas.save();  
      
    canvas.clipRect(new Rect(100, 100, 800, 800));  
    canvas.drawColor(Color.GREEN);  
    //保存画布大小为Rect(100, 100, 800, 800)  
    canvas.save();  
      
    canvas.clipRect(new Rect(200, 200, 700, 700));  
    canvas.drawColor(Color.BLUE);  
    //保存画布大小为Rect(200, 200, 700, 700)  
    canvas.save();  
      
    canvas.clipRect(new Rect(300, 300, 600, 600));  
    canvas.drawColor(Color.BLACK);  
    //保存画布大小为Rect(300, 300, 600, 600)  
    canvas.save();  
      
    canvas.clipRect(new Rect(400, 400, 500, 500));  
    canvas.drawColor(Color.WHITE);  
      
    //连续出栈三次，将最后一次出栈的Canvas状态作为当前画布，并画成黄色背景  
    canvas.restore();  
    canvas.restore();  
    canvas.restore();  
    canvas.drawColor(Color.YELLOW);  
}   
结果为：


OK啦，这篇就到了啦。