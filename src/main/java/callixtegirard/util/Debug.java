package callixtegirard.util;

import java.util.List;

public class Debug {

    public static final String l = concat('—', 60);
    public static final String s = concat('*', 90);
    public static final String h = concat('#', 120);

    public static String d(Object... objs) {
        String out = "";
        for (Object obj : objs) {
            if (obj.getClass().toString().contains("List")) dList((List<Object>) obj);
//            else if (obj.getClass().toString().contains("[]")) dList((Object[]) obj);
            else {
                String objStr = String.valueOf(obj);
                out += objStr + " ";
//                System.out.println(objStr);
            }
        }
        System.out.println(out);
        return out;
    }

    public static void dL(Object... objs) {
        for (Object obj : objs) {
            d(obj);
            d(l);
        }
    }

    private static void dList(List list) {
        for (Object obj : list) { d(obj); } d(l);
    }

    private static String concat(char c, int length) {
        String line = "";
        for (int i=0; i<length; i++) {
            line = line.concat(String.valueOf(c));
        }
        d(line);
        return line;
    }
}
