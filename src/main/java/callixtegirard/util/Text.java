package callixtegirard.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

public class Text {


    public static String concatString(String... stringsToConcat)
    {
        String out = "";

        for (String s : stringsToConcat) {
            out += s;
        }

        return out;
    }


    public static String stripAccents(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }


    public static String firstLetterUpperCase(String inputString, boolean eachWord)
    {
        try
        {
            String[] spl = inputString.split(" ");
            String result = "" ;
            int i = 0;

            for (String s : spl)
            {
                i ++;

                if (eachWord || i == 1) result += s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase() + " " ;
                else result += s;
            }

            return result ;
        }
        catch (StringIndexOutOfBoundsException e)
        {
            // si exception, ca veut dire que inputval == ""
            return inputString ;
        }
    }

}
