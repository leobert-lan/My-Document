## 文字绘制
### 常用paint设置

normal:

* void setStrokeWidth (int width);

	设置画笔宽度

* void setAntiAlias(boolean enable); 

	指定是否使用抗锯齿功能，如果使用，会使绘图速度变慢  
	
* void setStyle(Paint.Style style);

	绘图样式，enum {FILL,STROKE,FILL_AND_STROKE}
	
* void setTextAlign(Align align);

	文字对齐方式，enum {CENTER,LEFT,RIGHT}
	
* void setTextSize(int size);

	设置文字大小  
  
style:
  
* void setFakeBoldText(boolean b);

	设置是否为粗体文字
	
* void setUnderlineText(boolean b);

	设置下划线
	  
* void setTextSkewX(float f);

	设置字体水平倾斜度，普通斜体字是-0.25
	
* void setStrikeThruText(boolean b);

	设置带有删除线效果  
  
scale:

* void setTextScaleX(float scale);

	水平方向缩放，注意，没有Y方向的
	
[see more at here](http://www.android-doc.com/reference/android/graphics/Paint.html)

---

### canvas中绘制

正常绘制：

* void drawText (String text, float x, float y, Paint paint)
* void drawText (CharSequence text, int start, int end, float x, float y, Paint paint)
* void drawText (String text, int start, int end, float x, float y, Paint paint)
* void drawText (char[] text, int index, int count, float x, float y, Paint paint)

explain: *about x&y*

> Draw the text, with origin at (x,y), using the specified paint. 
> 
> * @param x     The x-coordinate of the origin of the text being drawn
> * @param y     The y-coordinate of the baseline of the text being drawn

--

指定单字位置挥绘制：

*deprecate， not support for supplementary characters (eg emoji)*

* void drawPosText(String text, @Size(multiple=2) float[] pos, Paint paint);
* void drawPosText(char[] text, int index, int count, @Size(multiple=2) float[] pos, Paint paint);

--

按照路径绘制：

* void drawTextOnPath (String text, Path path, float hOffset, float vOffset, Paint paint);
* void drawTextOnPath (char[] text, int index, int count, Path path, float hOffset, float vOffset, Paint paint);

>  Draw the text, with origin at (x,y), using the specified paint, along the specified path. The paint's Align setting determins where along the path to start the text.
> 
> * @param hOffset  The distance along the path to add to the text's starting position
> * @param vOffset  The distance above(-) or below(+) the path to position the text *(to affect the baseline of text)*


----
