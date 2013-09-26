# 页面聚类包使用说明
----

## 使用方法
1. 导入jar包。
2. 在jar包同层目录下建/config目录，并放入配置文件`config.xml`。
3. 声明接口所在包名：`package com.datamining.pagecluster.app;`

## 代码说明
接口掩盖了一切底层操作，只需2-3行代码即可完成聚类。

<pre>
// 初始化操作
// param1：html文件所在的文件夹（程序将读取改文件夹下的所有html文件，包括子目录）。
// param2：归类后文件的输出目录。
// param3：采用的归类方法，1表示折叠算法，2表示vision-base方法。
App app = new App("C:/Users/Admin/Desktop/douban/",
		    "C:/Users/Admin/Desktop/douban-out/",1);
//app.setMethod(2); //设置方法是采用基于内容的折叠算法还是vision-base的方法，前者参数设置为1，后者参数设置为2（默认设置为1，错误设置也将被强行设置为1）。
// 进行聚类操作。
app.cluster();
</pre>

## 配置文件
配置文件至关重要，直接影响了最终的聚类结果。

配置文件放在/config目录下。

配置项解释：

* Config>Content>NestThreshold： 树的子树之间的相似度如果小于这个值，将不会进行折叠处理。
* Config>PageSimilarityThreshold: 基于折叠算法进行聚类时，当页面之间的相似度小于这个值聚类将停止。
* Config>Vision>Threshold: 基于vision-based方法进行聚类时，当页面间vison相似度小于这个值聚类将停止。

配置文件：
<pre>
<?xml version="1.0" encoding="UTF-8"?>
<Config>
    <Content>
        <NestThreshold>0.8</NestThreshold>
        <PageSimilarityThreshold>0.7</PageSimilarityThreshold>
    </Content>
    <Vision>
        <Threshold>0.5</Threshold>
    </Vision>
</Config>
</pre>