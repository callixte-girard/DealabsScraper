package callixtegirard.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class RequestHandler
{
    private static ChromeDriver browser;
    private static Integer maxBodySize;

    private static final boolean debugWait = false;


    public RequestHandler(boolean webDriver, boolean headless, Integer pageSizeLimit)
    {
        if (browser != null) throw new AssertionError("!!! Driver already instanciated !!!");
        if (webDriver) {
            ChromeOptions options = new ChromeOptions();
            options.setHeadless(headless);
            browser = new ChromeDriver(options);
        }
        if (pageSizeLimit != null) maxBodySize = pageSizeLimit;
    }


    public Document getDocHTML(String url, boolean webDriver) throws IOException, InterruptedException
    {
        Document doc;
        if (!webDriver) {
            if (maxBodySize != null) doc = Jsoup.connect(url).maxBodySize(maxBodySize).get();
            else doc = Jsoup.connect(url).get();
        } else {
            browser.get(url);
//            if (debugWait) TimeUnit.SECONDS.sleep(2);
            String html = browser.getPageSource();
            doc = Jsoup.parse(html);
        }
        return doc;
    }


    public void closeBrowser()
    {
        if (browser != null) browser.quit();
        else throw new AssertionError("!!! WebDriver is empty ; could not be closed !!!");
    }


    public ChromeDriver getBrowser() {
        return browser;
    }
}
