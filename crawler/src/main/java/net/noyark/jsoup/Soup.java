package net.noyark.jsoup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

//JSOUP
//爬取的是页面的数据，都是以html格式显示
//需要解决分析页面出现的多种问题
//1 如何抓取整个页面
//2 如何抓取一个网站的所有页面(a标签位置的分析)
//3 如何定位一个准确信息的内容
//4 两次提交如何抓取
//5 登录·
public class Soup {
    /**
     * 抓取整个页面，了解jsoup如何连接URL
     */
    @Test
    public void onePage() throws IOException {

        String url="https://item.jd.com/2943313.html";
        Connection connection = Jsoup.connect(url);
        Connection.Response response = connection.execute();
        String str = response.body();
        System.out.println(str);
    }
    /**
     * 利用a标签，获取网页的所有连接，利用for循环无限获取所有页面的所有a标签
     * 理论上，循环达到一定次数，网站所有连接都能获取
     */
    @Test
    public void oneSite() throws IOException{
        //jsoup中将爬取的整个页面可以解析成Document对象
        //整个Document对象与js的一致，可以利用这个对象，定位
        //包括id，class，标签名称等内容，从而获取属性，值等
        //数据
        //利用Document对象，getElementsByName("a")
        String url = "https://item.jd.com/2943313.html";
        //返回代表当前页面解析html文本对象
        Document document = Jsoup.connect(url).get();
        //获取a标签
        Elements elements = document.getElementsByTag("img");
        Elements elements2 = document.getElementsByTag("a");
        for(Element e:elements2){
            //ele表示一个java对象，代表这个a标签
            String href = e.attr("href");
            System.out.println(href);
        }
        //会获取a标签后，就拿到的了当前页面的下级连接地址
        //这种是代码逻辑的下级标签，
        //经过对网站的·结构分析，从上到下的层次获取所有的a标签
        //连接地址
        //才是爬取整个网站的正确思路
    }

    public void oneFraction() throws Exception{
        //<div class="item ellipsis" title="华为荣耀10"》
        //华为荣耀10</div>,需要的数据，在div标签里，首先定位到·div
        String url = "https://item.jd.com/2943313.html";
        //带空格的class在定位时不能将空格写在字符串
        Elements all = Jsoup.connect(url).get().select(".item").select(".xx");
        for(Element ele:all){
            String str = ele.text();
            System.out.println(str);
        }
    }
    /**
     * 二次提交，导致第一次访问的url获取数据失败，本次访问
     * 的url不会返回第二次提交的数据，网页加载完成后的js继续发起的
     *
     */
    @Test
    public void price() throws Exception{
        String url = "https://item.jd.com/2943313.html";
        //Elements eles = Jsoup.connect(url).get().select(".dd span span");
        //将结果输出，发现价钱为空
        //找到二次提交的url连接，通过jsoup获取数据
        //f12找到这个连接，二次连接是
        //http://p.3.cn/prices/mgets?skuids=J_2943313
        String url2 = "http://p.3.cn/prices/mgets?skuids=J_2943313";
        Connection.Response response = Jsoup.connect(url2).ignoreContentType(true).execute();
        String priceJson = response.body();
        //利用jsonNode：将json字符串转化为有数据结构的对象
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(priceJson);
        JsonNode node1 = node.get(0).get("p");
        //二次提交时，要求
        String priceStr = node1.asText();
        Long price = Long.parseLong(priceStr.replace(".",""));
        System.out.println(price);
    }

    //登录连接
    //https://order.jd.com/center/list.action 我的订单
    //必须登录才能访问
    @Test
    public void loginTest() throws IOException{
        String url = "https://order.jd.com/center/list.action";
        /**
         * 假如Cookie是USER_FLAG_CHECK xxxxxxx
         */
//        for(Element ele:eles){
//            String myOrder = ele.text();
//            System.out.println(myOrder);
//        }
        Elements eles = Jsoup.connect(url).cookie("USER_FLAG_CHECK","xxxx").get().select("#order01 div h3");


    }

}
