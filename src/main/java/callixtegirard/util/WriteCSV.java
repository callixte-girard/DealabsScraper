package callixtegirard.util;

import callixtegirard.model.Attribute;
import callixtegirard.model.Item;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static callixtegirard.util.Debug.d;


public class WriteCSV
{
    private static final String NO_DATA = "-";


    /*public static void pipo(String path, List toWrite, FormatAttribute formatAttributes)
    {
        BufferedWriter bw = ReadWriteFile.outputWriter(path);
        List<String> lineAttributesToWrite = new ArrayList<>();
        for (Object obj : toWrite) {

            WriteCSV.writeLine(bw, lineAttributesToWrite, "|", true);
        }
        // don't forget to close this little motherfucker !
        bw.close();
    }*/


    public static void exportFields
            (List toWrite, String fullPath, String listSeparator, boolean addQuotes, boolean withHeaders)
            throws IOException
    {
        List<Field> fields = ClassReader.getFields(toWrite.get(0), true);
        List fieldsNames = ClassReader.getFieldsNames(toWrite.get(0), true);
//            d(fields);
//            d(fieldsNames);

        BufferedWriter bw = ReadWriteFile.outputWriter(fullPath);

        // write headers ?
        if (withHeaders) writeLine(bw, fieldsNames, listSeparator, addQuotes);

        // write all objects one by one
        for (Object obj : toWrite)
        {
            List<Object> fieldsValues = ClassReader.getFieldsValues(obj, fields);
//            d(fieldsValues);
            writeLine(bw, fieldsValues, listSeparator, addQuotes);
        }

        // FUCKING LINE OF DEATH !!!!!
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // sub methods
    public static void writeLine(BufferedWriter bw, List attrsToWrite, String valuesSeparator, boolean addQuotes)
    {
        try {
            int attrIndex = 0;
            for (Object attr : attrsToWrite)
            {
                String toWrite = "";
                if (attr != null)
                {
                    if (addQuotes) toWrite += "\"";
                    toWrite += String.valueOf(attr);
                    if (addQuotes) toWrite += "\"";
                }
                else toWrite = NO_DATA;

                bw.write(toWrite);

                attrIndex ++;
                if (attrIndex < attrsToWrite.size()) bw.write(valuesSeparator);
            }
//            bw.write("\\r\\n"); // beurk (windows)
            bw.newLine();

        } catch (IOException e) { d(e); }
    }
}
