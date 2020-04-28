package callixtegirard.scrape;

import static callixtegirard.util.Debug.*;

import okhttp3.HttpUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.net.URL;


public class ScrapeDealabs
{
    private static final String urlRoot = "https://www.dealabs.com/";
    private static final String[] urlsSections = {"hot"};

    public static void main( String[] args ) throws IOException
    {
        try {
//            ChromeDriver browser = new ChromeDriver();

            int pageIndex = 0;
            boolean finished = false;
            while (!finished)
            {
                pageIndex ++;

//                URL url = HttpUrl.parse("http://example.com:4567/foldername/1234?abc=xyz").url(); // to parse
                HttpUrl url = new HttpUrl.Builder()
                        .scheme("https")
                        .host("dealabs.com")
                        .addPathSegment("hot")
                        .addQueryParameter("page", String.valueOf(pageIndex))
                        .build();
                d(url);
                Document doc = Jsoup.connect(url.toString()).get();
//                d(doc);

                Elements articles = doc.getElementsByTag("article");
                for (Element article : articles)
                {
//                    d(article);
//                    Elements articleSections = article.getElementsByAttributeValueStarting("class", "threadGrid");
                    Element articleImage = article.getElementsByAttributeValueStarting("class", "threadGrid-image").first();
                    Element articleHeader = article.getElementsByAttributeValueStarting("class", "threadGrid-headerMeta").first();
                    Element articleTitle = article.getElementsByAttributeValueStarting("class", "threadGrid-title").first();
                    Element articleBody = article.getElementsByAttributeValueStarting("class", "threadGrid-body").first();
                    Element articleFooter = article.getElementsByAttributeValueStarting("class", "threadGrid-footerMeta").first();
//                    dL(articleImage, articleHeader, articleTitle, articleBody, articleFooter);

                    // 1) image
                    String imageURL = articleImage.getElementsByTag("a").first().attr("href"); // not the image url !
                    d(imageURL);

                    // 2)a) temperature & deal status
                    Element temperatureBox = articleHeader.getElementsByAttributeValueContaining("class", "vote-box").first();
                    boolean dealExpired = temperatureBox.attr("class").contains("vote-box--muted");
                    String temperatureRaw = temperatureBox.text().trim();
                    int temperature = Integer.parseInt(temperatureRaw.split(" ")[0].split("°")[0]);
                    d(dealExpired, temperature);

                    // 2)b) expiration date, place and publication date

                    d(l);
                }
                d("page n°", pageIndex, "contains", articles.size(), "articles");
                d(s);
            }

        } catch (IOException exc) {
            throw exc;
        }
    }
}
