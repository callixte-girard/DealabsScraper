package callixtegirard.model;

import callixtegirard.reference.model.Attribute;
import okhttp3.HttpUrl;

import java.io.Serializable;
import java.net.URL;


public class Deal implements Serializable
{
    private final URL url; // full ? à voir.
    private Attribute name;
    private Attribute temperature;
    //////


    public Deal(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public Attribute getName() {
        return name;
    }

    public void setName(Attribute name) {
        this.name = name;
    }

    public Attribute getTemperature() {
        return temperature;
    }

    public void setTemperature(Attribute temperature) {
        this.temperature = temperature;
    }
}
