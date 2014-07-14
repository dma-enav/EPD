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
package dk.dma.epd.common.graphics;

/**
 * Interface that a graphic can implement in order to allow for clients to
 * toggle selection of the graphic. For example, an implementation could add an
 * extra selection graphic (e.g. a ring surrounding the original graphic) or
 * change the current display of the graphic to visualize the
 * selection/deselection.
 * 
 * @author Janus Varmarken
 */
public interface ISelectableGraphic {
    /**
     * Update the selected status of this graphic.
     *
     * @param selected
     *            True if the graphic is selected, false if it is deselected.
     */
    void setSelectionStatus(boolean selected);

    /**
     * Get the selected status of this graphic.
     * 
     * @return True if this graphic is currently selected, false otherwise.
     */
    boolean getSelectionStatus();
}
