/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common.prototype.gui.util;

/**
 * Utility class to build an {@code InfoPanel} that uses HTML for its content.
 * This class manages document opening and closing tags. A sub class should
 * override {@link #produceBodyContent(Object)} with the content it want to
 * display within the body section of the HTML document.
 * 
 * @author Janus Varmarken
 * @param <T>
 *            The type that carries the data to be displayed in the produced
 *            HTML document.
 */
@SuppressWarnings("serial")
public abstract class HtmlInfoPanel<T> extends InfoPanel {

    /**
     * The HTML opening tag.
     */
    public static final String HTML_START = "<html>";

    /**
     * The HTML closing tag.
     */
    public static final String HTML_END = "</html>";

    /**
     * The HTML body opening tag.
     */
    public static final String BODY_START = "<body>";

    /**
     * The HTML body closing tag.
     */
    public static final String BODY_END = "</body>";

    /**
     * The HTML line break tag.
     */
    public static final String BR_TAG = "<br/>";

    /**
     * The HTML bold opening tag.
     */
    public static final String BOLD_START = "<b>";

    /**
     * The HTML bold closing tag.
     */
    public static final String BOLD_END = "</b>";

    /**
     * {@code StringBuilder} used to produce the HTML displayed by this
     * {@code HtmlInfoPanel}.
     */
    protected StringBuilder builder = new StringBuilder();

    /**
     * The object that provides the data to be displayed. This reference is
     * updated each time {@link #buildHtml(Object)} is called.
     */
    protected T dataObject;

    /**
     * Builds the entire HTML document and returns it as a string. Stores the
     * {@link #dataObject} variable in a local field for later use, e.g. with
     * {@link #showText(String)}.
     * 
     * @param dataObject
     *            Object providing the data to be displayed in the HTML
     *            document.
     * @return The HTML document as a string.
     */
    protected final String buildHtml(T dataObject) {
        this.dataObject = dataObject;
        // write opening <html> tag
        this.builder.append(HTML_START);
        // write opening <body> tag
        this.builder.append(BODY_START);
        // write body contents
        this.produceBodyContent(dataObject);
        // write closing </body> tag
        this.builder.append(BODY_END);
        // write closing </html> tag
        this.builder.append(HTML_END);
        // clear StringBuilder for next invocation
        String text = this.builder.toString();
        this.builder = new StringBuilder();
        return text;
    }

    /**
     * Produces the body content of the HTML displayed by this
     * {@code HtmlInfoPanel}, i.e. every piece of HTML besides the html and body
     * open and close tags. A sub class should invoke
     * {@code super.produceBodyContent(T)} if it wishes to display the HTML that
     * its super class produces. A sub class should append to the
     * {@link #builder} variable of this class if it wishes to add any
     * additional HTML as part of the body content.
     * 
     * @param dataObject
     *            Contains the data to be displayed in this {@code InfoPanel}.
     */
    protected abstract void produceBodyContent(T dataObject);

}
