package callixtegirard;

public class ScraperTest
{
    private String s = "mojePappou";
    public ScraperInterface parser /*= (Document doc) -> {
        return this.s;
    }*/;

    public ScraperTest(ScraperInterface method) {
        this.parser = method;
    }

}
