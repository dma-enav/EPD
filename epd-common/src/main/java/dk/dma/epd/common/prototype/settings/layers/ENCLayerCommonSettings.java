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
package dk.dma.epd.common.prototype.settings.layers;

import java.util.Properties;

import com.bbn.openmap.util.PropUtils;

/**
 * This class is used to maintain settings for an ENC/S52 layer.
 * 
 * @author Janus Varmarken
 */
public class ENCLayerCommonSettings<OBSERVER extends IENCLayerCommonSettingsObserver>
        extends LayerSettings<OBSERVER> {

    /**
     * Key used in the the properties file to store the value of
     * {@link #encInUse}.
     */
    private static final String KEY_ENC_IN_USE = "useENC";

    /**
     * Key used in the properties file to store the value of
     * {@link #s52ShowText}.
     */
    private static final String KEY_S52_SHOW_TEXT = "s52ShowText";

    /**
     * Key used in the properties file to store the value of
     * {@link #s52ShallowPattern}.
     */
    private static final String KEY_S52_SHALLOW_PATTERN = "s52ShallowPattern";

    /**
     * Key used in the properties file to store the value of
     * {@link #s52ShallowContour}.
     */
    private static final String KEY_S52_SHALLOW_CONTOUR = "s52ShallowContour";

    /**
     * Key used in the properties file to store the value of
     * {@link #s52SafetyDepth}.
     */
    private static final String KEY_S52_SAFETY_DEPTH = "s52SafetyDepth";

    /**
     * Key used in the properties file to store the value of
     * {@link #s52SafetyContour}.
     */
    private static final String KEY_S52_SAFETY_CONTOUR = "s52SafetyContour";

    /**
     * Key used in the properties file to store the value of
     * {@link #s52DeepContour}.
     */
    private static final String KEY_S52_DEEP_CONTOUR = "s52DeepContour";

    /**
     * Key used in the properties file to store the value of
     * {@link #useSimplePointSymbols}.
     */
    private static final String KEY_USE_SIMPLE_POINT_SYMBOLS = "useSimplePointSymbols";

    /**
     * Key used in the properties file to store the value of
     * {@link #usePlainAreas}.
     */
    private static final String KEY_USE_PLAIN_AREAS = "usePlainAreas";

    /**
     * Key used in the properties file to store the value of
     * {@link #s52TwoShades}.
     */
    private static final String KEY_S52_TWO_SHADES = "s52TwoShades";

    /**
     * Key used in the properties file to store the value of
     * {@link #encColorScheme}.
     */
    private static final String KEY_ENC_COLOR_SCHEME = "encColorScheme";

    /**
     * Setting specifying if the ENC layer should be loaded on application
     * launch.
     */
    private boolean encInUse = true;

    /**
     * Setting specifying if the S52/ENC layer should show text.
     */
    private boolean s52ShowText;

    /**
     * Setting specifying if the S52/ENC layer should use shallow pattern.
     */
    private boolean s52ShallowPattern;

    /**
     * Setting specifying the shallow contour of the S52/ENC layer.
     */
    private int s52ShallowContour = 6;

    /**
     * Setting specifying the safety depth of the S52/ENC layer. TODO: Verify
     * that this is in meters..?
     */
    private int s52SafetyDepth = 8;

    /**
     * Setting specifying the safety contour of the S52/ENC layer.
     */
    private int s52SafetyContour = 8;

    /**
     * Setting specifying the deep contour of the S52/ENC layer.
     */
    private int s52DeepContour = 10;

    /**
     * Setting specifying if the S52/ENC layer should use simple point symbols.
     */
    private boolean useSimplePointSymbols = true;

    /**
     * Setting specifying if the S52/ENC layer should use plain areas.
     */
    private boolean usePlainAreas;

    /**
     * Setting specifying if the S52/ENC layer should use two shades.
     */
    private boolean s52TwoShades;

    /**
     * Setting specifying the color scheme of the S52/ENC layer.
     */
    private ENCColorScheme encColorScheme = ENCColorScheme.DAY;

    /**
     * Get if ENC is in use (i.e. if it should be loaded on application launch).
     * 
     * @return {@code true} if ENC is in use (i.e. ENC should be loaded on
     *         application launch), {@code false} if ENC is not in use (i.e. ENC
     *         should not be loaded on application launch).
     */
    public boolean isEncInUse() {
        try {
            this.settingLock.readLock().lock();
            return this.encInUse;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if ENC is in use (i.e. if ENC should
     * be loaded on application launch).
     * 
     * @param useEnc
     *            {@code true} to enable ENC (i.e. ENC should be loaded on
     *            application launch), {@code false} to disable ENC (i.e. ENC
     *            should not be loaded on application launch).
     */
    public void setEncInUse(final boolean useEnc) {
        try {
            this.settingLock.writeLock().lock();
            if (this.encInUse == useEnc) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.encInUse = useEnc;
            for (OBSERVER obs : this.observers) {
                obs.isEncInUseChanged(useEnc);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies if the S52/ENC layer should show text.
     * 
     * @return {@code true} if the S52/ENC layer should show text, {@code false}
     *         if it shouldn't.
     */
    public boolean isS52ShowText() {
        try {
            this.settingLock.readLock().lock();
            return this.s52ShowText;
        } finally {
            this.settingLock.readLock().unlock();
        }

    }

    /**
     * Changes the setting that specifies if the S52/ENC layer should show text.
     * 
     * @param s52ShowText
     *            {@code true} if the S52/ENC layer should show text,
     *            {@code false} if it shouldn't.
     */
    public void setS52ShowText(final boolean s52ShowText) {
        try {
            this.settingLock.writeLock().lock();
            if (this.s52ShowText == s52ShowText) {
                // No change, no need to notify observers.
                return;
            }
            // Change found, update and notify.
            this.s52ShowText = s52ShowText;
            for (OBSERVER obs : this.observers) {
                obs.isS52ShowTextChanged(s52ShowText);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies if the S52/ENC layer should use shallow
     * pattern.
     * 
     * @return {@code true} if the S52/ENC layer should use shallow pattern,
     *         {@code false} if it shouldn't.
     */
    public boolean isS52ShallowPattern() {
        try {
            this.settingLock.readLock().lock();
            return this.s52ShallowPattern;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if the S52/ENC layer should use
     * shallow pattern.
     * 
     * @param useShallowPattern
     *            {@code true} if the S52/ENC layer should use shallow pattern,
     *            {@code false} if it shouldn't.
     */
    public void setS52ShallowPattern(final boolean useShallowPattern) {
        try {
            this.settingLock.writeLock().lock();
            if (this.s52ShallowPattern == useShallowPattern) {
                // No change, no need to notify observers.
                return;
            }
            this.s52ShallowPattern = useShallowPattern;
            for (OBSERVER obs : this.observers) {
                obs.isS52ShallowPatternChanged(useShallowPattern);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies the shallow contour of the S52/ENC layer.
     * 
     * @return The setting that specifies the shallow contour of the S52/ENC
     *         layer.
     */
    public int getS52ShallowContour() {
        try {
            this.settingLock.readLock().lock();
            return this.s52ShallowContour;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the shallow contour of the S52/ENC
     * layer.
     * 
     * @param s52ShallowContour
     *            The new shallow contour of the S52/ENC layer.
     */
    public void setS52ShallowContour(final int s52ShallowContour) {
        try {
            this.settingLock.writeLock().lock();
            if (this.s52ShallowContour == s52ShallowContour) {
                // No change, no need to notify observers.
                return;
            }
            this.s52ShallowContour = s52ShallowContour;
            for (OBSERVER obs : this.observers) {
                obs.s52ShallowContourChanged(s52ShallowContour);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies the safety depth of the S52/ENC layer.
     * 
     * @return The safety depth of the S52/ENC layer.
     */
    public int getS52SafetyDepth() {
        try {
            this.settingLock.readLock().lock();
            return this.s52SafetyDepth;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the safety depth of the S52/ENC layer.
     * 
     * @param s52SafetyDepth
     *            The new safety depth of the S52/ENC layer.
     */
    public void setS52SafetyDepth(final int s52SafetyDepth) {
        try {
            this.settingLock.writeLock().lock();
            if (this.s52SafetyDepth == s52SafetyDepth) {
                // No change, no need to notify observers.
                return;
            }
            this.s52SafetyDepth = s52SafetyDepth;
            for (OBSERVER obs : this.observers) {
                obs.s52SafetyDepthChanged(s52SafetyDepth);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies the safety contour of the S52/ENC layer.
     * 
     * @return The safety contour of the S52/ENC layer.
     */
    public int getS52SafetyContour() {
        try {
            this.settingLock.readLock().lock();
            return this.s52SafetyContour;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the safety contour of the S52/ENC
     * layer.
     * 
     * @param s52SafetyContour
     *            The new safety contour of the S52/ENC layer.
     */
    public void setS52SafetyContour(final int s52SafetyContour) {
        try {
            this.settingLock.writeLock().lock();
            if (this.s52SafetyContour == s52SafetyContour) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.s52SafetyContour = s52SafetyContour;
            for (OBSERVER obs : this.observers) {
                obs.s52SafetyContourChanged(s52SafetyContour);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies the deep contour of the S52/ENC layer.
     * 
     * @return The deep contour of the S52/ENC layer.
     */
    public int getS52DeepContour() {
        try {
            this.settingLock.readLock().lock();
            return this.s52DeepContour;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the deep contour of the S52/ENC layer.
     * 
     * @param s52DeepContour
     *            The new deep contour of the S52/ENC layer.
     */
    public void setS52DeepContour(final int s52DeepContour) {
        try {
            this.settingLock.writeLock().lock();
            if (this.s52DeepContour == s52DeepContour) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.s52DeepContour = s52DeepContour;
            for (OBSERVER obs : this.observers) {
                obs.s52DeepContourChanged(s52DeepContour);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies if the S52/ENC layer should use simple
     * point symbols.
     * 
     * @return {@code true} if the S52/ENC layer should use simple point
     *         symbols, {@code false} if it shouldn't.
     */
    public boolean isUseSimplePointSymbols() {
        try {
            this.settingLock.readLock().lock();
            return this.useSimplePointSymbols;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if the S52/ENC layer should use simple
     * point symbols.
     * 
     * @param useSimplePointSymbols
     *            {@code true} if the S52/ENC layer should use simple point
     *            symbols, {@code false} if it shouldn't.
     */
    public void setUseSimplePointSymbols(final boolean useSimplePointSymbols) {
        try {
            this.settingLock.writeLock().lock();
            if (this.useSimplePointSymbols == useSimplePointSymbols) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.useSimplePointSymbols = useSimplePointSymbols;
            for (OBSERVER obs : this.observers) {
                obs.isUseSimplePointSymbolsChanged(useSimplePointSymbols);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies if the S52/ENC layer should use plain
     * areas.
     * 
     * @return {@code true} if the S52/ENC layer should use plain areas,
     *         {@code false} if it shouldn't.
     */
    public boolean isUsePlainAreas() {
        try {
            this.settingLock.readLock().lock();
            return this.usePlainAreas;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if the S52/ENC layer should use plain
     * areas.
     * 
     * @param usePlainAreas
     *            {@code true} if the S52/ENC layer should use plain areas,
     *            {@code false} if it shouldn't.
     */
    public void setUsePlainAreas(final boolean usePlainAreas) {
        try {
            this.settingLock.writeLock().lock();
            if (this.usePlainAreas == usePlainAreas) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.usePlainAreas = usePlainAreas;
            for (OBSERVER obs : this.observers) {
                obs.isUsePlainAreasChanged(usePlainAreas);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies if the S52/ENC layer should use two
     * shades.
     * 
     * @return {@code true} if the S52/ENC layer should use two shades,
     *         {@code false} otherwise.
     */
    public boolean isS52TwoShades() {
        try {
            this.settingLock.readLock().lock();
            return this.s52TwoShades;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if the S52/ENC layer should use two
     * shades.
     * 
     * @param s52TwoShades
     *            {@code true} if the S52/ENC layer should use two shades,
     *            {@code false} otherwise.
     */
    public void setS52TwoShades(final boolean s52TwoShades) {
        try {
            this.settingLock.writeLock().lock();
            if (this.s52TwoShades == s52TwoShades) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.s52TwoShades = s52TwoShades;
            for (OBSERVER obs : this.observers) {
                obs.isS52TwoShadesChanged(s52TwoShades);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies the ENC color scheme.
     * 
     * @return The ENC color scheme.
     */
    public ENCColorScheme getEncColorScheme() {
        try {
            this.settingLock.readLock().lock();
            return this.encColorScheme;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the ENC color scheme.
     * 
     * @param encColorScheme
     *            The ENC color scheme to replace the current scheme.
     */
    public void setEncColorScheme(final ENCColorScheme encColorScheme) {
        try {
            this.settingLock.writeLock().lock();
            if (this.encColorScheme == encColorScheme) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observer.
            this.encColorScheme = encColorScheme;
            for (OBSERVER obs : this.observers) {
                obs.encColorSchemeChanged(encColorScheme);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLoadSuccess(Properties settings) {
        this.settingLock.writeLock().lock();
        // Let super class initialize its variables.
        super.onLoadSuccess(settings);
        this.setEncInUse(PropUtils.booleanFromProperties(settings,
                KEY_ENC_IN_USE, this.encInUse));
        this.setS52ShowText(PropUtils.booleanFromProperties(settings,
                KEY_S52_SHOW_TEXT, this.s52ShowText));
        this.setS52ShallowPattern(PropUtils.booleanFromProperties(settings,
                KEY_S52_SHALLOW_PATTERN, this.s52ShallowPattern));
        this.setS52ShallowContour(PropUtils.intFromProperties(settings,
                KEY_S52_SHALLOW_CONTOUR, this.s52ShallowContour));
        this.setS52SafetyDepth(PropUtils.intFromProperties(settings,
                KEY_S52_SAFETY_DEPTH, this.s52SafetyDepth));
        this.setS52SafetyContour(PropUtils.intFromProperties(settings,
                KEY_S52_SAFETY_CONTOUR, this.s52SafetyContour));
        this.setS52DeepContour(PropUtils.intFromProperties(settings,
                KEY_S52_DEEP_CONTOUR, this.s52DeepContour));
        this.setUseSimplePointSymbols(PropUtils.booleanFromProperties(settings,
                KEY_USE_SIMPLE_POINT_SYMBOLS, this.useSimplePointSymbols));
        this.setUsePlainAreas(PropUtils.booleanFromProperties(settings,
                KEY_USE_PLAIN_AREAS, this.usePlainAreas));
        this.setS52TwoShades(PropUtils.booleanFromProperties(settings,
                KEY_S52_TWO_SHADES, this.s52TwoShades));
        // Read the ENC color scheme setting.
        String schemeStrVal = settings.getProperty(KEY_ENC_COLOR_SCHEME);
        ENCColorScheme scheme = ENCColorScheme.getFromString(schemeStrVal);
        this.setEncColorScheme(scheme != null ? scheme : this.encColorScheme);
        this.settingLock.writeLock().unlock();
    }

    @Override
    protected Properties onSaveSettings() {
        this.settingLock.readLock().lock();
        // Let super class store its variables.
        Properties toSave = super.onSaveSettings();
        // Add own variables.
        toSave.setProperty(KEY_ENC_IN_USE, Boolean.toString(this.encInUse));
        toSave.setProperty(KEY_S52_SHOW_TEXT,
                Boolean.toString(this.s52ShowText));
        toSave.setProperty(KEY_S52_SHALLOW_PATTERN,
                Boolean.toString(this.s52ShallowPattern));
        toSave.setProperty(KEY_S52_SHALLOW_CONTOUR,
                Integer.toString(this.s52ShallowContour));
        toSave.setProperty(KEY_S52_SAFETY_DEPTH,
                Integer.toString(this.s52SafetyDepth));
        toSave.setProperty(KEY_S52_SAFETY_CONTOUR,
                Integer.toString(this.s52SafetyContour));
        toSave.setProperty(KEY_S52_DEEP_CONTOUR,
                Integer.toString(this.s52DeepContour));
        toSave.setProperty(KEY_USE_SIMPLE_POINT_SYMBOLS,
                Boolean.toString(this.useSimplePointSymbols));
        toSave.setProperty(KEY_USE_PLAIN_AREAS,
                Boolean.toString(this.usePlainAreas));
        toSave.setProperty(KEY_S52_TWO_SHADES,
                Boolean.toString(this.s52TwoShades));
        toSave.setProperty(KEY_ENC_COLOR_SCHEME, this.encColorScheme.toString());
        this.settingLock.readLock().unlock();
        return toSave;
        // TODO 24-03-2014: check correctness of above (was written in a rush)
    }

    /**
     * Enum used for the color scheme setting of the S52/ENC layer.
     * 
     * @author Janus Varmarken
     * 
     */
    public static enum ENCColorScheme {
        DAY {
            @Override
            public String toString() {
                return "Day";
            }
        },
        DUSK {
            @Override
            public String toString() {
                return "Dusk";
            }
        },
        NIGHT {
            @Override
            public String toString() {
                return "Night";
            }
        };

        /**
         * Get the {@link ENCColorScheme} enum constant corresponding to a given
         * {@link String} identifier.
         * 
         * @param identifier
         *            The identifier.
         * @return The {@link ENCColorScheme} enum constant corresponding to
         *         {@code identifier} or {@code null} if no constant matches the
         *         identifier.
         */
        public static ENCColorScheme getFromString(String identifier) {
            for (ENCColorScheme mode : values()) {
                if (mode.toString().toLowerCase()
                        .equals(identifier.toLowerCase())) {
                    return mode;
                }
            }
            return null;
        }
    }
}
