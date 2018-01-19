package fr.uga.miashs.sempic.model.datalayer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class PictureStore {

    private Path pictureStore;
    private Path thumbnailsStore;

    public PictureStore(Path pictureStore, Path thumbnailsStore) throws IOException {
        this.pictureStore = pictureStore;
        this.thumbnailsStore = thumbnailsStore;
        if (Files.notExists(pictureStore)) {
            Files.createDirectories(pictureStore);
        }
        if (Files.notExists(thumbnailsStore)) {
            Files.createDirectories(thumbnailsStore);
        }
    }

    public InputStream getPictureStream(Path picPath) throws IOException {
        return Files.newInputStream(getAbsolutePath(pictureStore, picPath), StandardOpenOption.READ);
    }

    public InputStream getPictureStream(Path picPath, int width) throws IOException {
        if (width < 0) {
            throw new RuntimeException("thumbnail width cannot be negative");
        }
        // Thumbnail path have the form: /path/to/store/width/path/to/picture
        Path p = getAbsolutePath(thumbnailsStore.resolve(String.valueOf(width)), picPath);

        if (Files.notExists(p)) {
            BufferedImage bim = ImageIO.read(getAbsolutePath(pictureStore, picPath).toFile());
            int height = (int) (bim.getHeight() * (((double) width) / bim.getWidth()));
            Image resizedImg = bim.getScaledInstance(width, height, Image.SCALE_FAST);
            BufferedImage rBimg = new BufferedImage(width, height, bim.getType());
            // Create Graphics object
            Graphics2D g = rBimg.createGraphics();

            // Draw the resizedImg from 0,0 with no ImageObserver
            g.drawImage(resizedImg, 0, 0, null);

            // Dispose the Graphics object, we no longer need it
            g.dispose();
            Files.createDirectories(p.getParent());
            ImageIO.write(rBimg, "png", Files.newOutputStream(p, StandardOpenOption.CREATE));
        }
        return Files.newInputStream(p, StandardOpenOption.READ);
    }

    public void storePicture(Path picPath, InputStream data) throws IOException {
        picPath = getAbsolutePath(pictureStore, picPath);
        Files.createDirectories(picPath.getParent());
        Files.copy(data, picPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void deletePicture(Path picPath) throws IOException {
        Path originalP = getAbsolutePath(pictureStore, picPath);
        try {
            Files.deleteIfExists(originalP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Files.deleteIfExists(originalP.getParent());
        } catch (DirectoryNotEmptyException e) {
        }

        for (Path p : Files.newDirectoryStream(thumbnailsStore)) {
            Path tp = getAbsolutePath(p, picPath);
            Files.deleteIfExists(tp);
            try {
                Files.deleteIfExists(tp.getParent());
            } catch (DirectoryNotEmptyException e) {
            }
        }
    }

    private static Path getAbsolutePath(Path store, Path path) throws IOException {
        // Normalize the path and check that we do not go before pictureStore
        // for security reasons
        // i.e. we do not allow /path/to/store + ../../ddsdd
        path = store.resolve(path);
        path = path.normalize();
        if (!path.startsWith(store)) {
            throw new FileNotFoundException();
        }
        return path;
    }

    // to be redefine in order to suppress also directories
    public void emptyCache() throws IOException {
        Files.walk(thumbnailsStore).filter(p -> Files.isRegularFile(p)).forEach(p -> {
            try {
                Files.delete(p);
            } catch (IOException ex) {
                Logger.getLogger(PictureStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public static long copy(InputStream source, OutputStream sink) throws IOException {
        long nread = 0L;
        byte[] buf = new byte[8192];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }
}
