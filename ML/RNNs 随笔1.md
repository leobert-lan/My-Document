原文链接[这里](http://blog.csdn.net/heyongluoyao8/article/details/48636251)
## 什么是RNNs
RNNs的目的使用来处理序列数据。

在传统的神经网络模型中，是从输入层到隐含层再到输出层，层与层之间是全连接的，每层之间的节点是无连接的。但是这种普通的神经网络对于很多问题却无能无力。

RNNs之所以称为循环神经网路，即一个序列当前的输出与前面的输出也有关。

## RNNs能干什么
主要用于NLP领域。如词向量表达、语句合法性检查、词性标注等。

在RNNs中，目前使用最广泛最成功的模型便是**LSTMs(Long Short-Term Memory，长短时记忆模型)模型**

* 语言模型与文本生成(Language Modeling and Generating Text)
* 机器翻译(Machine Translation)
* 语音识别(Speech Recognition)
* 图像描述生成 (Generating Image Descriptions)

## 如何训练RNNs

对于RNN是的训练和对传统的ANN训练一样。同样使用BP误差反向传播算法，不过有一点区别：

如果将RNNs进行网络展开，那么参数W,U,V是共享的，而传统神经网络却不是的。

并且在使用梯度下降算法中，每一步的输出不仅依赖当前步的网络，并且还以来前面若干步网络的状态。比如，在t=4时，我们还需要向后传递三步，已经后面的三步都需要加上各种的梯度。该学习算法称为Backpropagation Through Time (BPTT)。

在vanilla RNNs训练中，BPTT无法解决长时依赖问题(即当前的输出与前面很长的一段序列有关，一般超过十步就无能为力了)，因为BPTT会带来所谓的梯度消失或梯度爆炸问题(the vanishing/exploding gradient problem)。当然，有很多方法去解决这个问题，如LSTMs便是专门应对这种问题的。

----

对于RNNs，暂时没有啥理解，将结合LSTM和实际应用进行一定深入研究
