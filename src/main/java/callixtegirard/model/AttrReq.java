package callixtegirard.model;

import org.jsoup.nodes.Element;

import static callixtegirard.util.Debug.d;

public class AttrReq extends Attribute //implements ExtractAttrContainer, ExtractAttrValue
{

    private AttrReq(String name, String value, String status) {
        this.name = name;
        this.value = value;
        this.status = status;
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


    private static AttrReq create(String attrName, Object attrValue)
    {
        return create(attrName, attrValue, Attribute.STATUS_EMPTY);
    }

    private static AttrReq create(String attrName, Object attrValue, Object attrState)
    {
        AttrReq attr = new AttrReq(attrName, String.valueOf(attrValue), String.valueOf(attrState));
        if (debug) d(attr);
        return attr;
    }

/*
    @Override
    public Container extractAttrContainer(Container upperLevelContainer) {
        return null;
    }

    @Override
    public String extractAttrValue(Container attributeContainer) {
        return null;
    }*/
}
