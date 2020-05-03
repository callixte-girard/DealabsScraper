package callixtegirard.model;




import static callixtegirard.util.Debug.d;

public abstract class Attribute //implements MyInterface
{
    public static final boolean debug = true;

    // maybe replace String status with one of this enum directly with the tow create() methods... if needed.
    /*public enum Status {
        AVAILABLE,
    }*/
    public static final String STATUS_EMPTY = "(unavailable)";

    protected String name;
    protected String value;
    protected String status;

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
        out += "° " + this.getClass().getSimpleName() + "{" + "name=" + name;
        out += ", " +"value=" + value;
        if (!this.status.equals(STATUS_EMPTY)) out += ", " +"status=" + status;
        out += '}';
        return out;
    }

}
