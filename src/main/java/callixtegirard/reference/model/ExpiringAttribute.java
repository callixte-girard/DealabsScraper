package callixtegirard.reference.model;

public class ExpiringAttribute extends Attribute
{
    // They always exist on the page but there can be some deactivated functionality

    public ExpiringAttribute(String name, boolean expired, Object value)
    {
        this.name = name;
        if (!expired)
            this.status = AttributeStatus.AVAILABLE;
        else
            this.status = AttributeStatus.UNAVAILABLE;
        this.value = value;
    }
}
