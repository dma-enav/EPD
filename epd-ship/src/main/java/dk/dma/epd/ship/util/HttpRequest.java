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
package dk.dma.epd.ship.util;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceErrorCode;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.util.Compressor;
import dk.dma.epd.ship.settings.EPDEnavSettings;

/**
 * Class for making HTTP requests. Accepts gziped responses.
 */
public class HttpRequest extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRequest.class);

    private static final String USER_AGENT = "ee-INS";

    private String uri;
    private String url;
    private final String host;
    private int port = 80;
    private int readTimeout = 60000; // 60 sec
    private int connectionTimeout = 30000; // 30 sec

    private HttpClient httpClient;
    private GetMethod method;
    private byte[] responseBody;

    /**
     * Constructor given uri and enav settings
     * 
     * @param uri
     * @param enavSettings
     */
    public HttpRequest(String uri, EPDEnavSettings enavSettings) {
        this.host = enavSettings.getServerName();
        this.port = enavSettings.getHttpPort();
        this.connectionTimeout = enavSettings.getConnectTimeout();
        this.readTimeout = enavSettings.getReadTimeout();
        setUri(uri);
    }

    /**
     * Initialize the class
     */
    public void init() {
        httpClient = new HttpClient();
        method = new GetMethod(url);
        HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
        params.setSoTimeout(readTimeout);
        params.setConnectionTimeout(connectionTimeout);
        method.setRequestHeader("User-Agent", USER_AGENT);
        method.setRequestHeader("Connection", "close");
        method.addRequestHeader("Accept", "text/*");
        method.addRequestHeader("Accept-Encoding", "gzip");
    }

    /**
     * Make the actual request
     * 
     * @throws ShoreServiceException
     */
    public void makeRequest() throws ShoreServiceException {
        int statusCode;
        try {
            statusCode = httpClient.executeMethod(method);
        } catch (HttpException e) {
            LOG.error("HTTP request failed with: " + e.getMessage());
            throw new ShoreServiceException(ShoreServiceErrorCode.INTERNAL_ERROR);
        } catch (IOException e) {
            LOG.error("Failed to make HTTP connection: " + e.getMessage());
            throw new ShoreServiceException(ShoreServiceErrorCode.NO_CONNECTION_TO_SERVER);
        }

        if (statusCode != 200) {
            method.releaseConnection();
            throw new ShoreServiceException(ShoreServiceErrorCode.SERVER_ERROR);
        }

        try {
            responseBody = method.getResponseBody();

            // Check for GZip content encoding
            Header contentEncoding = method.getResponseHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().toUpperCase().indexOf("GZIP") >= 0) {
                responseBody = Compressor.decompress(responseBody);
            }
            LOG.debug("Received XML: " + new String(responseBody));
        } catch (IOException e) {
            LOG.error("Failed to read response body: " + e.getMessage());
            throw new ShoreServiceException(ShoreServiceErrorCode.INVALID_RESPONSE);
        }

        method.releaseConnection();
    }

    /**
     * Set uri
     * 
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
        this.url = "http://" + host;
        if (port != 80) {
            this.url += ":" + port;
        }
        this.url += this.uri;
    }

    /**
     * Get response
     * 
     * @return
     */
    public byte[] getResponseBody() {
        return responseBody;
    }

    /**
     * Get URL
     * 
     * @return
     */
    public String getUrl() {
        return url;
    }

}
