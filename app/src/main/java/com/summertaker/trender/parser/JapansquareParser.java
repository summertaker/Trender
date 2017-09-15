package com.summertaker.trender.parser;

import android.util.Log;

import com.summertaker.trender.common.BaseParser;
import com.summertaker.trender.model.Article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class JapansquareParser extends BaseParser {

    public void parseList(String response, ArrayList<Article> Articles) {
        /*
        <Article>
            <div class="innerHead">
                <div class="box-date">
                    <time>2016.4</time><time>01</time>
                </div>
                <div class="box-ttl">
                    <h3>
                        <a href="/mob/news/diarKijiShw.php?site=k46o&ima=4841&id=2349&cd=member">
                            日常のなかにプロレスあり～114～まさにこの通りです。
                        </a>
                    </h3>
                    <p class="name">
                        尾関 梨香
                    </p>
                </div>
                <div class="box-sns"></div>
            </div>
            <div class="box-Article">
                <div><br><br>こんばんは〜</div><div><br></div><div><br></div><div><div>4月1日、新年度になりました!</div>
                <img src="/files/14/diary/k46/member/moblog/201604/mob8SaU44.jpg" alt="image1.JPG" id="2E79D24E-D688-4812-A8F1-09B6F552A594">
            </div>
            <div class="box-bottom">
                <ul>
                    <li>2016/04/01 23:40</li>
                    <li class="singlePage">
                        <a href="/mob/news/diarKijiShw.php?site=k46o&ima=4841&id=2349&cd=member">個別ページ</a>
                    </li>
                </ul>
            </div>
        </Article>
        <Article>
            ....
        </Article>
        */

        //Log.e(TAG, response);

        Document doc = Jsoup.parse(response);

        Element root = doc.select("table.bd_lst > tbody").first();
        //Log.e(TAG, root.html());

        for (Element tr : root.select("tr")) {

            if (tr.attr("class").contains("notice")) {
                continue;
            }

            Elements tds = tr.select("td");
            if (tds.size() != 6) {
                continue;
            }

            String title;
            String date;
            String content;
            String url;
            boolean hasImage = false;

            Element tit = tds.get(2);
            Element dat = tds.get(3);

            title = tit.text();
            date = dat.text();
            url = "http://theqoo.net" + tit.select("a").first().attr("href");

            Element img = tit.select("img").first();
            if (img != null) {
                hasImage = true;
            }

            /*ArrayList<String> imageUrls = new ArrayList<>();
            ArrayList<String> thumbnails = new ArrayList<>();

            for (Element img : el.select("img")) {
                String src = img.attr("src");
                src = "http://www.keyakizaka46.com" + src;

                thumbnails.add(src);
                imageUrls.add(src);
            }
            */

            //Log.e(TAG, title + " " + url);

            Article article = new Article();
            article.setTitle(title);
            article.setDate(date);
            //Article.setContent(content);
            article.setUrl(url);
            article.setHasImage(hasImage);

            //Article.setThumbnails(thumbnails);
            //Article.setImageUrls(imageUrls);

            Articles.add(article);

        }
    }

    public ArrayList<String> parseDetail(String response) {
        ArrayList<String> result = new ArrayList<>();

        Document doc = Jsoup.parse(response);

        Element root = doc.select(".xe_content").first();
        if (root != null) {
            //Log.e(TAG, root.html());

            for (Element img : root.select("img")) {
                String src = img.attr("src");
                //src = "http://www.keyakizaka46.com" + src;
                //Log.e(TAG, src);

                result.add(src);
            }
        }

        return result;
    }
}
