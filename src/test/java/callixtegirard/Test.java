package callixtegirard;


import okhttp3.HttpUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static callixtegirard.util.Debug.d;


public class Test
{
    public static void main(String[] args) throws Exception
    {
//        Properties devProps = readPropertiesFile("devConfig.properties");
//        Properties prodProps = readPropertiesFile("prodConfig.properties");

        HttpUrl urlTest = new HttpUrl.Builder()
                .scheme("https")
                .host("dealabs.com")
                .addPathSegment("hot")
                .addQueryParameter("page", "1")
                .build();
        d(urlTest);
        testServerLimit(urlTest.toString(), "Smartphone 6.55\" OnePlus 8 - 8 Go RAM, 128 Go ROM - Onyx Black ou Glacial Green (+29.25€ en SuperPoints)");

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
