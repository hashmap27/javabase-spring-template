package com.javabase.template.framework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeUtility;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/**
 * Stpring의 기본 FormHttpMessageConverter계열이 Map<String, ?> 변환.
 * Map<String, ?>의 변환을 처리하기 위한 Converter
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 *
 * @See FormHttpMessageConverter
 */
public class CommonMapHttpMessageConverter implements HttpMessageConverter<Map<String, ?>> {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private List<MediaType> supportedMediaTypes = new ArrayList<>();
    private List<HttpMessageConverter<?>> partConverters = new ArrayList<>();
    private Charset charset = DEFAULT_CHARSET;

    @Nullable
    private Charset multipartCharset;

    public CommonMapHttpMessageConverter() {
        this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);  // see SPR-7316

        this.partConverters.add(new ByteArrayHttpMessageConverter());
        this.partConverters.add(stringHttpMessageConverter);
        this.partConverters.add(new ResourceHttpMessageConverter());
    }

    /**
     * Set the list of {@link MediaType} objects supported by this converter.
     */
    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    /**
     * Set the message body converters to use. These converters are used to
     * convert objects to MIME parts.
     */
    public void setPartConverters(List<HttpMessageConverter<?>> partConverters) {
        Assert.notEmpty(partConverters, "'partConverters' must not be empty");
        this.partConverters = partConverters;
    }

    /**
     * Add a message body converter. Such a converter is used to convert objects
     * to MIME parts.
     */
    public void addPartConverter(HttpMessageConverter<?> partConverter) {
        Assert.notNull(partConverter, "'partConverter' must not be null");
        this.partConverters.add(partConverter);
    }

    /**
     * Set the default character set to use for reading and writing form data when
     * the request or response Content-Type header does not explicitly specify it.
     * <p>As of 4.3, this is also used as the default charset for the conversion
     * of text bodies in a multipart request.
     * <p>As of 5.0 this is also used for part headers including
     * "Content-Disposition" (and its filename parameter) unless (the mutually
     * exclusive) {@link #setMultipartCharset} is also set, in which case part
     * headers are encoded as ASCII and <i>filename</i> is encoded with the
     * "encoded-word" syntax from RFC 2047.
     * <p>By default this is set to "UTF-8".
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Set the character set to use when writing multipart data to encode file
     * names. Encoding is based on the "encoded-word" syntax defined in RFC 2047
     * and relies on {@code MimeUtility} from "javax.mail".
     * <p>As of 5.0 by default part headers, including Content-Disposition (and
     * its filename parameter) will be encoded based on the setting of
     * {@link #setCharset(Charset)} or {@code UTF-8} by default.
     * @since 4.1.1
     * @see <a href="http://en.wikipedia.org/wiki/MIME#Encoded-Word">Encoded-Word</a>
     */
    public void setMultipartCharset(Charset charset) {
        this.multipartCharset = charset;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            // We can't read multipart....
            if (!supportedMediaType.equals(MediaType.MULTIPART_FORM_DATA) && supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, String> read(Class<? extends Map<String, ?>> clazz, HttpInputMessage inputMessage) throws IOException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charsetField = (contentType.getCharset() != null ? contentType.getCharset() : this.charset);
        String body = StreamUtils.copyToString(inputMessage.getBody(), charsetField);

        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        Map<String, String> result = new HashMap<>(pairs.length);
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx == -1) {
                result.put(URLDecoder.decode(pair, charsetField.name()), null);
            }
            else {
                String name = URLDecoder.decode(pair.substring(0, idx), charsetField.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), charsetField.name());
                result.put(name, value);
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(Map<String, ?> map, MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        if (!isMultipart(map, contentType)) {
            writeForm((Map<String, String>) map, contentType, outputMessage);
        }
        else {
            writeMultipart((Map<String, Object>) map, outputMessage);
        }
    }

    private boolean isMultipart(Map<String, ?> map, MediaType contentType) {
        if (contentType != null) {
            return MediaType.MULTIPART_FORM_DATA.includes(contentType);
        }
        for (Map.Entry<String, ?> name : map.entrySet()) {
            @SuppressWarnings("unlikely-arg-type")
            Object value = map.get(name);
            if(value != null && !(value instanceof String)) {
                return true;
            }
        }
        return false;
    }

    private void writeForm(Map<String, String> form, MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        Charset charsetField;
        if(contentType != null) {
            outputMessage.getHeaders().setContentType(contentType);
            charsetField = contentType.getCharset() != null ? contentType.getCharset() : this.charset;
        } else {
            outputMessage.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            charsetField = this.charset;
        }

        StringBuilder builder = new StringBuilder();
        for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
            String name = nameIterator.next();
            String value = form.get(name);
            builder.append(URLEncoder.encode(name, charsetField.name()));
            if (value != null) {
                builder.append('=');
                builder.append(URLEncoder.encode(value, charsetField.name()));
            }
            if(nameIterator.hasNext()) {
                builder.append('&');
            }
        }
        final byte[] bytes = builder.toString().getBytes(charsetField.name());
        outputMessage.getHeaders().setContentLength(bytes.length);
        StreamUtils.copy(bytes, outputMessage.getBody());
    }

    private void writeMultipart(final Map<String, Object> parts, HttpOutputMessage outputMessage) throws IOException {
        final byte[] boundary = generateMultipartBoundary();
        Map<String, String> parameters = Collections.singletonMap("boundary", new String(boundary, "US-ASCII"));
        MediaType contentType = new MediaType(MediaType.MULTIPART_FORM_DATA, parameters);
        outputMessage.getHeaders().setContentType(contentType);
        writeParts(outputMessage.getBody(), parts, boundary);
        writeEnd(outputMessage.getBody(), boundary);
    }

    private void writeParts(OutputStream os, Map<String, Object> parts, byte[] boundary) throws IOException {
        for (Map.Entry<String, Object> entry : parts.entrySet()) {
            String name = entry.getKey();
            Object part = entry.getValue();
            if (part != null) {
                writeBoundary(os, boundary);
                writePart(name, getHttpEntity(part), os);
                writeNewLine(os);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void writePart(String name, HttpEntity<?> partEntity, OutputStream os) throws IOException {
        Object partBody = partEntity.getBody();
        Class<?> partType = partBody.getClass();
        HttpHeaders partHeaders = partEntity.getHeaders();
        MediaType partContentType = partHeaders.getContentType();
        for (HttpMessageConverter<?> messageConverter : this.partConverters) {
            if (messageConverter.canWrite(partType, partContentType)) {
                HttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os);
                multipartMessage.getHeaders().setContentDispositionFormData(name, getFilename(partBody));
                if (!partHeaders.isEmpty()) {
                    multipartMessage.getHeaders().putAll(partHeaders);
                }
                ((HttpMessageConverter<Object>) messageConverter).write(partBody, partContentType, multipartMessage);
                return;
            }
        }
        throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter " +
                "found for request type [" + partType.getName() + "]");
    }

    /**
     * Generate a multipart boundary.
     * <p>This implementation delegates to
     * {@link MimeTypeUtils#generateMultipartBoundary()}.
     */
    protected byte[] generateMultipartBoundary() {
        return MimeTypeUtils.generateMultipartBoundary();
    }

    /**
     * Return an {@link HttpEntity} for the given part Object.
     * @param part the part to return an {@link HttpEntity} for
     * @return the part Object itself it is an {@link HttpEntity},
     * or a newly built {@link HttpEntity} wrapper for that part
     */
    protected HttpEntity<?> getHttpEntity(Object part) {
        return (part instanceof HttpEntity ? (HttpEntity<?>) part : new HttpEntity<>(part));
    }

    /**
     * Return the filename of the given multipart part. This value will be used for the
     * {@code Content-Disposition} header.
     * <p>The default implementation returns {@link Resource#getFilename()} if the part is a
     * {@code Resource}, and {@code null} in other cases. Can be overridden in subclasses.
     * @param part the part to determine the file name for
     * @return the filename, or {@code null} if not known
     */
    protected String getFilename(Object part) {
        if (part instanceof Resource) {
            Resource resource = (Resource) part;
            String filename = resource.getFilename();
            if (this.multipartCharset != null) {
                filename = MimeDelegate.encode(filename, this.multipartCharset.name());
            }
            return filename;
        }
        else {
            return null;
        }
    }

    private void writeBoundary(OutputStream os, byte[] boundary) throws IOException {
        os.write('-');
        os.write('-');
        os.write(boundary);
        writeNewLine(os);
    }

    private static void writeEnd(OutputStream os, byte[] boundary) throws IOException {
        os.write('-');
        os.write('-');
        os.write(boundary);
        os.write('-');
        os.write('-');
        writeNewLine(os);
    }

    private static void writeNewLine(OutputStream os) throws IOException {
        os.write('\r');
        os.write('\n');
    }

    /**
     * Implementation of {@link org.springframework.http.HttpOutputMessage} used
     * to write a MIME multipart.
     */
    private static class MultipartHttpOutputMessage implements HttpOutputMessage {
        private final OutputStream outputStream;
        private final HttpHeaders headers = new HttpHeaders();
        private boolean headersWritten = false;
        public MultipartHttpOutputMessage(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public HttpHeaders getHeaders() {
            return (this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
        }

        @Override
        public OutputStream getBody() throws IOException {
            writeHeaders();
            return this.outputStream;
        }

        private void writeHeaders() throws IOException {
            if (!this.headersWritten) {
                for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                    byte[] headerName = getBytes(entry.getKey());
                    for (String headerValueString : entry.getValue()) {
                        byte[] headerValue = getBytes(headerValueString);
                        this.outputStream.write(headerName);
                        this.outputStream.write(':');
                        this.outputStream.write(' ');
                        this.outputStream.write(headerValue);
                        writeNewLine(this.outputStream);
                    }
                }
                writeNewLine(this.outputStream);
                this.headersWritten = true;
            }
        }

        private byte[] getBytes(String name) {
            try {
                return name.getBytes("US-ASCII");
            } catch(UnsupportedEncodingException ex) {
                // Should not happen - US-ASCII is always supproted.
                throw new IllegalStateException(ex);
            }
        }
    }

    /**
     * Inner class to avoid a hard dependency on the JavaMail API.
     */
    private static class MimeDelegate {
        private MimeDelegate() {}
        public static String encode(String value, String charset) {
            try {
                return MimeUtility.encodeText(value, charset, null);
            }
            catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
