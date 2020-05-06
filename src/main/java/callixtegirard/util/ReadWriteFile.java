package callixtegirard.util;

import org.w3c.dom.events.UIEvent;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static callixtegirard.util.Debug.d;


public class ReadWriteFile
{
    // DOWNLOAD AND SAVE FILE
    public static void writeFileFromURL(String url, String path) throws IOException
    {
        InputStream in = new URL(url).openStream();
        Files.copy(in, Paths.get(path));
    }


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

        return new BufferedWriter(new FileWriter(f));
    }

    public static BufferedWriter outputWriter(String path, Charset encoding) throws IOException
    {
        // creates file if it doesn't exist yet
        File f = new File(path);
        if (!f.exists()) f.createNewFile();

        // encodings
//        StandardCharsets.UTF_8;
//        StandardCharsets.ISO_8859_1;

        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), encoding));
    }

    public static BufferedReader outputReader(String path) throws FileNotFoundException
    {
        File f = new File(path);
        return new BufferedReader(new FileReader(f));
    }

    public static BufferedReader outputReader(String path, Charset encoding) throws FileNotFoundException
    {
        File f = new File(path);

        // encodings
//        StandardCharsets.UTF_8;
//        StandardCharsets.ISO_8859_1;

        return new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
    }


    // READ FOLDERS AND FILES CONTENT
    public static void createFolderIfNotExists(String path) throws IOException
    {
        File f = new File(path);
        if (!f.exists()) f.mkdir();
    }

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
