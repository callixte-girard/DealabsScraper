package callixtegirard.scrape;

import static callixtegirard.util.Debug.*;

import callixtegirard.model.Deal;
import callixtegirard.reference.model.Attribute;
import callixtegirard.reference.model.AttributeStatus;
import callixtegirard.reference.model.ExpiringAttribute;
import okhttp3.HttpUrl;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class ScrapeDealabs
{
    private static final String urlRoot = "https://www.dealabs.com/";
    private static final String[] urlsSections = {"hot"};

    public static void main( String[] args ) throws IOException, URISyntaxException
    {
        try {
//            ChromeDriver browser = new ChromeDriver();

            int pageIndex = 0;
            boolean finished = false;
            while (!finished)
            {
                pageIndex ++;

                /*HttpUrl url = new HttpUrl.Builder()
                        .scheme("https")
                        .host("dealabs.com")
                        .addPathSegment("hot")
                        .addQueryParameter("page", String.valueOf(pageIndex))
                        .build();*/
                String scheme = "https";
                String authority = "dealabs.com";
                String path = "/hot";
                String query = "page=" + pageIndex;
                String fragment = null;
                URI uri = new URI(scheme, authority, path, query, fragment);
                URL url = uri.toURL();
//                d(url);

                Document doc = Jsoup.connect(url.toString()).get();
//                d(doc);

                Elements deals = doc.getElementsByTag("article");
                for (Element deal : deals)
                {
                    // get deal detail url
                    Element link = deal.getElementsByAttributeValueContaining("class", "linkPlain").first();
                    String dealUrl = link.attr("href");
//                    d(dealUrl);

                    // scrape infos in Preview View (not necesary here)
//                    String dealName = link.text();
//                    d(dealName);

                    // scrape infos in Detail View
                    scrapeDeal(dealUrl);
                    d(l);

                    // to test
                    finished = true;
                }
                d("page n°", pageIndex, "contains", deals.size(), "articles");
                d(s);
            }

        } catch (IOException exc) {
            throw exc;
        }
    }

    private static void scrapeDeal(String urlString) throws MalformedURLException, IOException
    {
        URL url = new URL(urlString);
//        d(url.getPath()); // bon à savoir !

        Deal deal = new Deal(url);

        Document doc = Jsoup.connect(urlString).get();

        Element threadItem = doc.getElementsByAttributeValueStarting("class", "threadItem").first();

        Element articleHeader = threadItem.getElementsByAttributeValueStarting("class", "threadItem-headerMeta").first();
        Element articleTitle = threadItem.getElementsByAttributeValueStarting("class", "threadItem-title").first();
        Element articleBody = threadItem.getElementsByAttributeValueStarting("class", "threadItem-body").first();
        Element articleFooter = threadItem.getElementsByAttributeValueStarting("class", "threadItem-footerMeta").first();

        // 1) image
        Element threadImage = threadItem.getElementsByAttributeValueStarting("class", "threadItem-image").first();
        String imageURL = threadImage.getElementsByTag("img").first().attr("src");
        d(imageURL);

//        // 2)a) temperature & deal status
//        Element temperatureBox = articleHeader.getElementsByAttributeValueContaining("class", "vote-box").first();
//        boolean dealExpired = temperatureBox.attr("class").contains("vote-box--muted");
//        String temperatureRaw = temperatureBox.text().trim();
//        int temperatureValue = Integer.parseInt(temperatureRaw.split(" ")[0].split("°")[0]);
////                    d(dealExpired, temperatureValue);
//        Attribute temperature = new ExpiringAttribute(dealExpired, temperatureValue);
//        deal.setTemperature(temperature);

        // 2)b) expiration date, place and publication date

    }
}
