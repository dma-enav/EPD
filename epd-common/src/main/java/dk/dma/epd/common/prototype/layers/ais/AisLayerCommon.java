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
package dk.dma.epd.common.prototype.layers.ais;

import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.AtoNTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.layers.LazyLayerCommon;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;

/**
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public abstract class AisLayerCommon<AISHANDLER extends AisHandlerCommon>
        extends LazyLayerCommon implements IAisTargetListener, AisLayerCommonSettings.IObserver {

    private static final Logger LOG = LoggerFactory
            .getLogger(AisLayerCommon.class);

    /**
     * The AIS handler that provides AIS data for this layer.
     */
    protected volatile AISHANDLER aisHandler;
    
    /**
     * Maps an MMSI to the object handling its graphical representation.
     */
    private Map<Long, TargetGraphic> targets = new ConcurrentHashMap<>();

    /**
     * The graphic that is currently selected by the user.
     */
    private ISelectableGraphic selectedGraphic;
    
    protected final PastTrackInfoPanel pastTrackInfoPanel = new PastTrackInfoPanel();
    
    /**
     * Creates a new {@link AisLayerCommon}.
     * @param settings An {@link AisLayerCommonSettings} instance that is to control the appearance of the new layer. It is up to the caller to register this layer as observer of the given settings.
     */
    public AisLayerCommon(AisLayerCommonSettings<?> settings) {
        super(settings.getLayerRedrawInterval() * 1000, settings);
        // receive left-click events for the following set of classes.
        this.registerMouseClickClasses(VesselGraphic.class);
        // receive right-click events for the following set of classes.
        this.registerMapMenuClasses(VesselGraphic.class, SartGraphic.class);
        // Register graphics for mouse over notifications
        this.registerInfoPanel(this.pastTrackInfoPanel, PastTrackWpCircle.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof AisHandlerCommon) {
            this.aisHandler = (AISHANDLER) obj;
            this.aisHandler.addListener(this);
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == this.aisHandler) {
            // TODO this cleanup causes problems when we have more than one
            // MapWindow:
            // When a new window is added, findAndUndo is called for the already
            // present window.
            // this.aisHandler.removeListener(this);
        }
        super.findAndUndo(obj);
    }

    /**
     * Updates which AIS target this layer should display as the selected
     * target. If the provided {@code mmsi} does not have a graphical
     * representation in this layer, this method will remove the current
     * selection. For a selection to be valid, the graphic matching the MMSI
     * must either be a sub class of {@link VesselGraphicComponent} or an
     * implementation of {@link ISelectableGraphic}.
     * 
     * @param mmsi
     *            The MMSI of an AIS target that is now the selected AIS target.
     * @param repaintImmediately
     *            If this layer should repaint itself immediately.
     */
    public final void setSelectedTarget(long mmsi, boolean repaintImmediately) {
        TargetGraphic tg = this.targets.get(mmsi);
        if(tg instanceof VesselGraphicComponent) {
            VesselGraphicComponent vgc = (VesselGraphicComponent) tg;
            this.setSelectedGraphic(vgc.getVesselGraphic(), repaintImmediately);
        } else if(tg instanceof ISelectableGraphic) {
            this.setSelectedGraphic((ISelectableGraphic) tg, repaintImmediately);
        } else {
            this.setSelectedGraphic(null, repaintImmediately);
        }
    }

    /**
     * Mark a graphic as selected. Repaint this layer if requested.
     * 
     * @param newSelection
     *            The graphic that is now the selected graphic.
     * @param repaint
     *            If this layer should repaint itself to reflect the change in
     *            selection.
     */
    protected void setSelectedGraphic(ISelectableGraphic newSelection,
            boolean repaint) {
        if (this.selectedGraphic != null) {
            // remove current selection
            this.selectedGraphic.setSelectionStatus(false);
        }
        if (newSelection != null) {
            // mark new selection
            newSelection.setSelectionStatus(true);
        }
        // keep reference to new selection
        this.selectedGraphic = newSelection;
        if (repaint) {
            this.doPrepare();
        }
    }

    /**
     * Add a graphical representation of an AIS target to this layers set of
     * graphics.
     * 
     * @param mmsi
     *            The MMSI of the AIS target that {@code tg} represents.
     * @param tg
     *            Object that manages the graphical representation of the given
     *            {@code mmsi}.
     */
    protected void addTargetGraphic(Long mmsi, TargetGraphic tg) {
        if (tg == null || mmsi == null) {
            return;
        }
        // Add the new graphic to the map of ais targets
        TargetGraphic oldVal = this.targets.put(mmsi, tg);
        synchronized (this.graphics) {
            if (oldVal != null) {
                // remove the old graphical representation of this MMSI
                this.graphics.remove(oldVal);
            }
            // add the new graphical representation of this MMSI to the list of
            // graphics to be displayed.
            this.graphics.add(tg);
        }
    }

    /**
     * Remove a graphical representation of an AIS target from this layers set
     * of graphics.
     * 
     * @param mmsi
     *            The MMSI of the AIS target that should have its graphical
     *            representation removed.
     */
    protected void removeTargetGraphic(Long mmsi) {
        TargetGraphic deleted = this.targets.remove(mmsi);
        if (deleted != null) {
            // Successfully removed a graphic object from map
            // This graphic object should also be present in the list of
            // graphics to be displayed, hence we need to remove it from the
            // graphics list.
            synchronized (this.graphics) {
                this.graphics.remove(deleted);
            }
        }
    }

    /**
     * Get the graphical representation of an AIS target with the given MMSI.
     * 
     * @param mmsi
     *            The MMSI to find the graphical representation for.
     * @return The graphical representation of the MMSI or null if there is
     *         currently no graphical representation for this MMSI.
     */
    protected TargetGraphic getTargetGraphic(Long mmsi) {
        return mmsi == null ? null : this.targets.get(mmsi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void targetUpdated(AisTarget aisTarget) {
        // Sanity check
        if (aisTarget == null) {
            return;
        }
        long mmsi = aisTarget.getMmsi();
        TargetGraphic targetGraphic = this.getTargetGraphic(mmsi);
        float mapScale = (this.getProjection() == null) ? 0 : this
                .getProjection().getScale();

        if (aisTarget.isGone()) {
            if (targetGraphic != null) {
                // Remove target from map of graphics + graphics list
                this.removeTargetGraphic(mmsi);
            }
            return;
        }

        // Create and insert
        if (targetGraphic == null) {
            if (aisTarget instanceof VesselTarget) {
                targetGraphic = new VesselGraphicComponentSelector(this.getSettings().isShowAllAisNameLabels());
            } else if (aisTarget instanceof SarTarget) {
                targetGraphic = new SarTargetGraphic();
            } else if (aisTarget instanceof AtoNTarget) {
                targetGraphic = new AtonTargetGraphic();
            } else {
                LOG.error("Unknown target type");
                return;
            }
            // add to map of graphics + graphics list
            this.addTargetGraphic(mmsi, targetGraphic);
        }

        // Send the new location data to the graphic representing the AisTarget
        targetGraphic.update(aisTarget, mapScale);
        targetGraphic.project(getProjection());
    }
    
    /**
     * Renders the graphics displayed by this {@link AisLayerCommon}. Sub classes should invoke super implementation in order to allow it to handle changes to display of AIS target selection.
     */
    @Override
    public synchronized OMGraphicList prepare() {
        synchronized (graphics) {
            graphics.project(getProjection());
        }
        // Was a vessel selected?
        if(this.selectedGraphic instanceof VesselGraphic) {
            VesselGraphic vg = (VesselGraphic) this.selectedGraphic;
            // Find the wrapper graphic that controls different displays modes for the selected vessel
            TargetGraphic tg = this.getTargetGraphic(vg.getMostRecentVesselTarget().getMmsi());
            if(tg instanceof VesselGraphicComponentSelector) {
                VesselGraphicComponentSelector vgcs = (VesselGraphicComponentSelector) tg;
                // Update selectedGraphic to be the new display mode of the VesselGraphicComponentSelector
                VesselGraphic newSelection = vgcs.getVesselGraphic();
                // Do not repaint immediately to avoid infinite recursive calls.
                this.setSelectedGraphic(newSelection, false);
                // Project only the vessel graphic such that it will have its selection visualization properly displayed
                newSelection.project(getProjection());
            }
        }
        return graphics;
    }
    
    /**
     * Updates target selection if the {@code clickedGraphics} is an {@code ISelectableGraphic} or null.
     */
    @Override
    protected void handleMouseClick(OMGraphic clickedGraphics, MouseEvent evt) {
        // Should only handle left clicks.
        assert evt.getButton() == MouseEvent.BUTTON1;
        if(clickedGraphics instanceof ISelectableGraphic) {
            // Update selected graphic and do a repaint
            this.setSelectedGraphic((ISelectableGraphic) clickedGraphics, true);
        }
        else if(clickedGraphics == null) {
            // User clicked somewhere on the map with no nearby graphics
            // We need to remove the current selection and repaint
            this.setSelectedGraphic(null, true);
        }
    }
    
    /**
     * Force this AIS layer to update itself.
     */
    public abstract void forceLayerUpdate();
    
    /**
     * Gets the {@link AisLayerCommonSettings} that controls the appearance of this {@link AisLayerCommon}.
     * @return The {@link AisLayerCommonSettings} that controls the appearance of this {@link AisLayerCommon}.
     */
    @Override
    public AisLayerCommonSettings<?> getSettings() {
        return (AisLayerCommonSettings<?>) super.getSettings();
    }
    
    /*
     * Begin settings observer methods.
     */
    
    /**
     * Invoked when the layer visibility is toggled on/off on the
     * {@link AisLayerCommonSettings} instance observed by this
     * layer.
     */
    @Override
    public void isVisibleChanged(boolean newValue) {
        this.setVisible(newValue);
    }
    
    /**
     * Invoked when the layer redraw interval is changed on the
     * {@link AisLayerCommonSettings} instance observed by this
     * layer.
     */
    @Override
    public void layerRedrawIntervalChanged(int newValue) {
        this.setRepaintInterval(newValue);
    }
    
    /**
     * Invoked when the AIS name labels are toggled on/off on the
     * {@link AisLayerCommonSettings} instance observed by this
     * layer.
     */
    @Override
    public void showAllAisNameLabelsChanged(boolean newValue) {
        for(TargetGraphic tg : this.targets.values()) {
            if(tg instanceof VesselGraphicComponentSelector) {
                ((VesselGraphicComponentSelector)tg).setShowNameLabel(newValue);
            }
        }
        // do a repaint
        this.doPrepare();
    }
    
    /**
     * Invoked when the minimum length of the movement vector is changed
     * on the {@link AisLayerCommonSettings} instance observed by this layer.
     */
    @Override
    public void movementVectorLengthMinChanged(int newMinLengthMinutes) {
       /*
        * We need to repaint in order to visually reflect new length of the movement vector. 
        */
        this.doPrepare();
    }
    
    /**
     * Invoked when the maximum length of the movement vector is
     * changed on the {@link AisLayerCommonSettings} instance observed
     * by this layer.
     */
    @Override
    public void movementVectorLengthMaxChanged(int newMaxLengthMinutes) {
        /*
         * We need to repaint in order to visually reflect new length of the movement vector. 
         */
         this.doPrepare();
    }
    
    /**
     * Invoked when the setting specifying the scale difference between two
     * successive length values for the movement vector is changed on the
     * {@link AisLayerCommonSettings} instance observed by this layer.
     */
    @Override
    public void movementVectorLengthStepSizeChanged(float newStepSize) {
        /*
         * We need to repaint in order to visually reflect new length of the movement vector. 
         */
         this.doPrepare();
    }
    
    /**
     * Invoked when the setting specifying the minimum speed a vessel must
     * travel with for its movement vector to be displayed is changed on the
     * {@link AisLayerCommonSettings} instance observed by this layer.
     */
    @Override
    public void movementVectorHideBelowChanged(float newMinSpeed) {
        /*
         * Repaint to visually reflect the change (hide/show individual speed vectors). 
         */
         this.doPrepare();
    }
    
    @Override
    public void showAllPastTracksChanged(boolean newValue) {
        /*
         * Repaint to visually reflect the past track toggle.
         */
        this.doPrepare();
    }
    
    /*
     * End settings observer methods.
     */
}
