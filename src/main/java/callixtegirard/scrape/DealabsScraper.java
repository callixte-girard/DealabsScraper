package callixtegirard.scrape;

import callixtegirard.gui.FrameProgressDoubleStop;
import callixtegirard.model.*;
import callixtegirard.util.ReadWriteFile;
import callixtegirard.util.WriteCSV;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static callixtegirard.util.Debug.*;
import static callixtegirard.util.Debug.d;

public class DealabsScraper extends WebsiteScraper
{
    private final RequestHandler reqHandler = new RequestHandler(true,false,null);

    // static parameters
    private static final String projectName = "DealabsScraper";
    private static final String valuesSeparator = ";";

    // writing and reading
    private final String filenameOutputCSV = "dealabs_" + dateNow + ".csv";
    private final String folderOutput = "exports/";
    private final String folderOutputImages = folderOutput + "images_" + dateNow + "/";
    private final String pathOutputCSV = folderOutput + filenameOutputCSV;

    // attribute names
    private final String attrName_stopScraping = "dateFlamme";
    private final String attrName_imageURL = "imageURL";
    private final String attrName_title = "titre";

    // ui specific to THIS scraper
    private FrameProgressDoubleStop frameProgress = new FrameProgressDoubleStop
            (projectName, e -> this.stopAtNextTurn(true));

//    public DealabsScraper(FrameProgressDoubleStop frameProgress) {
//        this.frameProgress = frameProgress;
//    }


