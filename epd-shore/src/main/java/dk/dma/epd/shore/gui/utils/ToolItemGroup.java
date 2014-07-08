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
package dk.dma.epd.shore.gui.utils;

import java.util.ArrayList;

import javax.swing.JLabel;

public class ToolItemGroup {

    private ArrayList<JLabel> toolItems = new ArrayList<JLabel>();
    private boolean singleEnable;



    public void addToolItem(JLabel toolItem) {
        this.toolItems.add(toolItem);
    }

    public void setToolItems(ArrayList<JLabel> toolItems) {
        this.toolItems = toolItems;
    }

    public ArrayList<JLabel> getToolItems() {
        return toolItems;
    }

    public void setSingleEnable(boolean singleEnable) {
        this.singleEnable = singleEnable;
    }

    public boolean isSingleEnable() {
        return singleEnable;
    }

}
