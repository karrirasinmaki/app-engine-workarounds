package nl.tomvanzummeren.inboundmail;

import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * A file attachment of a {@code EmailMessage}.
 *
 * @author Tom van Zummeren
 * @see nl.tomvanzummeren.inboundmail.EmailMessage
 */
public class Attachment {

    private String contentType;

    private byte[] data;

    /**
     * Constructs a new {@code Attachment}.
     *
     * @param contentType content type of the file attachment
     * @param inputStream input stream for the file attachment
     * @throws IOException when anything goes wrong reading the input stream
     */
    public Attachment(String contentType, InputStream inputStream) throws IOException {
        if (contentType.contains(";")) {
            contentType = contentType.substring(0, contentType.indexOf(";"));
        }
        this.contentType = contentType;
        this.data = FileCopyUtils.copyToByteArray(inputStream);
    }

    /**
     * Gets the content type of the file attachment.
     *
     * @return content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the actual file contents of the attachment.
     *
     * @return file data
     */
    public byte[] getData() {
        return data;
    }
}
