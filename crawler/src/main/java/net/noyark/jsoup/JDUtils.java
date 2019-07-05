package net.noyark.jsoup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JDUtils {
    //获取三级分类网页list的静态方法，调用test测试
    public static List<String> getCatUrls(String url) throws IOException {
        //根据调用静态方法，传入一个总页面连接地址
        //通过这个连接地址，jsoup获取所有三级分类的a标签
        //准备一个返回对象
        List<String> catUrlList = new ArrayList<>();
        //Jsoup，doc对象选择器获取eles
        Elements eles = Jsoup.connect(url).get().select("div dl dd a");
        for(Element ele : eles){
            String catUrl = ele.attr("href");
            boolean have = catUrl.startsWith("//list.jd.com/list.html?cat=");
            if(have) {
                catUrlList.add("https:"+catUrl);
            }
        }
        return catUrlList;
    }
    //分页集合
    //目标是获取商品的页面连接集合，进入三级分类，发现商品连接a标签
    //但是不能直接获取，因为有分页，分页的连接地址，就是当前三级
    //分类+&page=页数；必须获取所有页数的连接地址，分类地址上拼接

    /**
     * 获取某个分类页面的总页数
     * @throws IOException
     * https://list.jd.com/list.html?cat=737,794,12392
     */
    public static Integer getPageNum(String url){
        try {
            return Integer.parseInt(Jsoup.connect(url).get().select("#J_topPage span i").get(0).text());
        }catch (IOException e){
            //输出日志，或者将信息整理，放入rabbitmq
            //携带一个error routingKey
            System.out.println(url);
            return 0;
        }
    }

    /**
     * 利用写好的页数查询
     * @throws IOException
     */
    public static List<String> getPageUrls(String url){
        Integer pageNum = getPageNum(url);
        List<String> pageUrlList = new ArrayList<>();
        for(int i = 1;i<=pageNum;i++){
            String pageUrl = url+"&page="+pageNum;
            pageUrl = pageUrl.replace("https://","http://");
            pageUrlList.add(pageUrl);
        }
        return pageUrlList;
    }

    /**
     * 从分页获取所有商品a标签每页数据60个
     * @param url
     * @return
     */
    public List<String> getItemUrls(String url){
        List<String> itemUrlList = new ArrayList<>();
        try {
            Elements elements = Jsoup.connect(url).get().select(".p-img a");
            for(Element ele:elements){
                String href = ele.attr("href");
                itemUrlList.add(href);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(url);
        }
        return itemUrlList;
    }

    /**
     * 从商品连接中封装对象
     * @throws IOException
     */
    public static Long getId(String url){
        String id = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf(".html"));
        return Long.parseLong(id);
    }
    public static String getTitle(String url){
        try{
            Elements eles = Jsoup.connect("http:"+url).get().select(".itemInfo-wrap .sku-name");
            return eles.get(0).text();
        }catch (Exception e){
            return "";
        }
    }
    public static String getSellPoint(){
        return "";
    }
    /*
    sellPoint 二次提交
    http://ad.3.cn/ads/mgets?skuids=AD_6627499
     */
    public static final ObjectMapper mapper = new ObjectMapper();
    public static String getSellPoint(String url){
        Long id = getId(url);
        try{
            url = "http://ad.3.cn/ads/mgets?skuids=AD_"+id;
            Connection.Response response = Jsoup.connect(url).ignoreContentType(true).execute();
            return mapper.readTree(response.body()).get(0).get("ad").asText();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    @Test
    public void test() throws IOException{
        List<String> cats = getCatUrls("http://www.jd.com/allSort.aspx");
        for(String cat:cats){
            List<String> urls = getPageUrls(cat);
            for(String url:urls){
                List<String> itemLists = getItemUrls(url);
                for(String item:itemLists){
                    long id = getId(item);
                    String title = getTitle(item);
                    String sellPoint = getSellPoint(item);
                    Item itemO = new Item();
                    itemO.setId(id);
                    itemO.setTitle(title);
                    itemO.setSellPoint(sellPoint);
                    System.out.println(itemO);
                }
            }
        }
    }
}
