package callixtegirard.model;

import org.jsoup.nodes.Element;

import static callixtegirard.util.Debug.d;


public class AttrOpt extends Attribute
{

    private AttrOpt(String name, String value, String status) {
        this.name = name.replaceAll(";", ",");
        this.value = value.replaceAll(";", ",");
        this.status = status.replaceAll(";", ",");
    }


    public static AttrOpt create(String attrName,
                                 Element upperLevelElement,
                                 ExtractAttrValue extractValue,
                                 ExtractAttrContainer... extractContainers
    )
    {
        for (ExtractAttrContainer extractContainer : extractContainers) { // maybe we'll have to add a if (... != null)
            if (upperLevelElement != null) upperLevelElement = extractContainer.perform(upperLevelElement);
            else break;
        }
        String attrValue = Attribute.STATUS_EMPTY;
        if (upperLevelElement != null) attrValue = extractValue.perform(upperLevelElement);
        return create(attrName, attrValue);
    }


    public static AttrOpt create(String attrName, Object attrValue)
    {
        return create(attrName, attrValue, Attribute.STATUS_EMPTY);
    }

    public static AttrOpt create(String attrName, Object attrValue, Object attrState)
    {
        AttrOpt attr = new AttrOpt(attrName, String.valueOf(attrValue), String.valueOf(attrState));
        if (debug) d(attr);
        return attr;
    }
}
