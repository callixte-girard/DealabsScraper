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
    /*
    TODO :
    - refaire la partie Voir le deal / Voir le code promo à partir de threadItem
    V rajouter "posté le" (différent de "commence le")
    - remove hyphens in last edition
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

                Elements deals = doc.getElementsByTag("article");
                for (Element deal : deals)
                {
                    // get deal detail url
                    Element link = deal.getElementsByAttributeValueContaining("class", "linkPlain").first();
                    String dealUrl = link.attr("href");
//                    d(dealUrl);

                    // scrape infos in Preview View
                    /////// (not necesary here)

                    // scrape infos in Detail View
                    scrapeDeal(dealUrl);
                    d(l);

//                    finished = true; // to test TODO remove me and replace be my the good indicator : dates
                }
                d("page n°", pageIndex, "contains", deals.size(), "articles");
                d(s);
            }

        } catch (IOException exc) {
            throw exc;
        }
    }

    private static Item scrapeDeal(String urlString) throws MalformedURLException, IOException, NullPointerException, Exception
    {
        URL url = new URL(urlString);
        d(url);
//        d(url.getPath());
        Item deal = new Item(url);

        Document doc = Jsoup.connect(urlString).get();
        Element threadItem = doc.getElementsByAttributeValueStarting("class", "threadItem").first();

        Element threadHeader = threadItem.getElementsByAttributeValueStarting("class", "threadItem-headerMeta").first();
        Element threadBody = threadItem.getElementsByAttributeValueStarting("class", "threadItem-body").first();

        Element threadTitle = threadItem.getElementsByAttributeValueStarting("class", "threadItem-title").first();
        // title
        String title = threadTitle.getElementsByClass("thread-title").first().text().trim();
        deal.addAttribute(Attribute.create("titre", title));

        // prix original / avec rabais / pourcentage de rabais
        Element subTitle = threadTitle.getElementsByClass("overflow--fade").first();
        String priceReduced = subTitle.getElementsByClass("overflow--wrap-off").get(0).text().trim();
        Element priceReductionContainer = subTitle.getElementsByClass("flex--inline boxAlign-ai--all-c space--ml-2").first();
        String priceOriginal = Attribute.STATUS_EMPTY;
        String pricePercentage = Attribute.STATUS_EMPTY;
        if (priceReductionContainer != null) {
            priceOriginal = priceReductionContainer.child(0).text().trim();
            if (priceReductionContainer.childrenSize() > 1)
                pricePercentage = priceReductionContainer.child(1).text().trim();
        }
        deal.addAttribute(Attribute.create("prixRabais", priceReduced));
        deal.addAttribute(Attribute.create("prixOriginal", priceOriginal));
        deal.addAttribute(Attribute.create("pourcentageRabais", pricePercentage));

        // prix livraison
        String shippingPrice = extractInfoFromAssociatedIcon(threadTitle, "icon--truck", false);
        if (shippingPrice.length() > 1) shippingPrice = shippingPrice.split(" ")[1]; // remove Gratuit in double
        deal.addAttribute(Attribute.create("prixLivraison", shippingPrice));

        // vendeur
        Element merchantNameContainer = threadTitle.getElementsByAttributeValueContaining("class", "cept-merchant-name").first();
        String merchantName = Attribute.STATUS_EMPTY;
        if (merchantNameContainer != null) merchantName = merchantNameContainer.text().trim();
        deal.addAttribute(Attribute.create("vendeur", merchantName));

        // le mec qui a posté le deal
        Element threadFooter = threadItem.getElementsByAttributeValueStarting("class", "threadItem-footerMeta").first();
        String posterUsername = threadFooter.getElementsByAttributeValueContaining("class", "thread-username").text().trim();
        deal.addAttribute(Attribute.create("proposéPar", posterUsername));

        // image
        Element threadImage = doc.getElementsByAttributeValueStarting("class", "threadItem-image").first();
        String imageURL = threadImage.getElementsByTag("img").first().attr("src");
        deal.addAttribute(Attribute.create("imageURL", imageURL));

        Element borderTemp = doc.getElementsByAttributeValueContaining("class", "border--color-borderGrey").get(0);
        // temperature & deal status
        Element temperatureBox = borderTemp.getElementsByAttributeValueContaining("class", "vote-box").first();
//        boolean expired = temperatureBox.attr("class").contains("vote-box--muted");
        String temperatureRaw = temperatureBox.text().trim();
        String[] temperatureSplit = temperatureRaw.split(" ");
        String temperatureValue = temperatureSplit[0];
        String temperatureStatus;
        if (temperatureSplit.length > 1) temperatureStatus = temperatureSplit[1];
        else temperatureStatus = Attribute.STATUS_EMPTY;
        deal.addAttribute(Attribute.create("température", temperatureValue/*, temperatureStatus*/));
        deal.addAttribute(Attribute.create("statut", temperatureStatus));

        Element borderMiddle = doc.getElementsByAttributeValueContaining("class", "border--color-borderGrey").get(1);
        // shipping from
        String shippingFrom = extractInfoFromAssociatedIcon(borderMiddle, "icon--world", true);
        deal.addAttribute(Attribute.create("livraisonDepuis", shippingFrom));
        // location
        String location = extractInfoFromAssociatedIcon(borderMiddle, "icon--location", true);
        deal.addAttribute(Attribute.create("localisation", location));
        // dates (publication and expiration)
        String postedOn = extractInfoFromAssociatedIcon(borderMiddle, "icon--clock text--color-greyShade", true);
        deal.addAttribute(Attribute.create("postéLe", postedOn));
        String dateStart = extractInfoFromAssociatedIcon(borderMiddle, "icon--clock text--color-green", true);
        deal.addAttribute(Attribute.create("dateDébut", dateStart));
        String dateExpiration = extractInfoFromAssociatedIcon(borderMiddle, "icon--hourglass", true);
        deal.addAttribute(Attribute.create("dateFin", dateExpiration));
        // edition
        String modifiedOn = extractInfoFromAssociatedIcon(borderMiddle, "icon--pencil", true);
        deal.addAttribute(Attribute.create("modification", modifiedOn));
        // description
        String descriptionWithHtml = doc.getElementsByClass("userHtml userHtml-content").first().child(0).children().outerHtml();
        deal.addAttribute(Attribute.create("description", descriptionWithHtml));

        // TODO link to the deal (but it will just be the redirection link on dealabs...)
        // TODO maybe just use the interface mentioned in extractInfoFromAssociatedIcon()...
        // v1 : "Voir le deal"
        // v1a : en bas à droite
        // v1b : au milieu
        // les deux sont géres en prenant doc comme référence
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

        return deal;
    }


    private static String extractInfoFromAssociatedIcon(Element elt, String iconIdentifier, boolean parentTwice)
    {
        // TODO make an interface that takes a Document and outputs a String avoid NPExcs, just like this one.
        Element icon = elt.getElementsByAttributeValueContaining("class", iconIdentifier).first();
        String attributeValue = Attribute.STATUS_EMPTY;
        if (icon != null) {
            if (parentTwice) attributeValue = icon.parent().parent().text().trim();
            else attributeValue = icon.parent().text().trim();
        }
        return attributeValue;
    }

}
