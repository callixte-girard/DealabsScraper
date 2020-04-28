package callixtegirard.model;

import callixtegirard.reference.model.Attribute;
import okhttp3.HttpUrl;

import java.io.Serializable;


public class Deal implements Serializable
{
    private HttpUrl url; // full ? à voir.
    private Attribute productName;
    private Attribute temperature;
    //////


    public Deal(HttpUrl url) {
        this.url = url;
    }

    public HttpUrl getUrl() {
        return url;
    }

    public Attribute getProductName() {
        return productName;
    }

    public void setProductName(Attribute productName) {
        this.productName = productName;
    }

    public Attribute getTemperature() {
        return temperature;
    }

    public void setTemperature(Attribute temperature) {
        this.temperature = temperature;
    }
}
