package callixtegirard.model;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Item implements Serializable
{
    private final URL url;
    private List<Attribute> attributes = new ArrayList<>();

    public Item(URL url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Item{" +
                "url=" + url +
                ", attributes=" + attributes +
                '}';
    }

    public URL getUrl() {
        return url;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(Attribute attribute) throws Exception {
        if (this.attributes.contains(attribute))
            throw new Exception(attribute + " already present in list !");
        else this.attributes.add(attribute);
    }


}
