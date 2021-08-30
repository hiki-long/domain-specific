# 领域2 ToDo

+ controller移到service
+ 加入事务管理
+ 商品数量增减
+ 后台管理界面
+ 推荐模型
+ 数据库搜索优化
+ 买卖家聊天
+ 登录短信验证

## Sonic搜索优化

### 工作流程

1. 将mysql中数据添加到sonic中
2. 每次启动后端时进行初始化
3. 在后端进行数据库操作时同时加入sonic
4. 搜索时首先搜索sonic，如果没有命中则搜索mysql
5. 若出现在mysql中命中的情况，则进行一次对此bucket的同步

### Bucket定义

1. item-tag
2. item-name
3. shop-name

## 服务端docker构建

1. spark
2. hbase
3. mysql
4. sonic
5. java(后端)
6. react(前端)
7. 自动同步