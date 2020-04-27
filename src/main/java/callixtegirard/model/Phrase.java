package callixtegirard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Phrase implements Serializable
{
    private String marker;
    private String phrase;

    public Phrase(String marker, String phrase) {
        this.marker = marker;
        this.phrase = phrase;
    }

    @Override
    public String toString() {
        return "model.Phrase{" +
                "marker='" + marker + '\'' +
                ", phrase='" + phrase + '\'' +
                '}';
    }

    public String getMarker() {
        return marker;
    }

    public String getPhrase() {
        return phrase;
    }
}
