package callixtegirard.reference.model;


public class Attribute
{
    private final String name;
    private final AttributeStatus status;
    private final Object value;

    public Attribute(String name, AttributeStatus status, Object value) {
        this.name = name;
        this.status = status;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", value=" + value +
                '}';
    }
}