    protected void scrapeEverything() throws Exception//, IOException, URISyntaxException,
    {
        frameProgress.getBar().setMaximum(60); // minutes
        frameProgress.getBar2().setMaximum(24); // heures
        frameProgress.update("Initialisation...", 0);

        try {
            int pageIndex = 0;
            int itemIndex = 0;
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
                for (Element dealItem : dealItems)
                {
                    itemIndex ++;
                    // get deal detail url
                    Element link = dealItem.getElementsByAttributeValueContaining("class", "linkPlain").first();
                    String dealUrl = link.attr("href");
//                    d(dealUrl);

                    // scrape infos in Detail View
                    Item deal = scrapeItem(dealUrl);

                    // scrape infos in Preview View
                    // date flamme (?) // stays Opt because might return null if hidden (very rare but happens...)
                    deal.addAttribute(AttrOpt.create(attrName_stopScraping,
                            dealItem,
                            elt -> elt.text().trim(),
                            elt -> elt.getElementsByAttributeValueContaining("class",
                                    "size--all-s flex boxAlign-jc--all-fe flex--grow-1 overflow--hidden").first(),
                            elt -> {
                                Element el1, el2;
                                el1 = elt.getElementsByAttributeValueContaining("class", "icon--flame").first();
                                el2 = elt.getElementsByAttributeValueContaining("class", "icon--refresh").first();
                                if (el1 != null) return el1;
                                else if (el2 != null) return el2;
                                else return null;
                            },
//                            elt -> elt.getElementsByAttributeValueContaining("xlink:href", "ico_0f836.svg#flame").first(),
//                            elt -> elt.getElementsByAttributeValueContaining("class", "hide--toW3").first(),
                            elt -> elt.nextElementSibling().nextElementSibling()
                    ));

                    // short description
                    deal.addAttribute(AttrReq.create("descriptionCourte",
                            dealItem,
                            elt -> elt.text().trim().replaceAll(" Afficher plus", ""),
                            elt -> elt.getElementsByAttributeValueContaining("class", "cept-description-container").first()
                    ));

                    scrapedItems.add(deal);
//                    d(deal);
                    d(l);

                    // save image
                    String imageFilename = /*deal.getAttributeByName(attrName_title).getValue() + */
                            "promo_" + dateNow + "_" + itemIndex;
                    try {
                        String pathOutputImage = folderOutputImages + imageFilename + ".jpg";
                        ReadWriteFile.createFolderIfNotExists(folderOutputImages);
                        ReadWriteFile.writeFileFromURL(
                                deal.getAttributeByName(attrName_imageURL).getValue(),
                                pathOutputImage
                        );

                    } catch (IOException ioException) {
                        frameProgress.update("Erreur lors de l'écriture de l'image dans [ " + imageFilename + " ].");
                        throw ioException;
                    }

                    // update ui accordingly
                    try {
                        frameProgress.update("Récupération de la page n°" + pageIndex
                                        + " — deal n°" + itemIndex
                                        + " — " + deal.getAttributeByName(attrName_stopScraping).getValue()
                                , extractMinutes(deal.getAttributeByName(attrName_stopScraping).getValue())
                                , extractHours(deal.getAttributeByName(attrName_stopScraping).getValue())
                        );
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                        frameProgress.update("Récupération de la page n°" + pageIndex
                                + " — deal n°" + itemIndex
                                + " — " + deal.getAttributeByName(attrName_stopScraping).getValue()
                        );
                    }

                    // is it the time to stop ?
                    if ( (! deal.getAttributeByName(attrName_stopScraping).getValue().contains("il y a ")
                            && ! deal.getAttributeByName(attrName_stopScraping).getValue().contains(Attribute.STATUS_EMPTY) )
                            || (shouldWeStopAtNextTurn)
                    ) { // more than 24h
                        finished = true;
                        // update the ui
                        frameProgress.update("Exportation du CSV dans [ " + pathOutputCSV + " ] en cours...",
                                60, 24);
                        break; // leave the for loop
                    }
                }
                d("page n°", pageIndex, "contains", dealItems.size(), "articles");
                d(s);
            }
        } catch (IllegalArgumentException | UnknownHostException | HttpStatusException exc) {
            frameProgress.update("Erreur de connexion...");

        } catch (Exception exc) {
            throw exc;
        } finally {
            reqHandler.closeBrowser();

            try {
                // write results as csv
                writeExportCSV(scrapedItems);
                frameProgress.update("Exportation du CSV dans [ " + pathOutputCSV + " ] terminée!",
                        60, 24);

            } catch (IOException ioException) {
                frameProgress.update("Erreur lors de l'exportation du CSV dans [ " + pathOutputCSV + " ].",
                        60, 24);
            }

        }
    }

    protected Item scrapeItem(String urlString) throws Exception
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
        item.addAttribute(AttrReq.create(attrName_imageURL,
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
        item.addAttribute(AttrReq.create(attrName_title,
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

        // posted on [AttrOpt]
        item.addAttribute(AttrOpt.create("datePublication",
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

        // date last update [AttrOpt]
        /*item.addAttribute(AttrOpt.create("miseAJour",
                borderBottom,
                elt -> elt.text().trim()*//*.replace("\"", "")*//*,
                elt -> elt.getElementsByAttributeValueContaining("class", "icon--refresh").first(),
                elt -> elt.parent().parent()
        ));*/

        // description [AttrReq]
        /*item.addAttribute(AttrReq.create("description",
                doc,
                elt -> elt.outerHtml(),
                elt -> elt.getElementsByAttributeValueContaining("class", "cept-description-container").first()
        ));*/

        // thread section 5 : deal button
        Element threadDealButton = threadItem.getElementsByAttributeValueStarting
                ("class", "threadItem-dealBtn").first();

        // deal real url
        item.addAttribute(AttrOpt.create("dealRealURL",
                threadDealButton,
                elt -> {
                    reqHandler.getBrowser().get(elt.attr("href"));
                    return reqHandler.getBrowser().getCurrentUrl();
                },
                elt -> elt.getElementsByTag("a").first()
        ));

        // bon ben pas besoin en fait hein !
        // deal voucher code + url : first click if needed
        /*WebElement buttonVoucherToClick;
        try {
            buttonVoucherToClick = reqHandler.getBrowser().findElementByClassName("cept-vch");
            buttonVoucherToClick.click(); // not always needed
            // close the newly created tab
            String originalHandle = reqHandler.getBrowser().getWindowHandle();
            for (String handle : reqHandler.getBrowser().getWindowHandles()) {
                if (! handle.equals(originalHandle)) {
                    reqHandler.getBrowser().switchTo().window(originalHandle);
//                    reqHandler.getBrowser().close();
                }
            }
        } catch (Exception exc) {}*/

        // voucher code
        /*item.addAttribute(AttrOpt.create("voucherCode",
                Jsoup.parse(reqHandler.getBrowser().getPageSource()),
                elt -> elt.attr("value"),
                elt -> elt.getElementsByAttributeValueContaining("class", "js-voucherCode").first(),
                elt -> elt.getElementsByTag("input").first()
        ));*/

        // voucher real link
        /*item.addAttribute(AttrOpt.create("voucherRealURL",
                threadItem,
                elt -> {
                    reqHandler.getBrowser().get(elt.attr("href"));
                    return reqHandler.getBrowser().getCurrentUrl();
                },
                elt -> elt.getElementsByAttributeValueStarting("class", "cept-dealBtn").first()
//                elt -> elt.getElementsByClass("cept-vcb").first()
        ));*/

        // now close it
//        if (reqHandler.getBrowser().getWindowHandles().size() > 1)
//            reqHandler.getBrowser().close();

        return item;
    }


    // sub methods
    private ArrayList<Integer> extractNumbersFromString(String str)
    {
        ArrayList<Integer> extractedNumbers = new ArrayList<>();
        String[] spl = str.split(" ");
        for (String part : spl) {
            try {
                int parsed = Integer.parseInt(part);
                extractedNumbers.add(parsed);
            } catch (NumberFormatException numberFormatException) {}
        }
        return extractedNumbers;
    }

    private int extractMinutes(String str) {
        ArrayList<Integer> extractedNumbers = extractNumbersFromString(str);
        if (extractedNumbers.size() > 1)
            return extractedNumbers.get(1);
        else
            return extractedNumbers.get(0);
    }

    private int extractHours(String str) {
        ArrayList<Integer> extractedNumbers = extractNumbersFromString(str);
        if (extractedNumbers.size() > 1)
            return extractedNumbers.get(0);
        else
            return 0;
    }


    // when button clicked
    @Override
    public void stopAtNextTurn(boolean stop) {
        shouldWeStopAtNextTurn = stop;
    }


    // export
    protected void writeExportCSV(List<Item> deals) throws IOException
    { // TODO to make a higher-level method for writing CSV from custom Object[]
        BufferedWriter bw = ReadWriteFile.outputWriter(pathOutputCSV);
        // headers
        List<String> headersToWrite = new ArrayList<>();
        for (Attribute attr : deals.get(0).getAttributes()) {
            headersToWrite.add(attr.getName());
        }
        WriteCSV.writeLine(bw, headersToWrite, valuesSeparator, true);
        // attributes
        for (Item deal : deals) {
            List<String> dataToWrite = new ArrayList<>();
            for (Attribute attr : deal.getAttributes()) {
                dataToWrite.add(attr.getValue());
            }
            WriteCSV.writeLine(bw, dataToWrite, valuesSeparator, true);
        }
        // don't forget to close this little motherfucker !
        bw.close();
    }

}
