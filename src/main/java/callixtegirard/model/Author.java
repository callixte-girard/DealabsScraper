package callixtegirard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class Author implements Serializable {

    private String url; // full
    private String name;
    private List<Phrase> phrases;

    public Author(String url, String name, List<Phrase> phrases) {
        this.url = url;
        this.name = name;
        this.phrases = phrases;
    }

    @Override
    public String toString() {
        return "model.Author{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", phrases=" + phrases +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public List<Phrase> getPhrases()
    {
        if (phrases == null) {
            phrases = new ArrayList<>();
            phrases.add(new Phrase("[ERREUR]", "[erreur dans l'URL]"));
        }
        return phrases;
    }
}
