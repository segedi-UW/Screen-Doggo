import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;

/**
 * Utility class that methods for utilizing class path resources.
 * @author youngAgFox
 *
 */
public class ResourceParser {

    private static final String classPath = System.getProperty("java.class.path");

    /**
     * Searches class path for resources with the matching key. Not safe for use in
     * jar files.
     * @param key The key that the resource name contains
     * @return An ArrayList of resources containing the key. If empty there were no matches
     */
    public static ArrayList<String> getResourcesByKey(String key) {
        return getResourcesByKey(classPath, key);
    }

    /**
     * Searches provided path for resources with the matching key
     * @param path The path to start searching on
     * @param key The key to match
     * @return An ArrayList of resources containing the key, or an empty ArrayList
     * if there were no matches.
     */
    public static ArrayList<String> getResourcesByKey(String path, String key) {
        ArrayList<String> resList = new ArrayList<>();
        resList.addAll(getResourcesByKeyHelper(path, key));
        return resList;
    }

    /**
     * Private recursive helper method for getResourcesByKey(). Searches every file
     * in each directory on the given path for names containing the key.
     * @param path The file path.
     * @param key The key to match
     * @return An ArrayList with all matching resources, or an empty ArrayList if there were no matches.
     */
    private static ArrayList<String> getResourcesByKeyHelper(String path, String key) {
        ArrayList<String> resList = new ArrayList<String>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                resList.addAll(getResourcesByKeyHelper(files[i].getAbsolutePath(), key));
            }
        } else {
            if (file.getName().contains(key)) {
                resList.add(file.getAbsolutePath());
            }
        }
        return resList;
    }

    /**
     * Returns the resource File. Not jar safe.
     * @return A File object that is based off the class path.
     */
    public static File getResourceFile(Object object) {
        return new File(object.getClass().getResource(classPath).getPath());
    }

    /**
     * Returns the directory contents of a given filename. Note 
     * that the name needs to match exactly. Can search through jar files as well.
     * @param filename The name of the directory to find
     * @return A string array containing the filenames of the directories contents
     * @throws NoSuchElementException if the filename does not exist in the class path
     */
    public static String[] getResourcesFromFile(String filename) {
        return getResourcesFromFileHelper(classPath, filename);
    }

    /**
     * Returns the directory contents of a given filename (where the filename matches exactly).
     * Convenience method that calls makeResourceReady() after a call to getResourcesFromFile(String).
     * @param filename The name of the directory to find.
     * @param resourceReady if this array should contain Strings ready for use in the class loader
     * @return A string array containing the filenames of the directories contents.
     * @throws NoSuchElementException if the filename does not exist in the class path
     */
    public static String[] getResourcesFromFile(String filename, boolean resourceReady) {
        String[] resources = getResourcesFromFile(filename);
        if (resourceReady)
            makeResourceReady(resources);
        return resources;
    }

    /**
     * Private recursive helper method for getResourcesFromFile(). Checks each directory
     * name for a match, and returns the matched directories contents. Note that this considers
     * a special case for jar files.
     * @param path The current file path
     * @param filename The name of the directory to find
     * @throws NoSuchElementException if the filename does not exist in the class path
     * @return A String array of all resources in the given filename
     */
    private static String[] getResourcesFromFileHelper(String path, String filename) {
        File file = new File(path);
        if (isParsingJar()) {
            if (file.isFile()) {  // Run with JAR file
                JarFile jar = null;
                try {
                    ArrayList<String> files = new ArrayList<>(50);
                    jar = new JarFile(file);
                    final Enumeration<JarEntry> ENTRIES = jar.entries(); // gives ALL entries in jar
                    while (ENTRIES.hasMoreElements()) {
                        final String name = ENTRIES.nextElement().getName();
                        if (name.contains(filename)) {
                            char end = name.charAt(name.length() - 1);
                            if (end != '\\' && end != '/' && end != File.separatorChar)
                                files.add(name);
                        }
                    }
                    return files.toArray(new String[files.size()]);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                } finally {
                    if (jar != null)
                        try {
                            jar.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                }
            }
        } else {
            if (file.isDirectory()) {
                if (file.getName().equals(filename)) {
                    return getAbsPathList(file.listFiles());
                } else {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {
                            try {
                                return getResourcesFromFileHelper(files[i].getAbsolutePath(),
                                    filename);
                            } catch (NoSuchElementException e) {
                                // Not done searching yet
                            }
                        }
                    }
                }
            }
        }
        throw new NoSuchElementException("There were no directories by the name of : " + filename);
    }

    /**
     * Gets a list of the absolute paths from a File Array
     * @param listFiles The File array to get the absolute paths from
     * @return A String array of all absolute file paths
     */
    private static String[] getAbsPathList(File[] listFiles) {
        String[] pathArr = new String[listFiles.length];
        for (int i = 0; i < pathArr.length; i++) {
            pathArr[i] = listFiles[i].getAbsolutePath();
        }
        return pathArr;
    }

    /**
     * Searches class path for resources with the matching extension
     * @param ext The file extension to match
     * @return An ArrayList of resource names matching the extension
     */
    public static ArrayList<String> getResourcesByExtension(String ext) {
        ArrayList<String> resList = new ArrayList<>();
        resList.addAll(getResourcesByExtensionHelper(classPath, ext));
        return resList;
    }

    /**
     * Private recursive method that checks all directories and files in the given path for
     * the extension. Returns ArrayList of all matching resource names (as Strings).
     * @param path The String path to the file.
     * @param ext The String extension to match.
     * @return An ArrayList containing all resources that had the correct extension, 
     * or an empty ArrayList if there were no matches.
     * 
     */
    private static ArrayList<String> getResourcesByExtensionHelper(String path, String ext) {
        ArrayList<String> resList = new ArrayList<String>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                resList.addAll(getResourcesByKeyHelper(files[i].getAbsolutePath(), ext));
            }
        } else {
            if (file.getName().endsWith(ext)) {
                resList.add(file.getAbsolutePath());
            }
        }
        return resList;
    }

    /**
     * Provides an array of Strings that contains all files within the specified argument
     * directories.
     * @param files The names of files to search for.
     * @return An array of the contents of passed directories. If passed files were
     * not directories or were empty, an empty array is returned.
     */
    public static String[] getResourcesFromFiles(String[] files) {
        String[] retArr = new String[0];
        for (int i = 0; i < files.length; i++) {
            String[] res = getResourcesFromFile(files[i]);
            int index = retArr.length;
            retArr = Arrays.copyOf(retArr, retArr.length + res.length);
            int k = 0;
            for (int j = index; j < retArr.length; j++) {
                retArr[j] = res[k];
                k++;
            }
        }
        return retArr;
    }

    /**
     * Parses all Strings in the passed array for "bin", working backwards. After finding bin
     * all previous parts of the path (including "bin") are truncated. The resultant Strings
     * can be used by the ClassLoader getResource() methods.
     * @param resources The resource Strings to parse.
     * @return A String[] array containing the parsed Strings or the same Strings if "bin" was not found.
     */
    public static String[] makeResourceReady(ArrayList<String> resources) {
        String[] arr = new String[resources.size()];
        arr = resources.toArray(arr);
        makeResourceReady(arr);
        return arr;
    }

    /**
     * Parses the passed Strings for "bin", working backwards. After finding bin
     * all previous parts of the path (including "bin") are truncated. The resultant Strings
     * can be used by the ClassLoader getResource() methods.
     * @param resources The resource Strings to parse.
     * @return A String array containing the parsed Strings or the same Strings if "bin" was not found.
     */
    public static void makeResourceReady(String[] resArr) {
        for (int i = 0; i < resArr.length; i++) {
            resArr[i] = makeResourceReady(resArr[i]);
        }
    }

    /**
     * Parses the passed String for "bin", working backwards. After finding bin
     * all previous parts of the path (including "bin") are truncated. The resultant String
     * can be used by the ClassLoader getResource() methods.
     * @param resource The resource String to parse.
     * @return A String containing the parsed String or the same String if "bin" was not found.
     */
    public static String makeResourceReady(String resource) {
        int binIndex = resource.indexOf("bin");
        resource = binIndex >= 0 ? resource.substring(binIndex + 3) : resource;
        return resource;
    }

    /**
     * Returns IO.read on String that is loaded as a resource.
     * @param filename The name of the resource image
     * @return BufferedImage The image loaded in from the resource
     * @throws IOException if The resource is not an appropriate type (file)
     */
    public static BufferedImage getImageFromResource(String filename) throws IOException {
        return ImageIO.read(ClassLoader.getSystemResource(filename));
    }

    /**
     * Loads in a uniform sprite sheet with sprites of a set width and height
     * @param WIDTH The width of the sprites
     * @param HEIGHT The height of the sprites
     * @param spriteSheet The spriteSheet image to process
     * @return An array of sprites that are the same width and height as provided.
     */
    public static BufferedImage[] loadSpriteSheet(final int WIDTH, final int HEIGHT,
        BufferedImage spriteSheet) {
        BufferedImage[] sprites = new BufferedImage[(spriteSheet.getWidth() / WIDTH)
            * (spriteSheet.getHeight() / HEIGHT)];
        int index = 0;
        for (int h = 0; h < spriteSheet.getHeight(); h += HEIGHT) {
            for (int w = 0; w < spriteSheet.getWidth(); w += WIDTH) {
                sprites[index] = spriteSheet.getSubimage(w, h, WIDTH, HEIGHT);
                index++;
            }
        }
        return sprites;
    }

    /**
     * Resizes an BufferedImage to a scaled image. Scales using a Raster that stores data on RGB and
     * alpha value data.
     * @param image The image to scale.
     * @param SCALE_FACTOR The coefficient to scale the image by. Negative numbers provide
     * decreases in size by given factor.
     * @return A resized image to the provided factor.
     * @throws IllegalArgumentException if SCALE_FACTOR is equal to zero.
     */
    public static BufferedImage resizeImage(BufferedImage image, final int SCALE_FACTOR) {
        if (SCALE_FACTOR == 0)
            throw new IllegalArgumentException("Cannot scale an image by zero");
        final int SCALED_PIXELS_X;
        final int SCALED_PIXELS_Y;
        if (SCALE_FACTOR > 0) {
            SCALED_PIXELS_X = image.getWidth() * SCALE_FACTOR;
            SCALED_PIXELS_Y = image.getHeight() * SCALE_FACTOR;
        } else {
            SCALED_PIXELS_X = image.getWidth() / SCALE_FACTOR;
            SCALED_PIXELS_Y = image.getHeight() / SCALE_FACTOR;
        }
        BufferedImage scaledImage =
            new BufferedImage(SCALED_PIXELS_X, SCALED_PIXELS_Y, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2D = scaledImage.createGraphics();
        g2D.drawImage(image, 0, 0, SCALED_PIXELS_X, SCALED_PIXELS_Y, null);

        return scaledImage;
    }

    /**
     * Resizes an array of BufferedImage.
     * @param images the images to resize
     * @param SCALE_FACTOR The coefficient to multiply the pixels by, or divide by if it is negative
     * @throws IllegalArgumentException if the SCALE_FACTOR is zero
     */
    public static void resizeImages(BufferedImage[] images, final int SCALE_FACTOR) {
        for (int i = 0; i < images.length; i++) {
            images[i] = resizeImage(images[i], SCALE_FACTOR);
        }
    }

    /**
     * Reads a text file and returns the String
     * @param string
     * @return
     */
    public static String readFile(String file) {
        Scanner scan = null;
        String fileText = "";
        try {
            scan = new Scanner(ResourceParser.class.getResourceAsStream(file));
            while (scan.hasNextLine()) {
                fileText += scan.nextLine() + "\n";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (scan != null)
                scan.close();
        }
        return fileText;
    }

    /**
     * Reads the file as an array, separating each statement by the newline
     * @param file the name of the file
     * @return The String[] contents of the file, where each new line starts the next element
     * in the array.
     */
    public static String[] readFileToArray(String file) {
        return readFile(file).split("\n");
    }

    /**
     * Determines if the classPath is running from a file system or jar file
     * @return true if from a jar file, false if from a file system
     */
    public static boolean isParsingJar() {
        return classPath.endsWith(".jar") ? true : false;
    }

}
