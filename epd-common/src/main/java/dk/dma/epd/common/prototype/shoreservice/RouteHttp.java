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
package dk.dma.epd.common.prototype.shoreservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.settings.EnavSettings;

/**
 * Encapsulation of HTTP connection to shore.
 */
public class RouteHttp {

    private static final Logger LOG = LoggerFactory.getLogger(RouteHttp.class);

    private static final String USER_AGENT = "EPD";

    private String uri = "";
    private String url;
    private String host;
    private int port = 80;
//    private int readTimeout = 60000; // 60 sec
//    private int connectionTimeout = 30000; // 30 sec

    private HttpClient httpClient;
    private PostMethod method;
    private String responseBody;

    public RouteHttp(EnavSettings enavSettings) {
        this.host = enavSettings.getMonaLisaServer();
        this.port = enavSettings.getMonaLisaPort();

        setUri(uri);
    }

    public void makeRequest() throws Exception {
        // Make the request
        int resCode = -1;
        try {
            // System.out.println("Trying to connect to server");
            resCode = httpClient.executeMethod(method);
            // System.out.println("Connected!");
        } catch (HttpException e) {
            LOG.error("Failed to make HTTP connection: " + e.getMessage());
            LOG.error("HTTP request failed with: " + e.getMessage());
            // throw new
            // ShoreServiceException(ShoreServiceErrorCode.INTERNAL_ERROR);
        } catch (IOException e) {
            LOG.error("Failed to make HTTP connection: " + e.getMessage());
            System.out.println("Failed: " + e.getMessage());
            // throw new
            // ShoreServiceException(ShoreServiceErrorCode.NO_CONNECTION_TO_SERVER);
        }

        // System.out.println(resCode);

        if (resCode != 200) {
            method.releaseConnection();
        }

        try {
            responseBody = method.getResponseBodyAsString();
            // System.out.println(responseBody);
        } catch (IOException e) {
            LOG.error("Failed to read response body: " + e.getMessage());
            // throw new
            // ShoreServiceException(ShoreServiceErrorCode.INVALID_RESPONSE);
        }

        method.releaseConnection();
    }

    public void init(int timeout) {
        httpClient = new HttpClient();
        method = new PostMethod(url);
        HttpConnectionManagerParams params = httpClient
                .getHttpConnectionManager().getParams();
        // params.setSoTimeout(readTimeout);
        // params.setConnectionTimeout(connectionTimeout);
        params.setSoTimeout(timeout);
        params.setConnectionTimeout(timeout);
        method.setRequestHeader("User-Agent", USER_AGENT);
        method.setRequestHeader("Connection", "close");
        method.addRequestHeader("Accept", "text/*");
        method.addRequestHeader("Content-Type", "text/xml");

        // TODO if compress response
        // method.addRequestHeader("Accept-Encoding", "gzip");
    }

    public void setRequestBody(String route) {
        try {
            method.setRequestEntity(new StringRequestEntity(route, null, null));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setUri(String uri) {
        this.uri = uri;
        this.url = "http://" + host;
        if (port != 80) {
            this.url += ":" + port;
        }
        // this.url += this.uri;
    }

    public String getUri() {
        return uri;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getResponseBody() {
        return responseBody;
    }

}
