/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.epd.common.prototype.communication.webservice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.settings.network.NetworkSettings;
import dk.dma.epd.common.util.Compressor;

/**
 * Encapsulation of HTTP connection to shore. 
 */
public class ShoreHttp {

    private static final Logger LOG = LoggerFactory.getLogger(ShoreHttp.class);

    private static final String USER_AGENT = "EPD";
    private static final String ENCODING = "UTF-8";

    private String uri;
    private String url;

    private final NetworkSettings<?> settings;
    
    private HttpClient httpClient;
    private PostMethod method;
    private byte[] responseBody;

    public ShoreHttp(String uri, NetworkSettings<?> settings) {
        this.settings = Objects.requireNonNull(settings);
        setUri(uri);
    }
    
    public NetworkSettings<?> getSettings() {
        return this.settings;
    }

    public void makeRequest() throws ShoreServiceException {
        // Make the request
        int resCode = -1;
        try {
            resCode = httpClient.executeMethod(method);
        } catch (HttpException e) {
            LOG.error("HTTP request failed with: " + e.getMessage());
            throw new ShoreServiceException(ShoreServiceErrorCode.INTERNAL_ERROR);
        } catch (IOException e) {
            LOG.error("Failed to make HTTP connection: " + e.getMessage());
            throw new ShoreServiceException(ShoreServiceErrorCode.NO_CONNECTION_TO_SERVER);
        }

        if (resCode != 200) {
            method.releaseConnection();
            throw new ShoreServiceException(ShoreServiceErrorCode.SERVER_ERROR);
        }

        try {
            responseBody = method.getResponseBody();
            int rawResSize = responseBody.length;

            // Check for GZip content encoding
            Header contentEncoding = method.getResponseHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().toUpperCase().indexOf("GZIP") >= 0) {
                responseBody = Compressor.decompress(responseBody);
            }        
            LOG.debug("Received XML: " + new String(responseBody));
            LOG.debug("Received XML size    : " + responseBody.length);
            LOG.debug("Received raw XML size: " + rawResSize);
        } catch (IOException e) {
            LOG.error("Failed to read response body: " + e.getMessage());
            throw new ShoreServiceException(ShoreServiceErrorCode.INVALID_RESPONSE);
        }

        method.releaseConnection();
    }

    public void init() {
        httpClient = new HttpClient();
        method = new PostMethod(url);
        HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
        params.setSoTimeout(settings.getReadTimeout());
        params.setConnectionTimeout(settings.getConnectTimeout());
        method.setRequestHeader("User-Agent", USER_AGENT);
        method.setRequestHeader("Connection", "close");
        method.addRequestHeader("Accept", "text/*");    
        
        // TODO if compress response
        method.addRequestHeader("Accept-Encoding", "gzip");
    }

    public Object getXmlUnmarshalledContent(String contextPath) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(contextPath);
        Unmarshaller u = jc.createUnmarshaller();
        return u.unmarshal(new ByteArrayInputStream(responseBody));
    }

    public void setXmlMarshalContent(String contextPath, Object obj) throws JAXBException, UnsupportedEncodingException {
        JAXBContext jc = JAXBContext.newInstance(contextPath);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
        StringWriter sw = new StringWriter();        
        m.marshal(obj, sw);
        String req = sw.toString();
        LOG.debug("XML request: " + req);
        setRequestBody(sw.toString().getBytes(ENCODING), ENCODING);
    }

    public void setRequestBody(byte[] body, String contentType) {
        // TODO if Gzip Compress request
        byte[] compressed = {};
        try {            
            compressed = Compressor.compress(body);                        
            //body = compressed;
            //method.addRequestHeader("Content-Encoding", "gzip");
        } catch (IOException e) {
            LOG.error("Failed to GZip request: " + e.getMessage());
        }        
        LOG.debug("XML req size           : " + body.length);
        LOG.debug("XML req compressed size: " + compressed.length);
        ByteArrayRequestEntity requestEntity = new ByteArrayRequestEntity(body, contentType);
        method.setRequestEntity(requestEntity);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
        this.url = "http://" + settings.getHost();
        if (settings.getPort() != 80) {
            this.url += ":" + settings.getPort();
        }
        this.url += this.uri;
    }
}
