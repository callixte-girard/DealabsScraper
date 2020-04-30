package callixtegirard.scrape;

import static callixtegirard.util.Debug.*;

import callixtegirard.model.Item;
import callixtegirard.reference.model.Attribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class ScrapeDealabs
{
    // debug parameters
    //////

    public static void main( String[] args ) throws IOException, URISyntaxException, Exception
    {
        try {
//            ChromeDriver browser = new ChromeDriver();

            int pageIndex = 0;
            boolean finished = false;
            while (!finished)
            {
                pageIndex ++;

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

    private static void scrapeDeal(String urlString) throws MalformedURLException, IOException, NullPointerException, Exception
    {
        URL url = new URL(urlString);
        d(url.getPath());
        Item item = new Item(url);


        Document doc = Jsoup.connect(urlString).get();

        Element threadItem = doc.getElementsByAttributeValueStarting("class", "threadItem").first();

        Element dealHeader = threadItem.getElementsByAttributeValueStarting("class", "threadItem-headerMeta").first();
        Element dealTitle = threadItem.getElementsByAttributeValueStarting("class", "threadItem-title").first();
        Element dealBody = threadItem.getElementsByAttributeValueStarting("class", "threadItem-body").first();
        Element dealFooter = threadItem.getElementsByAttributeValueStarting("class", "threadItem-footerMeta").first();

        // image
        Element threadImage = doc.getElementsByAttributeValueStarting("class", "threadItem-image").first();
        String imageURL = threadImage.getElementsByTag("img").first().attr("src");
//        d(imageURL);
        Attribute.create("imageURL", imageURL);

        // temperature & deal status
        Element temperatureBox = doc.getElementsByAttributeValueContaining("class", "vote-box").first();
//        boolean expired = temperatureBox.attr("class").contains("vote-box--muted");
        String temperatureRaw = temperatureBox.text().trim();
//        int temperatureValue = Integer.parseInt(temperatureRaw.split(" ")[0].split("°")[0]);
        String[] temperatureSplit = temperatureRaw.split(" ");
        String temperatureStatus = Attribute.STATUS_DEFAULT;
        if (temperatureSplit.length > 1) temperatureStatus = temperatureSplit[1];
//        d(expired, temperatureRaw);
        Attribute.create("temperature", temperatureRaw, temperatureStatus); // TODO maybe replace expired byt its indicated status on the page

        // location and shipping infos
        /*item.addAttribute(extractInfoFromAssociatedIcon("shipping", doc, "world"));
        item.addAttribute(extractInfoFromAssociatedIcon("location", doc, "location"));
        // publication and expiration dates
        item.addAttribute(extractInfoFromAssociatedIcon("datePublished", doc, "clock"));
        item.addAttribute(extractInfoFromAssociatedIcon("dateExpiration", doc, "hourglass"));*/

    }


    /*private static Attribute extractInfoFromAssociatedIcon(String attributeName, Document doc, String iconIdentifier)
    {
        Attribute attribute;
        try {
            Element icon = doc.getElementsByAttributeValueContaining("class", "icon--" + iconIdentifier).first();
//            d(iconWorld);
            String shippingText = icon.parent().parent().text();
//            d(shippingText);
            attribute = new Attribute(attributeName, true, shippingText);

        } catch (NullPointerException nullPointerException) {
            attribute = new Attribute(attributeName, false, null);
        }
        if (debugExtractInfo) d(attribute);
        return attribute;
    }*/
}
