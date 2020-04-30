package callixtegirard.reference.model;




import static callixtegirard.util.Debug.d;

public class Attribute
{
    public static final boolean debug = true;

    // TODO maybe replace String status with one of this enum directly with the tow create() methods.
    /*public enum Status {
        AVAILABLE,
    }*/
    public static final String STATUS_DEFAULT = "default";
    public static final String STATUS_PERMANENT = null;

    protected String name;
    protected String value;
    protected String status;

    public Attribute(String name, String value, String status) {
        this.name = name;
        this.value = value;
        this.status = status;
    }

    /*public Attribute(AttributeStatus status, Object value) {
        this.status = status;
        this.value = value;
    }*/

    /*public Attribute(boolean exists, Object value) {
        if (exists) this.status = AttributeStatus.PRESENT;
        else this.status = AttributeStatus.ABSENT;
        this.value = value;
    }*/


    @Override
    public String toString() {
        return "Attribute{" +
                "name=" + name + ", " +
                "value=" + value + ", " +
                "status=" + status +
                '}';
    }


    public static Attribute create(String attrName, Object attrValue)
    {
        return create(attrName, attrValue, Attribute.STATUS_PERMANENT);
    }


    public static Attribute create(String attrName, Object attrValue, Object attrState)
    {
        Attribute attr = new Attribute(attrName, String.valueOf(attrValue), String.valueOf(attrState));
        if (debug) d(attr);
        return attr;
    }
}
