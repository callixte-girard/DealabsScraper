package callixtegirard.scrape;

import static callixtegirard.util.Debug.*;

import callixtegirard.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
                    d(deal);
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

        // image [AttributeRequired]
        Element containerImage = threadImage.getElementsByTag("img").first();
        String imageURL = containerImage.attr("src");
        item.addAttribute(ReqAttribute.create("imageURL", imageURL));

        // thread section 2 (is it really useful ?)
        Element threadHeader = threadItem.getElementsByAttributeValueStarting
                ("class", "threadItem-headerMeta").first(); // yeah me should be
        /*Element threadBody = threadItem.getElementsByAttributeValueStarting
                ("class", "threadItem-body").first();*/

        // thread section 3 : title
        Element threadTitle = threadItem.
                getElementsByAttributeValueStarting("class", "threadItem-title").first();

        // title [AttributeRequired]
        String title = threadTitle.getElementsByClass("thread-title").first().text().trim();
        item.addAttribute(ReqAttribute.create("titre", title));

        // price proposed [AttributeOptional]
        Element subTitle = threadTitle.getElementsByClass("overflow--fade").first();
        String priceReduced = subTitle.getElementsByClass("overflow--wrap-off").get(0).text().trim();
        item.addAttribute(Attribute.create("prixRabais", priceReduced));

        // price reduction [AttributeOptional]
        Element priceReductionContainer = subTitle.getElementsByClass
                ("flex--inline boxAlign-ai--all-c space--ml-2").first();
        String priceOriginal = Attribute.STATUS_EMPTY;
        String pricePercentage = Attribute.STATUS_EMPTY;
        if (priceReductionContainer != null) {
            priceOriginal = priceReductionContainer.child(0).text().trim();
            if (priceReductionContainer.childrenSize() > 1)
                pricePercentage = priceReductionContainer.child(1).text().trim();
        }
        item.addAttribute(Attribute.create("prixOriginal", priceOriginal));
        item.addAttribute(Attribute.create("pourcentageRabais", pricePercentage));

        // shipping price [AttributeOptional]
        Element containerShippingPrice = threadTitle.getElementsByAttributeValueContaining
                ("class", "icon--truck").first();
        String shippingPrice = Attribute.STATUS_EMPTY;
        if (containerShippingPrice != null) {
            containerShippingPrice = containerShippingPrice.parent(); // warning ! takes only one .parent() here
            shippingPrice = containerShippingPrice.text().trim();
        }
        if (shippingPrice.length() > 1) shippingPrice = shippingPrice.split(" ")[1]; // remove Gratuit in double
        item.addAttribute(Attribute.create("prixLivraison", shippingPrice));

        // merchant name [AttributeOptional]
        Element merchantNameContainer = threadTitle.getElementsByAttributeValueContaining
                ("class", "cept-merchant-name").first();
        String merchantName = Attribute.STATUS_EMPTY;
        if (merchantNameContainer != null) merchantName = merchantNameContainer.text().trim();
        item.addAttribute(Attribute.create("vendeur", merchantName));

        // thread section 4 : footer
        Element threadFooter = threadItem.getElementsByAttributeValueStarting
                ("class", "threadItem-footerMeta").first();

        // posted by (name) [AttributeRequired]
        // NEW : V2.0 (long)
        ExtractAttrContainer extractAttrContainer = (Element el) -> el.getElementsByAttributeValueContaining
                ("class", "thread-username").first();
        ExtractAttrValue extractAttrValue = (Element el) -> el.text().trim();
        item.addAttribute(ReqAttribute.create("pseudoPublicateur",
                extractAttrValue.extractAttrValue(extractAttrContainer.extractAttrContainer(threadFooter))));
        // NEW : V2.1 (one-line optimised)
        item.addAttribute(ReqAttribute.create("pseudoPublicateur",
                threadFooter,
                upperLevelContainer -> upperLevelContainer.getElementsByAttributeValueContaining
                        ("class", "thread-username").first(),
                attributeContainer -> attributeContainer.text().trim()
        ));

        // OLD : V1.0
        Element containerPosterUsername = threadFooter.getElementsByAttributeValueContaining
                ("class", "thread-username").first();
        String posterUsername = containerPosterUsername.text().trim();
        item.addAttribute(ReqAttribute.create("pseudoPublicateur", posterUsername));

        // posted by (title) [AttributeOptional]
        Element containerPosterTitle = threadFooter.getElementsByAttributeValueContaining
                ("class", "cept-user-title").first();
        String posterTitle = containerPosterTitle.text().trim();
        item.addAttribute(OptAttribute.create("titrePublicateur", posterTitle));

        // border 1 : beginning
        Element borderBeginning = doc.getElementsByAttributeValueContaining
                ("class", "border--color-borderGrey").get(0);

        // sub container : temperature & status
        Element containerTemperatureAndStatus = borderBeginning.getElementsByAttributeValueContaining
                ("class", "vote-box").first();

        // temperature [AttributeRequired]
        String temperature = containerTemperatureAndStatus.text().trim().split(" ")[0];
        item.addAttribute(ReqAttribute.create("température", temperature));

        // status [OptionalAttribute]
        String[] temperatureAndStatus = containerTemperatureAndStatus.text().trim().split(" ");
        String status = Attribute.STATUS_EMPTY;
        if (temperatureAndStatus.length > 1) status = temperatureAndStatus[1];
        item.addAttribute(OptAttribute.create("statut", status));

        // border 2 : middle
        Element borderMiddle = doc.getElementsByAttributeValueContaining
                ("class", "border--color-borderGrey").get(1);

        // shippingFrom [OptionalAttribute]
        Element containerShippingFrom = borderMiddle.getElementsByAttributeValueContaining
                ("class", "icon--world").first();
        String shippingFrom = containerShippingFrom.parent().parent().text().trim();
        item.addAttribute(OptAttribute.create("livraisonDepuis", shippingFrom));

        // location [OptionalAttribute]
        Element containerLocation = borderMiddle.getElementsByAttributeValueContaining
                ("class", "icon--location").first();
        String location = containerLocation.parent().parent().text().trim();
        item.addAttribute(OptAttribute.create("localisation", location));

        // posted on [RequiredAttribute]
        Element containerPostedOn = borderMiddle.getElementsByAttributeValueContaining
                ("class", "icon--clock text--color-greyShade").first();
        String postedOn = containerPostedOn.parent().parent().text().trim();
        item.addAttribute(ReqAttribute.create("postéLe", postedOn));

        // date start [OptionalAttribute]
        Element containerDateStart = borderMiddle.getElementsByAttributeValueContaining
                ("class", "icon--clock text--color-green").first();
        String dateStart = containerDateStart.parent().parent().text().trim();
        item.addAttribute(Attribute.create("dateDébut", dateStart));

        // date expiration [OptionalAttribute]
        Element containerDateExpiration = borderMiddle.getElementsByAttributeValueContaining
                ("class", "icon--hourglass").first();
        String dateExpiration = containerDateExpiration.parent().parent().text().trim();
        item.addAttribute(Attribute.create("dateFin", dateExpiration));

        // date last modification [OptionalAttribute]
        Element containerModifiedOn = borderMiddle.getElementsByAttributeValueContaining
                ("class", "icon--pencil").first();
        String modifiedOn = Attribute.STATUS_EMPTY;
        if (containerModifiedOn != null)
            modifiedOn = containerModifiedOn.parent().parent().text().trim().replace("\"", "");
        item.addAttribute(Attribute.create("modification", modifiedOn));

        // description [RequiredAttribute]
        Element containerDescription = doc.getElementsByAttributeValueContaining
                ("class", "userHtml-content").first();
        String descriptionWithHtml = containerDescription.child(0).children().outerHtml();
        item.addAttribute(ReqAttribute.create("description", descriptionWithHtml));

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

        return item;
    }


    private static String extractAttributeValueFromContainer(Element attributeContainer)
    {
        // takes an Element and outputs a String and avoids NPExcs (because there is a possibility they don't exist)
        String attributeValue = Attribute.STATUS_EMPTY;
        if (attributeContainer != null) {
            //////////
        }
        return attributeValue;
    }

}
