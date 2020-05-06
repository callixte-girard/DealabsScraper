package callixtegirard;

import org.jsoup.nodes.Document;

public interface ScraperInterface {
    // 1) extractContainer
    // 2) extractAttrValue
    // 3) extractAttrStatus

    // 1 constructor with all 3
    // 1 constructor with Container + Value
    // 1 constructor with Value only

    String extractFromDoc(Document doc);
}
