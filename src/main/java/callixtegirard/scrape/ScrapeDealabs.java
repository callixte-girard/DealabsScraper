package callixtegirard.scrape;

import static callixtegirard.util.Debug.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;


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
                String urlSection = urlRoot + urlsSections[i];
                Document doc = Jsoup.connect(urlSection).get();
                d(doc);
            }

        } catch (IOException exc) {
            throw exc;
        }
    }
}
