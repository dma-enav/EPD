package dk.dma.epd.common.prototype.enavcloud.intendedroute;

public class Leg {

    private Double speed;
    private Double xtdPort;
    private Double xtdStarboard;
    private HeadingType headingType;

    public Leg() {

    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getXtdPort() {
        return xtdPort;
    }

    public void setXtdPort(Double xtdPort) {
        this.xtdPort = xtdPort;
    }

    public Double getXtdStarboard() {
        return xtdStarboard;
    }

    public void setXtdStarboard(Double xtdStarboard) {
        this.xtdStarboard = xtdStarboard;
    }

    public HeadingType getHeadingType() {
        return headingType;
    }

    public void setHeadingType(HeadingType headingType) {
        this.headingType = headingType;
    }

}
