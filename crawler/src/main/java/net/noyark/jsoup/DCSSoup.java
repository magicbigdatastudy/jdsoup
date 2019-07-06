package net.noyark.jsoup;


//海量数据的爬取
//ES mycat是爬虫的落地位置，redis和rabbitmq实现分布式爬取
//爬取三级分类->放到rabbitmq队列->各主机作为消费者抢夺->抢到爬其他页面
//反爬虫
//定期修改页面结构
//定期修改代码
//设置黑名单：单位事件访问次数过多，封锁ip，时间限制
//爬虫技术的头判断
    //用Jsoup伪装头
    //利用获取的所有浏览器的user-agent头信息
    //封装static静态方法
    //s随机调用任何一个头，进行每次连接的user-agent封装
public class DCSSoup {
    //存储技术，落地相关
        //mysql
        //es
    //分布式
        //rabbitmq
        //redis
    //产品介绍
        //httpclient
        //httpUnit
        //j soup
        //python
    //解决问题
        //网页
        //网站结构
        //二次提交
        //登录
            //cookie
        //反爬虫
            //改结构
            //黑名单
            //判断头
}
