package callixtegirard.reference.model;




import static callixtegirard.util.Debug.d;

public class Attribute //implements MyInterface
{
    public static final boolean debug = true;

    // TODO maybe replace String status with one of this enum directly with the tow create() methods.
    /*public enum Status {
        AVAILABLE,
    }*/
    public static final String STATUS_EMPTY = "(unavailable)";

    protected String name;
    protected String value;
    protected String status;

    private Attribute(String name, String value, String status) {
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
        String out = "";
        out += "° Attribute{" + "name=" + name;
        out += ", " +"value=" + value;
        if (!this.status.equals(STATUS_EMPTY)) out += ", " +"status=" + status;
        out += '}';
        return out;
    }


//    public static Attribute extract


    public static Attribute create(String attrName, Object attrValue)
    {
        return create(attrName, attrValue, Attribute.STATUS_EMPTY);
    }


    private static Attribute create(String attrName, Object attrValue, Object attrState)
    {
        Attribute attr = new Attribute(attrName, String.valueOf(attrValue), String.valueOf(attrState));
        if (debug) d(attr);
        return attr;
    }

    /*@Override
    public String extractFromDoc(Document doc) {

    }*/
}
