# Android 中的设计风格

##概述&历史
纵观Android设计风格历史，可以说是杂乱的，iOS和WP从设计之初就有特征明显的设计风格。Android从2.2（甚至更早一点）开始被更多的人注意到，虽然framework层提供了Android开发人员提供的THEME，但是没有给设计规范指导，加之第三方rom，市场上可以看到各种乱七八糟的风格。

发展到2011年，android4.0版本发布，同时发布了《android design》这一设计规范，不翻墙已经找不到原始资源了，国内有一些可窥一斑的翻译版：[android design](https://www.freemindworld.com/adchs/index.html)，而且在Android 4.0版本中THEME进行了大改版，提供了HOLO风格。

holo风格中提供了两种主题类型：

- HOLO Light (淡色)
- HOLO Dark (深色)

严格的说，holo主题只是按照Android4.0发布之时的Android Design规范的一个具体体现。

随着Android的发展，到Android5.0（Android L）版本时，Android Design也包含了更多的风格体系，而系统原生的主题风格是material design。

## Material Design ##

### Introduction ###
![Material Design](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0Bx4BSt6jniD7Y1huOXVQdlFPMmM/materialdesign_introduction.png)

> We challenged ourselves to create a visual language for our users that synthesizes the classic principles of good design with the innovation and possibility of technology and science. This is material design. This spec is a living document that will be updated as we continue to develop the tenets and specifics of material design.

> 我们挑战自我，为用户创造了崭新的视觉设计语言。与此同时，新的设计语言除了遵循经典设计定则，还汲取了最新的科技，秉承了创新的设计理念。这就是原质化设计(Material Design)。这份文档是动态更新的，将会随着我们对 Material Design 的探索而不断迭代、升级。

<p>

> contents：
> 
- goals
- principles

#### goals： ####

- goal1:
![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0Bx4BSt6jniD7QTA5cHFBUlV3RTA/materialdesign_goals_language.png)
> Create a visual language that synthesizes classic principles of good design with the innovation and possibility of technology and science.
> <p>
> 我们希冀创造一种新的视觉设计语言，能够遵循优秀设计的经典定则，同时还伴有创新理念和新的科技。

- goal2:
![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0Bx4BSt6jniD7c0R4RUtiTkhMZTQ/materialdesign_goals_system.png)
> Develop a single underlying system that allows for a unified experience across platforms and device sizes. Mobile precepts are fundamental, but touch, voice, mouse, and keyboard are all ﬁrst-class input methods.
> <p>
> 我们希望创造一种独一无二的底层系统，在这个系统的基础之上，构建跨平台和超越设备尺寸的统一体验。遵循基本的移动设计定则，同时支持触摸、语音、鼠标、键盘等输入方式。

#### principles: ####

- Material is the metaphor: 实体感就是隐喻
![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0Bx4BSt6jniD7VG9DQVluOFJ4Tnc/materialdesign_principles_metaphor.png)

> A material metaphor is the unifying theory of a rationalized space and a system of motion. The material is grounded in tactile reality, inspired by the study of paper and ink, yet technologically advanced and open to imagination and magic.
> <p>
> 通过构建系统化的动效和空间合理化利用，并将两个理念合二为一，构成了实体隐喻。与众不同的触感是实体的基础，这一灵感来自我们对纸墨的研究，但是我们相信，随着科技的进步，应用前景将不可估量。
> <p>
> Surfaces and edges of the material provide visual cues that are grounded in reality. The use of familiar tactile attributes helps users quickly understand affordances. Yet the flexibility of the material creates new affordances that supercede those in the physical world, without breaking the rules of physics.
> <p>
> 实体的表面和边缘提供基于真实效果的视觉体验，熟悉的触感让用户可以快速地理解和认知。实体的多样性可以让我们呈现出更多反映真实世界的设计效果，但同时又绝不会脱离客观的物理规律。
> <p>
> The fundamentals of light, surface, and movement are key to conveying how objects move, interact, and exist in space and in relation to each other. Realistic lighting shows seams, divides space, and indicates moving parts.
> <p>
> 光效、表面质感、运动感这三点是解释物体运动规律、交互方式、空间关系的关键。真实的光效可以解释物体之间的交合关系、空间关系，以及单个物体的运动。


- Bold, graphic, intentional: 鲜明、形象、深思熟虑
![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0Bx4BSt6jniD7NndTQW9VZTlZV2s/materialdesign_principles_bold.png)



> The foundational elements of print-based design – typography, grids, space, scale, color, and use of imagery – guide visual treatments. These elements do far more than please the eye. They create hierarchy, meaning, and focus. Deliberate color choices, edge-to-edge imagery, large-scale typography, and intentional white space create a bold and graphic interface that immerse the user in the experience.
> <p>
> 新的视觉语言，在基本元素的处理上，借鉴了传统的印刷设计——排版、网格、空间、比例、配色、图像使用——这些基础的平面设计规范。在这些设计基础上下功夫，不但可以愉悦用户，而且能够构建出视觉层级、视觉意义以及视觉聚焦。精心选择色彩、图像、选择合乎比例的字体、留白，力求构建出鲜明、形象的用户界面，让用户沉浸其中。
> <p>
> An emphasis on user actions makes core functionality immediately apparent and provides waypoints for the user.
> <p>
> Material Design 设计语言强调根据用户行为凸显核心功能，进而为用户提供操作指引。


- Motion provides meaning: 有意义的动画效果
![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0Bx4BSt6jniD7dkRYelJkeklqWFU/materialdesign_principles_motion.png)
> Motion respects and reinforces the user as the prime mover. Primary user actions are inflection points that initiate motion, transforming the whole design.
> <p>
> 动画效果(简称动效)可以有效地暗示、指引用户。动效的设计要根据用户行为而定，能够改变整体设计的触感。
> <p>
> All action takes place in a single environment. Objects are presented to the user without breaking the continuity of experience even as they transform and reorganize.
> <p>
> 动效应当在独立的场景呈现。通过动效，让物体的变化以更连续、更平滑的方式呈现给用户，让用户能够充分知晓所发生的变化。
> <p>
> Motion is meaningful and appropriate, serving to focus attention and maintain continuity. Feedback is subtle yet clear. Transitions are efﬁcient yet coherent.
> <p>
> 动效应该是有意义的、合理的，动效的目的是为了吸引用户的注意力，以及维持整个系统的连续性体验。动效反馈需细腻、清爽。转场动效需高效、明晰。

## What is material? ##
### Environment ###
![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0B7WCemMG6e0VVFpiZ041SmhwY2c/what_is_material_environment.png)

> Material design is a three-dimensional environment containing light, material, and cast shadows.
> <p>
> 原质化设计是一个包含光线，原质，阴影的三维环境

All material objects have x, y, and z dimensions.

All material objects have a single z-axis position.

Key lights create directional shadows, and ambient light creates soft shadows.

> contents:
>
- 3D world
- Light and shadow

#### 3D world ####
![3D space with x, y, and z axes](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0Bx4BSt6jniD7UXpQYWltVjNPWXc/whatismaterial_environment_3d.png)


> The material environment is a 3D space, which means all objects have x, y, and z dimensions. The z-axis is perpendicularly aligned to the plane of the display, with the positive z-axis extending towards the viewer. Every sheet of material occupies a single position along the z-axis and has a standard 1dp thickness, equivalent to one pixel of thickness on screens with a pixel density of 160.
> <p>
> Material 环境是一个三维的空间，这意味着每个对象都有 x ， y ， z 三维坐标属性，z 轴垂直于显示平面，并延伸向用户视角,每个 material 元素在 z 轴上占据一定的位置并且有一个 1dp 厚度的标准，当屏幕PPI是160时，长度刚好和1px一致，这个在我们以前的培训中讲过。
> <p>
> On the web, the z-axis is used for layering and not for perspective. The 3D world is emulated by manipulating the y-axis.
> <p>
> 在网页上，z 轴被用来分层而不是为了视角。3D 空间通过操纵 y 轴进行仿真。

#### Light and shadow ####

> Within the material environment, virtual lights illuminate the scene. Key lights create directional shadows, while ambient light creates soft shadows from all angles.
> <p>
> 在 material 环境中，虚拟的光线照射使场景中的对象投射出阴影，主光源投射出一个定向的阴影，而环境光从各个角度投射出连贯又柔和的阴影。
> <p>
> Shadows in the material environment are cast by these two light sources. In Android development, shadows occur when light sources are blocked by sheets of material at various positions along the z-axis. On the web, shadows are depicted by manipulating the y-axis only. The following example shows the card with a height of 6dp.
> <p>
> material 环境中的所有阴影都是由这两种光投射产生的.在Android开发中，当各个元素在z轴上占据了不同大小的位置遮挡住了这些光线，就产生了阴影。在web中我就不管了。

![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0B6Okdz75tqQsSFZUZ01GTk13T28/whatismaterial_environment_shadow1.png)
Shadow cast by key light

![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0B6Okdz75tqQsdDhaaTMwMTFVLTA/whatismaterial_environment_shadow2.png)
Shadow cast by ambient light

![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0B6Okdz75tqQsNnVmbTNMUF9DR0U/whatismaterial_environment_shadow3.png)
Combined shadow from key and ambient lights


### Material properties ###



> Material has certain immutable characteristics and inherent behaviors.
> <p>
> 材料拥有确定不变的特性和固定的行为

Material characteristics:

- Solid 固体的
- Occupies unique points in space 在空间中占据独一的点
- Impenetrable 不可穿透
- Mutable shape 形状可变
- Changes in size only along its plane 仅在自己的平面内变换大小
- Unbendable 专注表现
- Can join to other material 可以和其他原质结合
- Can separate, split, and heal 可以分隔、分割、恢复
- Can be created or destroyed 可以被创建或摧毁
- Moves along any axis 沿着任意轴移动



> Contents
> 
- Physical properties
- Transforming material
- Movement of material

#### Physical properties ####


> Material has varying x & y dimensions (measured in dp) and a uniform thickness (1dp).
> <p>
> 材料具有变化的长宽尺寸（以 dp 为计）和均匀的厚度（1dp）。

![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0B8v7jImPsDi-aTBFT1FDVEstenM/whatismaterial_materialproperties_physicalproperties_thickness_01_yes.png)

上图可取

![](https://material-design.storage.googleapis.com/publish/material_v_8/material_ext_publish/0B8v7jImPsDi-Sno0Qy1FY3UtaFk/whatismaterial_materialproperties_physicalproperties_thickness_02_no.png)

上图不可取



> Material casts shadows.
> <p>
> 原质产生阴影
> <p>
> Shadows result naturally from the relative elevation (z-position) between material elements.
> <p>
> 阴影是由于原质之间的相对高度（Z 轴位置）而自然产生的。

<video crossorigin="anonymous" loop="" controls="" width="740" height="350">
<source src="http://materialdesign.qiniudn.com/videos/inline%20whatismaterial-materialprop-physicalprop-PaperShadow_01_xhdpi_008.mp4">
</video>

符合规范的例子,阴影描述材料元件之间的相对高度。

[视频链接](http://materialdesign.qiniudn.com/videos/inline%20whatismaterial-materialprop-physicalprop-PaperShadow_01_xhdpi_008.mp4)

<video crossorigin="anonymous" loop="" controls="" width="740" height="350">
<source src="http://materialdesign.qiniudn.com/videos/inline%20whatismaterial-materialprop-physicalprop-PaperShadow_02_xhdpi_008.mp4">
</video>

不符合规范的例子,阴影随着材料高度的变化而产生变化。

[视频链接](http://materialdesign.qiniudn.com/videos/inline%20whatismaterial-materialprop-physicalprop-PaperShadow_02_xhdpi_008.mp4)



> Content is displayed on material, in any shape and color. Content does not add thickness to material.
> <p>
> 内容可被以任何形状和颜色显示在原质上。内容并不会增加原质的厚度。

<video crossorigin="anonymous" loop="" controls="" width="740" height="350">
<source src="http://materialdesign.qiniudn.com/videos/whatismaterial-materialprop-physicalprop-InkDisplay_xhdpi_006.mp4">
</video>

符合规范的例子，材料能展示任何形状和颜色。
[视频链接](http://materialdesign.qiniudn.com/videos/whatismaterial-materialprop-physicalprop-InkDisplay_xhdpi_006.mp4)

> Content can behave independently of the material, but is limited within the bounds of the material.
> <p>
> 内容的展示能够独立于材料,但要被限制在材料的范围里。

<video crossorigin="anonymous" loop="" controls="" width="740" height="350">
<source src="http://materialdesign.qiniudn.com/videos/whatismaterial-materialprop-physicalprop-InkBehavior_xhdpi_006.mp4">
</video>

符合规范的例子，内容的行为能独立于材料的行为。
[视频链接](http://materialdesign.qiniudn.com/videos/whatismaterial-materialprop-physicalprop-InkBehavior_xhdpi_006.mp4)


#### Transforming material ####







#### Movement of material ####





### Elevation and shadows ###










