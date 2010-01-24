package nl.tomvanzummeren.images;

import com.google.appengine.api.images.Image;

/**
 * Utility class for Google App Engine's <code>Image</code> class.
 *
 * @author Tom van Zummeren
 * @see com.google.appengine.api.images.Image Image
 */
public class ImageUtils {

    private ImageUtils() {
    }

    public static String getContentType(Image image) {
        switch (image.getFormat()) {
            case BMP: return "image/bmp";
            case GIF: return "image/gif";
            case ICO: return "image/ico";
            case JPEG: return "image/jpg";
            case PNG: return "image/png";
            case TIFF: return "image/tiff";
        }
        throw new IllegalArgumentException("Unknown image format: " + image.getFormat());
    }
}
