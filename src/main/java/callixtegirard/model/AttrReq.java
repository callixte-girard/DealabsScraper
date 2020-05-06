package callixtegirard.model;

import org.jsoup.nodes.Element;

import static callixtegirard.util.Debug.d;


public class AttrReq extends Attribute
{

    private AttrReq(String name, String value, String status) {
        this.name = name.replaceAll(";", ",");
        this.value = value.replaceAll(";", ",");
        this.status = status.replaceAll(";", ",");
    }


    public static AttrReq create(String attrName,
                                 Element upperLevelElement,
                                 ExtractAttrValue extractValue,
                                 ExtractAttrContainer... extractContainers
    )
    {
        for (ExtractAttrContainer extractContainer : extractContainers) {
            upperLevelElement = extractContainer.perform(upperLevelElement);
        }
        String attrValue = extractValue.perform(upperLevelElement);
        return create(attrName, attrValue);
    }


    public static AttrReq create(String attrName, Object attrValue)
    {
        return create(attrName, attrValue, Attribute.STATUS_EMPTY);
    }

    public static AttrReq create(String attrName, Object attrValue, Object attrState)
    {
        AttrReq attr = new AttrReq(attrName, String.valueOf(attrValue), String.valueOf(attrState));
        if (debug) d(attr);
        return attr;
    }
}
