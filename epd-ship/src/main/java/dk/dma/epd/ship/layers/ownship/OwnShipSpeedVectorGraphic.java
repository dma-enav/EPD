package dk.dma.epd.ship.layers.ownship;

import java.awt.Paint;

import com.bbn.openmap.omGraphics.OMGraphicConstants;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.layers.ais.SpeedVectorGraphic;

public class OwnShipSpeedVectorGraphic extends SpeedVectorGraphic {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RotationalPoly frontShipArrow;
	private RotationalPoly backShipArrow;

	public OwnShipSpeedVectorGraphic(Paint lineColour) {
		super(lineColour);
		// TODO Auto-generated constructor stub
	}
	
	protected void init() {
		super.init();
		
        int[] frontArrowX = {5,0,-5};
        int[] frontArrowY = {10,0,10};
        frontShipArrow = new RotationalPoly(frontArrowX, frontArrowY, stroke, null);
        int[] backArrowX = {5,0,-5};
        int[] backArrowY = {20,10,20};
        backShipArrow = new RotationalPoly(backArrowX, backArrowY, stroke, null);
        
        this.add(frontShipArrow);
        this.add(backShipArrow);
	}
	@Override
	public void update(VesselPositionData posData, float currentMapScale) {
		super.update(posData, currentMapScale);
		// add arrow heads
		
        Double cogRadian = Math.toRadians(posData.getCog());
//		Position pos = posData.getPos();
		this.frontShipArrow.setLocation(endPos.getLatitude(), endPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
		this.backShipArrow.setLocation(endPos.getLatitude(), endPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
	}
}
