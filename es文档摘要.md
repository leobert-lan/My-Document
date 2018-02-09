# 请求体查询



## 空查询

```
GET /_search
{
}
```

等价于

```
GET /_search
{
    "query": {
        "match_all": {}
    }
}
```
这是一种查询表达式

## 查询表达式

```
GET /_search
{
    "query": YOUR_QUERY_HERE
}
```

### 查询语句的结构
```
{
    QUERY_NAME: {
        ARGUMENT: VALUE,
        ARGUMENT: VALUE,...
    }
}
```

### 合并查询语句
查询语句(Query clauses) 就像一些简单的组合块 ，这些组合块可以彼此之间合并组成更复杂的查询。这些语句可以是如下形式：

* 叶子语句（Leaf clauses） (就像 match 语句) 被用于将查询字符串和一个字段（或者多个字段）对比。
* 复合(Compound) 语句 主要用于 合并其它查询语句。 比如，一个 bool 语句 允许在你需要的时候组合其它语句，无论是 must 匹配、 must_not 匹配还是 should 匹配，**同时它可以包含不评分的过滤器（filters）**

## 查询与过滤
* 过滤：只是简单的问一个问题：“这篇文档是否匹配？”。回答也是非常的简单，yes 或者 no ，二者必居其一。 es2.0已移除
* 查询：评分问题

> 但从 Elasticsearch 2.0 开始，过滤（filters）已经从技术上被排除了，同时所有的查询（queries）拥有变成不评分查询的能力。


## 查询类型 QUERY_NAME
* match_all 
* match
* multi_match
* range
* term
* terms
* exists
* missing

### match_all

查询简单的 匹配所有文档。在没有指定查询方式时，它是默认的查询：

```
{ "match_all": {}}
```


### match

无论你在任何字段上进行的是全文搜索还是精确查询，match 查询是你可用的标准查询。

* 如果你在一个全文字段上使用 match 查询，在执行查询前，它将用正确的分析器去分析查询字符串：

```
{ "match": { "tweet": "About Search" }}
```

* 如果在一个精确值的字段上使用它， 例如数字、日期、布尔或者一个 not_analyzed 字符串字段，那么它将会精确匹配给定的值：

```
{ "match": { "age":    26           }}
{ "match": { "date":   "2014-09-01" }}
{ "match": { "public": true         }}
{ "match": { "tag":    "full_text"  }}
```

> 对于精确值的查询，可能需要使用 filter 语句来取代 query，因为 filter 将会被缓存。



### multi_match

multi_match 查询可以在多个字段上执行相同的 match 查询：

```
{
    "multi_match": {
        "query":    "full text search",
        "fields":   [ "title", "body" ]
    }
}
```

### range

range 查询找出那些落在指定区间内的数字或者时间：

```
{
    "range": {
        "age": {
            "gte":  20,
            "lt":   30
        }
    }
}
```

被允许的操作符如下：

* gt 大于
* gte 大于等于
* lt 小于
* lte 小于等于


### term

term 查询被用于**精确值** 匹配，这些精确值可能是数字、时间、布尔或者**那些 `not_analyzed` 的字符串**：

```
{ "term": { "age":    26           }}
{ "term": { "date":   "2014-09-01" }}
{ "term": { "public": true         }}
{ "term": { "tag":    "full_text"  }}
```

term 查询对于输入的文本**不分析** ，所以它将给定的值进行精确查询。

### terms
terms 查询和 term 查询一样，但它允许你指定多值进行匹配。如果这个字段包含了指定值中的任何一个值，那么这个文档满足条件：

```
{ "terms": { "tag": [ "search", "full_text", "nosql" ] }}
```


和 `term` 查询一样，terms 查询对于输入的文本不分析。它查询那些精确匹配的值*（包括在大小写、重音、空格等方面的差异）*。

### exists & missing 

exists 查询和 missing 查询被用于查找那些指定字段中有值 (exists) 或无值 (missing) 的文档。这与SQL中的 IS_NULL (missing) 和 NOT IS_NULL (exists) 在本质上具有共性：

```
{
    "exists":   {
        "field":    "title"
    }
}
```

这些查询经常用于某个字段有值的情况和某个字段缺值的情况。


## 组合多查询
将多个查询组合成一条查询

而且将组合为bool查询，接受以下参数

* must 文档 必须 匹配这些条件才能被包含进来。
* must_not 文档 必须不 匹配这些条件才能被包含进来。
* should 如果满足这些语句中的任意语句，将增加 _score ，否则，无任何影响。它们主要用于修正每个文档的相关性得分。
* filter 必须 匹配，但它以不评分、过滤模式来进行。这些语句对评分没有贡献，只是根据过滤标准来排除或包含文档。

e.g.:

```
{
    "bool": {
        "must":     { "match": { "title": "how to make millions" }},
        "must_not": { "match": { "tag":   "spam" }},
        "should": [
            { "match": { "tag": "starred" }},
            { "range": { "date": { "gte": "2014-01-01" }}}
        ]
    }
}
```

> 如果没有 must 语句，那么至少需要能够匹配其中的一条 should 语句。**但，如果存在至少一条 must 语句，则对 should 语句的匹配没有要求**。

当我们需要查询： `A且（B或C）`时，**这样的语句是错的**：

```
{
	"bool": {
		"must": {A},
		"should":{
			B,
			C
		}
	}
}
```

能够得到正确的结构是需要增加一层bool嵌套

```
{
	"bool": {
		"must": [
			{A}，
			{
				“bool”: {
					"should": {
						B,
						C
					}
					...
				}
			}
		]
		...
	}
}
```

并且，在可以减少嵌套的情况下移除外层的嵌套。

> constant_score 查询经常用于你只需要执行一个 filter 而没有其它查询（例如，评分查询）的情况下。可以使用它来取代只有 filter 语句的 bool 查询。


# 排序
