package callixtegirard.util;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static callixtegirard.util.Debug.d;


public class ReadWriteFile
{
    // READ AND WRITE PROPERTIES
    public static Properties readProperties(String propFileName)
    {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream("resources/" + propFileName);
            props.load(in);
            in.close();
            return props;
        } catch (IOException ioException) {
            d("Properties file [ " + propFileName + " ] could not be read !");
            return null;
        }
    }


    // READ AND WRITE TEXT TO & FROM FILES
    public static BufferedWriter outputWriter(String path) throws IOException
    {
        // creates file if it doesn't exist yet
        File f = new File(path);
        if (!f.exists()) f.createNewFile();

        BufferedWriter bw = new BufferedWriter
                // 1) writes file in... some format (probably ANSI).
//                (new FileWriter(f));
                // 2) writes file in UTF-8 format
                (new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));
        return bw;
    }

    public static BufferedReader outputReader(String path) throws FileNotFoundException
    {
//        File f = new File(path);

        BufferedReader br = new BufferedReader
                // 1) reads file in... some format (probably ANSI).
//                (new FileReader(f));
                // 2) reads file in UTF-8 format
                (new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
        return br ;
    }


    // READ FOLDERS AND FILES CONTENT
    public static ArrayList<File> getFilesInFolder(String full_path)
    {
        File folder = new File(full_path);
        File[] files = folder.listFiles();
        ArrayList<File> files_al = new ArrayList<>(Arrays.asList(files));
        return files_al;
    }

    public static ArrayList<String> getFileContent(String full_path) throws IOException
    {
        BufferedReader br = ReadWriteFile.outputReader(full_path);

        ArrayList<String> out = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            // ignore lines that are empty or just containing spaces
            if (! line.trim().equals("")) out.add(line);
        }
        return out;
    }



}
