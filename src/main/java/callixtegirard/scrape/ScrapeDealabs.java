package callixtegirard.scrape;

import static callixtegirard.util.Debug.*;

import okhttp3.HttpUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

            for (int i=0 ; i<urlsSections.length ; i++)
            {
//                URL url = HttpUrl.parse("http://example.com:4567/foldername/1234?abc=xyz").url(); // to parse
                HttpUrl url = new HttpUrl.Builder()
                        .scheme("https")
                        .host("dealabs.com")
                        .addPathSegment("hot")
                        .addQueryParameter("page", "1")
                        .build();
                d(url);


                String urlSection = urlRoot + urlsSections[i];
                Document doc = Jsoup.connect(urlSection).get();
                d(doc);
            }

        } catch (IOException exc) {
            throw exc;
        }
    }
}
