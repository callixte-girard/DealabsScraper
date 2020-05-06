package callixtegirard.model;

import callixtegirard.util.Date;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class WebsiteScraper
{
    protected final List<Item> scrapedItems = new ArrayList<>();
    protected final String dateNow = Date.nowFormatted;

    protected boolean shouldWeStopAtNextTurn = false;

    protected void stopAtNextTurn(boolean stop) {
        this.shouldWeStopAtNextTurn = stop;
    }

    public abstract void scrapeEverything() throws Exception;
    protected abstract Item scrapeItem(String itemUrl) throws Exception;
    protected abstract void writeExportCSV(List<Item> dataToWrite) throws IOException;
}
