package callixtegirard.model;

import org.jsoup.nodes.Element;

import static callixtegirard.util.Debug.d;

public class ReqAttribute extends Attribute //implements ExtractAttrContainer, ExtractAttrValue
{

    private ReqAttribute(String name, String value, String status) {
        this.name = name;
        this.value = value;
        this.status = status;
    }


    public static ReqAttribute create(String attrName, Element upperLevelElement,
                                      ExtractAttrContainer extractContainer, ExtractAttrValue extractValue)
    {
        Element attrContainer = extractContainer.extractAttrContainer(upperLevelElement);
        String attrValue = extractValue.extractAttrValue(attrContainer);
        return create(attrName, attrValue);
    }


    public static ReqAttribute create(String attrName, Object attrValue)
    {
        return create(attrName, attrValue, Attribute.STATUS_EMPTY);
    }

    private static ReqAttribute create(String attrName, Object attrValue, Object attrState)
    {
        ReqAttribute attr = new ReqAttribute(attrName, String.valueOf(attrValue), String.valueOf(attrState));
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
