package com.tongtu.xzqh;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import com.tongtu.xzqh.entity.AhuiCode2021;
import com.tongtu.xzqh.entity.Updateurl;
import com.tongtu.xzqh.repository.AhuiCode2021Repository;
import com.tongtu.xzqh.repository.UpdateurlRepository;
import com.tongtu.xzqh.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;

import org.jsoup.select.Elements;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
class XzqhApplicationTests {
    @Autowired
    AhuiCode2021Repository ahuiCode2021Repository;
    @Autowired
    UpdateurlRepository updateurlRepository;
    String hostMast = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2020/";
    List<Updateurl> urlList = new ArrayList<Updateurl>();
    List<AhuiCode2021> ahuiCode2021s = new ArrayList<AhuiCode2021>();

    @Test
    void contextLoads() {
    }

    /**
     * 发送 get 请求
     *
     * @param url 请求地址
     * @return 请求结果
     */
    public String get(String url) {
        String result = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(hostMast + url);
            URI uri = builder.build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            // 执行请求
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), "GBK");
                Optional<Updateurl> updateurl = updateurlRepository.findById(url);
                updateurl.get().setIsup(Boolean.TRUE);
                updateurlRepository.save(updateurl.get());
//                urlList.put(url, Boolean.TRUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Test
    void httpUtilsLoads() {
        urlList = updateurlRepository.findUpdateurlByIsup();
//        urlList.put("34.html", Boolean.FALSE);
        if (urlList.size() > 0) {
            loadsCode();
        }

    }

    void loadsCode() {
        List<Updateurl> collect = urlList.stream().collect(Collectors.toList());
        collect.forEach(k -> {
            String baseurl = "";
            String pcode = "";
            if (k.getUrl().contains("/")) {
                pcode = k.getUrl().substring(k.getUrl().lastIndexOf("/") + 1, k.getUrl().lastIndexOf("."));
                baseurl = k.getUrl().substring(0, k.getUrl().lastIndexOf("/") + 1);
            } else {
                pcode = k.getUrl().substring(0, k.getUrl().lastIndexOf("."));
                baseurl = "";
            }
            pcode = StringUtils.rightPad(pcode, 12, "0");
            log.info("pcode : {} ", pcode);
            String text = get(k.getUrl());
            Document parse = Jsoup.parse(text);
            Elements table = parse.select("table[class*=table] tr");
            Elements aselect = table.select("a[href]");
            List<Indexed<Element>> aselectOptional = StreamUtils.zipWithIndex(aselect.stream()).filter(i -> i.getIndex() % 2 == 0).collect(Collectors.toList());
            String finalBaseurl = baseurl;
            aselectOptional.forEach(elementIndexed -> {
                Updateurl updateurl = new Updateurl();
                updateurl.setUrl(finalBaseurl + elementIndexed.getValue().attr("href"));
                updateurl.setIsup(Boolean.FALSE);
                updateurlRepository.save(updateurl);
            });
            ahuiCode2021s = new ArrayList<AhuiCode2021>();
            String finalPcode = pcode;
            if (aselect.size() == 0) {
                table.next().forEach(element -> {
                    Elements childrenelements = element.children();
                    Optional<Indexed<Element>> stringCodeOptional = StreamUtils.zipWithIndex(childrenelements.stream()).filter(i -> i.getIndex() == 0).findAny();
                    Optional<Indexed<Element>> stringCxfxdmOptional = StreamUtils.zipWithIndex(childrenelements.stream()).filter(i -> i.getIndex() == 1).findAny();
                    Optional<Indexed<Element>> stringNameOptional = StreamUtils.zipWithIndex(childrenelements.stream()).filter(i -> i.getIndex() == 2).findAny();
//                    log.info("{}{}{}", stringCodeOptional.get().getValue().text(), stringCxfxdmOptional.get().getValue().text(), stringNameOptional.get().getValue().text());
                    AhuiCode2021 ahuiCode2021 = new AhuiCode2021();
                    ahuiCode2021.setCode(stringCodeOptional.get().getValue().text());
                    ahuiCode2021.setCxfxdm(stringCxfxdmOptional.get().getValue().text());
                    ahuiCode2021.setName(stringNameOptional.get().getValue().text());
                    ahuiCode2021.setLevel(5);
                    ahuiCode2021.setPcode(finalPcode);
                    ahuiCode2021s.add(ahuiCode2021);
                });
            } else {
                table.next().forEach(element -> {
                    int countlength = StringUtils.countMatches(k.getUrl(), '/');
                    Elements childrenelements = element.children();
                    Optional<Indexed<Element>> stringCodeOptional = StreamUtils.zipWithIndex(childrenelements.stream()).filter(i -> i.getIndex() == 0).findAny();
                    Optional<Indexed<Element>> stringNameOptional = StreamUtils.zipWithIndex(childrenelements.stream()).filter(i -> i.getIndex() == 1).findAny();
                    log.info("{}{}", stringCodeOptional.get().getValue().text(), stringNameOptional.get().getValue().text());
                    AhuiCode2021 ahuiCode2021 = new AhuiCode2021();
                    ahuiCode2021.setCode(stringCodeOptional.get().getValue().text());
                    ahuiCode2021.setName(stringNameOptional.get().getValue().text());
                    ahuiCode2021.setLevel(countlength + 2);
                    ahuiCode2021.setPcode(finalPcode);
                    ahuiCode2021s.add(ahuiCode2021);
                });
            }
            ahuiCode2021Repository.saveAll(ahuiCode2021s);

        });
        urlList = updateurlRepository.findUpdateurlByIsup();
        if (urlList.size() > 0) {
            loadsCode();
        }
    }
}
