package nl.tomvanzummeren.appengine.fileupload;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Uses the Streaming API of the Commons File Upload framework to resolve multipart requests.
 *
 * @author Tom van Zummeren
 * @see org.springframework.web.multipart.commons.CommonsMultipartResolver
 */
public class CommonsStreamMultipartResolver implements MultipartResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMultipart(HttpServletRequest request) {
        return request != null && ServletFileUpload.isMultipartContent(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
        ServletFileUpload upload = new ServletFileUpload();
        MultipartParameters multipartParameters;
        try {
            FileItemIterator iterator = upload.getItemIterator(request);
            multipartParameters = parseFileItems(iterator);
        } catch (FileUploadException e) {
            throw new MultipartException("Could not parse multipart servlet request", e);
        } catch (IOException e) {
            throw new MultipartException("Could not parse multipart servlet request", e);
        }
        MultiValueMap<String, MultipartFile> fileParameters = multipartParameters.getMultipartFileParameters();
        Map<String, String[]> stringParameters = multipartParameters.getStringParameters();
        return new DefaultMultipartHttpServletRequest(request, fileParameters, stringParameters);
    }

    /**
     * Parses file items and adds them as either string parameters or multipart file parameters to a new instance of
     * {@code ParametersAndFiles}.
     *
     * @param iterator to iterate over file items
     * @throws FileUploadException when something goes wrong while parsing a file item
     * @throws IOException when something else goes wrong related to input streams.
     * @return parameters and files
     */
    private MultipartParameters parseFileItems(FileItemIterator iterator) throws FileUploadException, IOException {
        MultipartParameters multipartParameters = new MultipartParameters();
        while (iterator.hasNext()) {
            FileItemStream fileItemStream = iterator.next();
            String fieldName = fileItemStream.getFieldName();
            if (fileItemStream.getContentType() == null) {
                // The FileItemStream represents a simple String parameter when there is no content type
                String fieldValue = FileCopyUtils.copyToString(new InputStreamReader(fileItemStream.openStream()));
                multipartParameters.addStringParameter(fieldName, fieldValue);
            } else {
                // The FileItemStream represents an actual file
                MultipartFile multipartFile = new CommonsStreamMultipartFile(fileItemStream);
                multipartParameters.addMultipartFileParameter(fieldName, multipartFile);
            }
        }
        return multipartParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        // No cleanup necessary
    }

}
