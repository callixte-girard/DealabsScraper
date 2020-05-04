package callixtegirard.scrape;

import static callixtegirard.util.Debug.*;

import callixtegirard.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URL;


public class ScrapeDealabs
{
    /*
    TODO :
    - refaire la partie Voir le deal / Voir le code promo à partir de threadItem
    - ajouter gestion des codes promo avec Selenium (il faut cliquer qqepart sur le <span class="voucher-label lbox--v-4 boxAlign-jc--all-c size--all-m text--color-white"><span class="hide--fromW2">Voir le code promo</span><span class="hide--toW2 hide--fromW3">Voir le code promo</span><span class="hide--toW3">Voir le code promo</span></span>)
    - refaire la partie température / statut pour qu'ils soient séparés
    - implémenter l'arrêt une fois les dernières 24h dépassées
    V intégrer l'interfaçage des éléments dans la dernière méthode de la page, pour gérer proprement ce qui peut être nul et prévenir de ce qui ne doit pas l'être.
    V différencier les attributs toujours présents (throw une exception s'ils ne sont pas sur la page) et les attributs facultatifs (qui ne sont pas nécessairement présents sur la page)
    V rajouter "posté le" (différent de "commence le")
    V rajouter les frais de livraison (section subtitle et non middle). Transformer la dernière méthode pour la scinder en deux (et rajouter un argument simple ou double .parent())
    A maybe refaire enum pour les status (mais bon, autant le considérer comme un attribute hein)
    O remove quotes in last edition
     */

    private static RequestHandler reqHandler = new RequestHandler(true, null);

    // debug parameters
    private static final boolean debugExtractInfo = false;
    ///////

    public static void main( String[] args ) throws Exception//, IOException, URISyntaxException,
    {
        try {
            int pageIndex = 0;
            boolean finished = false;
            while (!finished)
            {
                pageIndex++;

                String scheme = "https";
                String authority = "dealabs.com";
                String path = "/hot";
                String query = "page=" + pageIndex;
                String fragment = null;
                URI uri = new URI(scheme, authority, path, query, fragment);
                URL url = uri.toURL();
//                d(url);

                // includes selenium or jsoup
                Document doc = reqHandler.getDocHTML(url.toString(), false); // don't need selenium here

                Elements dealItems = doc.getElementsByTag("article");
                for (Element dealItem : dealItems) {
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
        } catch (Exception exc) {
            throw exc;
        } finally {
            reqHandler.closeBrowser();
        }
    }

    private static Item scrapeDeal(String urlString) throws Exception
    {
        URL url = new URL(urlString);
        d(url);
//        d(url.getPath());
        Item item = new Item(url);

        Document doc = reqHandler.getDocHTML(url.toString(), true); // here we need selenium :)

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

        // price without reduction [AttrOpt]
        item.addAttribute(AttrOpt.create("prixOriginal",
                subTitle,
                elt -> elt.child(0).text().trim(),
                elt -> elt.getElementsByClass("flex--inline boxAlign-ai--all-c space--ml-2").first()
        ));

        // price percentage off [AttrOpt]
        item.addAttribute(AttrOpt.create("pourcentageRabais",
                subTitle,
                elt -> {
                    String val = Attribute.STATUS_EMPTY;
                    if (elt.children().size() > 1) val = elt.child(1).text().trim();
                    return val;
                },
                elt -> elt.getElementsByClass("flex--inline boxAlign-ai--all-c space--ml-2").first()
        ));

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

        // border 1 : beginning
        Element borderTemperatureAndStatus = doc.getElementsByAttributeValueContaining
                ("class", "border--color-borderGrey").first();

        // temperature [AttrReq]
        item.addAttribute(AttrReq.create("température",
                borderTemperatureAndStatus,
                elt -> elt.text().trim().split(" ")[0],
                elt -> elt.getElementsByAttributeValueContaining("class", "vote-box").first()
        ));

        // status [AttrOpt]
        item.addAttribute(AttrOpt.create("statut", 
                borderTemperatureAndStatus,
                elt -> {
                    String val = elt.text().trim();
                    if (val.split(" ").length > 1) val = val.split(" ")[1];
                    else val = Attribute.STATUS_EMPTY;
                    return val;
                },
                elt -> elt.getElementsByAttributeValueContaining("class", "vote-box").first()
        ));

        // border 2 : bottom border containing additional infos
        Element borderBottom = doc.getElementsByAttributeValueContaining
                ("class", "border border--color-borderGrey bRad--a space--v-1 space--h-2").first();

        // shippingFrom [AttrOpt]
        item.addAttribute(AttrOpt.create("livraisonDepuis",
                borderBottom,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--world").first(),
                elt -> elt.parent().parent()
        ));

        // location [AttrOpt]
        item.addAttribute(AttrOpt.create("localisation",
                borderBottom,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--location").first(),
                elt -> elt.parent().parent()
        ));

        // posted on [AttrReq]
        item.addAttribute(AttrReq.create("postéLe",
                borderBottom,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--clock text--color-greyShade").first(),
                elt -> elt.parent().parent()
        ));

        // date start [AttrOpt]
        item.addAttribute(AttrOpt.create("dateDébut",
                borderBottom,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--clock text--color-green").first(),
                elt -> elt.parent().parent()
        ));

        // date expiration [AttrOpt]
        item.addAttribute(AttrOpt.create("dateFin",
                borderBottom,
                elt -> elt.text().trim(),
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--hourglass").first(),
                elt -> elt.parent().parent()
        ));

        // date last modification [AttrOpt]
        item.addAttribute(AttrOpt.create("modification",
                borderBottom,
                elt -> elt.text().trim()/*.replace("\"", "")*/,
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--pencil").first(),
                elt -> elt.parent().parent()
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
        // v2 : voucher code
        // on gère les 2 en 1 en récupérant simplement l'élément par le texte

        // thread section 4 : footer
        Element threadDealButton = threadItem.getElementsByAttributeValueStarting
                ("class", "threadItem-dealBtn").first();

        // deal real url
        AttrOpt dealabsRedirectURL = AttrOpt.create("dealRedirectURL",
                threadDealButton,
                elt -> elt.attr("href"),
                elt -> elt.getElementsByTag("a").first()
        );
        reqHandler.getBrowser().get(dealabsRedirectURL.getValue());
        item.addAttribute(AttrOpt.create("dealRealURL", reqHandler.getBrowser().getCurrentUrl()));

        // deal voucher code + url
        reqHandler.getBrowser().findElementByClassName("cept-vch").click();

        // voucher code
        item.addAttribute(AttrOpt.create("voucherCode",
                Jsoup.parse(reqHandler.getBrowser().getPageSource()),
                elt -> elt.attr("value"),
                elt -> elt.getElementsByAttributeValueContaining("class", "js-voucherCode").first(),
                elt -> elt.getElementsByTag("input").first()
        ));
        // voucher link
        reqHandler.getBrowser().findElementByClassName("cept-vch").click();
        item.addAttribute(AttrOpt.create("voucherRealURL", reqHandler.getBrowser().getCurrentUrl()));

        return item;
    }


}
