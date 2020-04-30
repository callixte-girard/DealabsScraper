package callixtegirard.reference.model;


public abstract class Attribute
{
    protected String name;
    protected AttributeStatus status;
    protected Object value;

    /*public Attribute(String name, AttributeStatus status, Object value) {
        this.name = name;
        this.status = status;
        this.value = value;
    }*/

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
//                "name='" + name + '\'' + " ," +
                "status=" + status + ", " +
                "value=" + value +
                '}';
    }
}
