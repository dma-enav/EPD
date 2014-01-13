package dk.dma.epd.common.prototype.settings;

/**
 * Abstract parent class the encapsulates the 
 * list of specialized settings 
 */
public abstract class Settings {

    public abstract GuiSettings getGuiSettings();

    public abstract MapSettings getMapSettings();

    public abstract SensorSettings getSensorSettings();

    public abstract NavSettings getNavSettings();

    public abstract AisSettings getAisSettings();

    public abstract EnavSettings getEnavSettings();

    public abstract S57LayerSettings getS57Settings();
}
