package callixtegirard.reference.model;

public class OptionalAttribute extends Attribute
{
    // Sometimes they're just irrelevant, so they're not present on the page

    /*public OptionalAttribute(Object value)
    {
        if (value != null)
            this.status = AttributeStatus.AVAILABLE;
        else
            this.status = AttributeStatus.INEXISTANT;
        this.value = value;
    }*/

    public OptionalAttribute(String name, boolean exists, Object value)
    {
        this.name = name;
        if (exists)
            this.status = AttributeStatus.AVAILABLE;
        else
            this.status = AttributeStatus.INEXISTANT;
        this.value = value;
    }
}
