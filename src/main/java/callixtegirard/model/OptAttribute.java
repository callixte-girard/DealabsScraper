package callixtegirard.model;

import static callixtegirard.util.Debug.d;

public class OptAttribute extends Attribute {

    private OptAttribute(String name, String value, String status) {
        this.name = name;
        this.value = value;
        this.status = status;
    }

    public static OptAttribute create(String attrName, Object attrValue)
    {
        return create(attrName, attrValue, Attribute.STATUS_EMPTY);
    }


    private static OptAttribute create(String attrName, Object attrValue, Object attrState)
    {
        OptAttribute attr = new OptAttribute(attrName, String.valueOf(attrValue), String.valueOf(attrState));
        if (debug) d(attr);
        return attr;
    }
}
