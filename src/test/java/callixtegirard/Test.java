package callixtegirard;


import callixtegirard.reference.model.Attribute;
import callixtegirard.reference.model.AttributeStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static callixtegirard.util.Debug.d;
import static callixtegirard.util.Debug.dL;


public class Test
{
    public static void main(String[] args) throws Exception
    {
        String testOutputPath = "/Users/c/Desktop/test.jpg";

//        String imageURL = "https://www.dealabs.com/visit/threadimage/1879504"; :/ fuck c'est pas une image en fait
        String imageURL = "https://upload.wikimedia.org/wikipedia/commons/d/df/Fox_study_6.jpg";

        try (InputStream in = new URL(imageURL).openStream()) {
            Files.copy(in, Paths.get(testOutputPath));
        }

//        Image image = null;
//        URL url = new URL(imageURL);
//        image = ImageIO.read(url);
//        FileOutputStream fos = new FileOutputStream(testOutputPath);
//        fos.write();
//        fos.close();




        /*URI uri = new URI("https://www.dealabs.com/hot?page=1");
        d(uri.getAuthority(), uri.getQuery(), uri.getPath(), uri.getPort());
        URL url = uri.toURL();
        d(url);*/

        /*Attribute[] attrs = {
                new Attribute("pipou", AttributeStatus.AVAILABLE, 35),
                new Attribute("pipou", AttributeStatus.INEXISTANT, null),
                new Attribute("pipou", AttributeStatus.UNAVAILABLE, 76),
        };
        dL(attrs);*/

//        Properties devProps = readPropertiesFile("devConfig.properties");
//        Properties prodProps = readPropertiesFile("prodConfig.properties");

        /*HttpUrl urlTest = new HttpUrl.Builder()
                .scheme("https")
                .host("dealabs.com")
                .addPathSegment("hot")
                .addQueryParameter("page", "1")
                .build();
        d(urlTest);
        testServerLimit(urlTest.toString(), "Smartphone 6.55\" OnePlus 8 - 8 Go RAM, 128 Go ROM - Onyx Black ou Glacial Green (+29.25€ en SuperPoints)");*/

        /*String testValueOfPrimitive = String.valueOf(3);
        String testValueOfExtended = String.valueOf(new ArrayList<>()); // yeah ! it works.
//        String testToStringPrimitive = 3.toString(); // doesn't exist !
        String testToStringExtended = new ArrayList<>().toString();
        disp(testValueOfPrimitive, testValueOfExtended);*/

        /*List<Author> authors = new ArrayList<>();
        authors.add(new Author("www.pipou.fr", "Pipou San", "Pipou est né le", "son père est gigolo", "sa mère est pute"));
        authors.add(new Author("www.pipou.fr", "Pipou San", "Pipou est né le", "son père est gigolo", "sa mère est pute"));
        authors.add(new Author("www.pipou.fr", "Pipou San", "Pipou est né le", "son père est gigolo", "sa mère est pute"));
        authors.add(new Author("www.pipou.fr", "Pipou San", "Pipou est né le", "son père est gigolo", "sa mère est pute"));
        ReadWriteFile.exportCSV(authors, "auteurs_test.csv", "|", true);*/

//        ReadWriteFile_Test.writeTest();
    }


    // TOOLS AND CUSTOM METHODS
    // 3) properties
    /*public static Properties readPropertiesFile(String propFileName) throws IOException
    {
        // cleaner version (but we don't want something as clean nigga)
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(propFileName);
            prop = new Properties();
            prop.load(fis);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } finally {
            fis.close();
        }
        return prop;
    }*/

    /*public static void readPropertiesFile(String propFileName) throws IOException
    {
        Properties prop = new Properties();
        InputStream inputStream = Test.class.getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);

        // get the property value and print it out
        boolean stayChill = Boolean.parseBoolean(prop.getProperty("stayChill"));
        String rootPath = prop.getProperty("rootPath");
        d(stayChill, rootPath);
    }*/


    // 2) read something written from Windows and vice-versa
    // TODO


    // 1) scraping tests
//    private static final String url_main = "https://fr.wikipedia.org" ;
//    private static final String url_sub = "/wiki" ;
//    private static final String page_test = "Arthur_Rimbaud";
//    private static final String url_test = url_main + url_sub + "/" + page_test;

    static void testServerLimit(String urlTest, String indicator)
    { // TODO check that VPN is disconnected (so as not to start saturating a region in case the is indeed a limit)
        boolean success = true;
        int i = 0;

        while (success) {
            i ++;
            // test after how many tries test page gets an error
            try {
                Document doc_test = Jsoup.connect(urlTest).get();
//                d(doc_test);
                Element test_elt = doc_test.body().getElementsContainingText(indicator).first();
                success = test_elt != null;
                d("attempt ", i, " : ", success);
//                l();
            } catch (Exception e) {
                d("!!!", e);
            }
        }
    }


}
