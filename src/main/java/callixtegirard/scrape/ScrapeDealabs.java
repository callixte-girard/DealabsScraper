package callixtegirard.scrape;

import static callixtegirard.util.Debug.*;

import callixtegirard.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.attribute.HashPrintRequestAttributeSet;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class ScrapeDealabs
{
    /*
    TODO :
    - refaire la partie Voir le deal / Voir le code promo à partir de threadItem
    - refaire la partie température / statut pour qu'ils soient séparés
    - différencier les attributs toujours présents (throw une exception s'ils ne sont pas sur la page) et les attributs facultatifs (qui ne sont pas nécessairement présents sur la page)
    V rajouter "posté le" (différent de "commence le")
    - remove quotes in last edition
    - ajouter gestion des codes promo avec Selenium (il faut cliquer qqepart sur le <span class="voucher-label lbox--v-4 boxAlign-jc--all-c size--all-m text--color-white"><span class="hide--fromW2">Voir le code promo</span><span class="hide--toW2 hide--fromW3">Voir le code promo</span><span class="hide--toW3">Voir le code promo</span></span>)
    - intégrer l'interfaçage des éléments dans la dernière méthode de la page, pour gérer proprement ce qui peut être nul et prévenir de ce qui ne doit pas l'être.
    - maybe refaire enum pour les status (mais bon, autant le considérer comme un attribute hein)
    V rajouter les frais de livraison (section subtitle et non middle). Transformer la dernière méthode pour la scinder en deux (et rajouter un argument simple ou double .parent())
     */

    // debug parameters
    private static final boolean debugExtractInfo = false;
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

                Elements dealItems = doc.getElementsByTag("article");
                for (Element dealItem : dealItems)
                {
                    // get deal detail url
                    Element link = dealItem.getElementsByAttributeValueContaining("class", "linkPlain").first();
                    String dealUrl = link.attr("href");
//                    d(dealUrl);

                    // scrape infos in Preview View
                    /////// (not necesary here)

                    // scrape infos in Detail View
                    Item deal = scrapeDeal(dealUrl);
//                    d(deal);
                    d(l);

//                    finished = true; // to test TODO remove me and replace be my the good indicator : dates
                }
                d("page n°", pageIndex, "contains", dealItems.size(), "articles");
                d(s);
            }

        } catch (IOException exc) {
            throw exc;
        }
    }

    private static Item scrapeDeal(String urlString) throws Exception
    {
        URL url = new URL(urlString);
        d(url);
//        d(url.getPath());
        Item item = new Item(url);

        Document doc = Jsoup.connect(urlString).get();

        // main deal container
        Element threadItem = doc.getElementsByAttributeValueStarting("class", "threadItem").first();

        // thread section 1 : image
        Element threadImage = doc.getElementsByAttributeValueStarting
                ("class", "threadItem-image").first();

        // image [AttrReq]
        item.addAttribute(AttrReq.create("imageURL",
                threadImage,
                elt -> elt.attr("src"),
                elt -> elt.getElementsByTag("img").first()
        ));

        // thread section 2 (is it really useful ?)
        Element threadHeader = threadItem.getElementsByAttributeValueStarting
                ("class", "threadItem-headerMeta").first(); // yeah me should be
        /*Element threadBody = threadItem.getElementsByAttributeValueStarting
                ("class", "threadItem-body").first();*/

        // thread section 3 : title
        Element threadTitle = threadItem.getElementsByAttributeValueStarting
                ("class", "threadItem-title").first();

        // title [AttrReq]
        item.addAttribute(AttrReq.create("titre",
                threadTitle,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByClass("thread-title").first()
        ));

        // sub thread section 3a : sub title
        Element subTitle = threadTitle.getElementsByClass("overflow--fade").first();
        
        // price proposed (reduced) [AttrOpt]
        item.addAttribute(AttrOpt.create("prixRabais",
                subTitle,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByClass("overflow--wrap-off").first()
        ));

        // price reduction [AttrOpt] TODO
        /*Element priceReductionContainer = subTitle.getElementsByClass
                ("flex--inline boxAlign-ai--all-c space--ml-2").first();
        String priceOriginal = Attribute.STATUS_EMPTY;
        String pricePercentage = Attribute.STATUS_EMPTY;
        if (priceReductionContainer != null) {
            priceOriginal = priceReductionContainer.child(0).text().trim();
            if (priceReductionContainer.childrenSize() > 1)
                pricePercentage = priceReductionContainer.child(1).text().trim();
        }
        item.addAttribute(AttrOpt.create("prixOriginal", priceOriginal));
        item.addAttribute(AttrOpt.create("pourcentageRabais", pricePercentage));*/

        // shipping price [AttrOpt]
        item.addAttribute(AttrOpt.create("prixLivraison",
                threadTitle,
                // attrValue
//                elt -> elt.text().trim().replace("Gratuit ", ""), // simplified version : to test
                elt -> {
                    String val = elt.text().trim();
                    if (val.split(" ").length > 1) val = val.split(" ")[1]; // remove Gratuit in double
                    return val;
                },
                // attrContainer(s)
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--truck").first(),
                elt -> elt.parent()
        ));

        // merchant name [AttrOpt]
        item.addAttribute(AttrOpt.create("vendeur",
                threadTitle,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "cept-merchant-name").first()
        ));

        // ***** optimisation starts here *****
        // thread section 4 : footer
        Element threadFooter = threadItem.getElementsByAttributeValueStarting
                ("class", "threadItem-footerMeta").first();

        // poster name [AttrReq]
        item.addAttribute(AttrReq.create("pseudoPublicateur",
                threadFooter,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "thread-username").first()
        ));

        // poster title [AttrOpt]
        item.addAttribute(AttrOpt.create("titrePublicateur",
                threadFooter,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "cept-user-title").first()
        ));
        // ***** optimisation ends here *****

        // border 1 : beginning
        Element borderBeginning = doc.getElementsByAttributeValueContaining
                ("class", "border--color-borderGrey").first();

        // temperature [AttrReq]
        item.addAttribute(AttrReq.create("température",
                borderBeginning,
                elt -> elt.text().trim().split(" ")[0],
                elt -> elt.getElementsByAttributeValueContaining("class", "vote-box").first()
        ));

        // status [AttrOpt]
        item.addAttribute(AttrOpt.create("statut", 
                borderBeginning,
                elt -> {
                    String val = elt.text().trim();
                    if (val.split(" ").length > 1) val = val.split(" ")[1];
                    else val = Attribute.STATUS_EMPTY;
                    return val;
                },
                elt -> elt.getElementsByAttributeValueContaining("class", "vote-box").first()
        ));

        // border 2 : middle
        Element borderMiddle = doc.getElementsByAttributeValueContaining
                ("class", "border--color-borderGrey").get(1);

        // shippingFrom [AttrOpt]
        item.addAttribute(AttrOpt.create("livraisonDepuis",
                borderMiddle,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--world").first(),
                elt -> elt.parent(),
                elt -> elt.parent()
        ));

        // location [AttrOpt]
        item.addAttribute(AttrOpt.create("localisation",
                borderMiddle,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--location").first(),
                elt -> elt.parent(),
                elt -> elt.parent()
        ));

        // posted on [AttrReq]
        item.addAttribute(AttrReq.create("postéLe",
                borderMiddle,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--clock text--color-greyShade").first(),
                elt -> elt.parent(),
                elt -> elt.parent()
        ));

        // date start [AttrOpt]
        item.addAttribute(AttrOpt.create("dateDébut",
                borderMiddle,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--clock text--color-green").first(),
                elt -> elt.parent(),
                elt -> elt.parent()
        ));

        // date expiration [AttrOpt]
        item.addAttribute(AttrOpt.create("dateFin",
                borderMiddle,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--hourglass").first(),
                elt -> elt.parent(),
                elt -> elt.parent()
        ));

        // date last modification [AttrOpt]
        item.addAttribute(AttrOpt.create("modification",
                borderMiddle,
                elt -> elt.text().trim()/*.replace("\"", "")*/,
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--pencil").first(),
                elt -> elt.parent(),
                elt -> elt.parent()
        ));

        // description [AttrReq]
        item.addAttribute(AttrReq.create("description",
                doc,
                elt -> elt.outerHtml(),
                elt -> elt.getElementsByAttributeValueContaining("class", "cept-description-container").first()
        ));

        // TODO link to the deal (but it will just be the redirection link on dealabs...)
        // v1 : "Voir le deal"
        // v1a : en bas à droite
        // v1b : au milieu
        // les deux sont gérés en prenant doc comme référence
        /*String dealRedirectURL;
        Elements buttonDeal = doc.getElementsByAttributeValueStarting("class", "cept-dealBtn");
        if (buttonDeal != null) {
            dealRedirectURL = buttonDeal.first().attr("href");
        }
        // v2 : voucher code
        String voucherRedirectURL;
        Elements buttonVoucher = doc.getElementsByAttributeValueStarting("class", "cept-vcb");
        if (buttonVoucher != null) {
            voucherRedirectURL =
        }*/

        return item;
    }


}
