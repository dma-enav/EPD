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
package dk.dma.epd.ship.layers.areanotice;

import java.awt.Color;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMRect;
import com.bbn.openmap.omGraphics.OMText;

/**
 * Simple text box to display AreaNotice message/message type
 */
public class ASTextBox extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    public ASTextBox(int xpos, int ypos, int width, int height, java.lang.String text) {
        super();
        super.clear();
        OMRect outline = new OMRect(xpos, ypos, xpos + width, ypos + height);

        outline.setFillPaint(Color.orange.brighter());
        OMText antext = new OMText(xpos + 10, ypos + 15, text, OMText.JUSTIFY_LEFT);

        super.add(antext);
        super.add(outline);
    }

}
