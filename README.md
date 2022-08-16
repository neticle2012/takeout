# takeout（Java外卖项目）

## 项目介绍

本项目为B站黑马程序员瑞吉外卖项目的扩展，视频地址：<a href="https://www.bilibili.com/video/BV13a411q753">https://www.bilibili.com/video/BV13a411q753</a>

## 技术栈

- 前端
    - Vue
    - Element-UI
- 后端
    - SpringBoot 2.7.2
    - MyBatisPlus 3.5.1
    - MySQL 5.7
    - Redis 5.0.9
    - Nginx 1.12.2

## 项目特征

在原有项目的基础上完善了以下功能：

- 后台起售/停售和批量起售/停售菜品、删除和批量删除菜品
- 后台修改套餐、起售/停售和批量起售/停售套餐、删除和批量删除套餐
- 将前台短信登录改为电子邮件登录
- 前台修改收货地址、删除收货地址
- 前台减少购物车、点击套餐图片显示套餐中的具体菜品
- 前台用户查看自己的订单、再来一单、退出登录
- 后台按条件查看和展示客户订单、修改订单状态

## 项目说明

项目文件结构：

.
|-- README.md  
|-- imgs/：菜品/套餐图片资源  
|-- pom.xml  
|-- src/  
|-- takeout.sql：项目数据库备份的SQL文件，在MySQL客户端运行`source takeout.sql`以恢复数据库中的数据  
`-- 外卖项目.xmind：项目笔记，其中每节都与git的版本相对应

首先打开`application.yml`文件，修改为自己的配置（MySQL主从库配置、电子邮件配置、Redis配置等），然后启动对应的MySQL、Redis、Nginx服务，再启动项目，

- 前台登录浏览器访问 http://localhost:8080/front/page/login.html
- 后台登录浏览器访问 http://localhost:8080/backend/page/login/login.html

