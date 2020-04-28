package callixtegirard.reference.model;

public class OptionalAttribute extends Attribute
{
    // Sometimes they're just irrelevant, so they're not present on the page

    public OptionalAttribute(boolean exists, Object value)
    {
        if (exists)
            this.status = AttributeStatus.AVAILABLE;
        else
            this.status = AttributeStatus.INEXISTANT;
        this.value = value;
    }
}
