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
package dk.dma.epd.common.prototype.settings;

import java.io.Serializable;
import java.util.Properties;

import com.bbn.openmap.util.PropUtils;

/**
 * Navigational settings
 */
public class NavSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String PREFIX = "nav.";
        
    private boolean autoFollow;
    private int autoFollowPctOffTollerance = 10;
    private boolean lookAhead;
    private double cogVectorLength = 6; // minutes
    private float showArrowScale = 450000;
    private int showMinuteMarksSelf = 200;
    private double defaultSpeed = 10.0;
    private double defaultTurnRad = 0.5;
    private double defaultXtd = 0.1;
    private double minWpRadius = 0.2;
    private boolean relaxedWpChange = true;
    private float routeWidth = 2.0f;
    private boolean dynamicPrediction;
    private boolean showXtd;
    
    public NavSettings() {
    }

    public void readProperties(Properties props) {
        autoFollow = PropUtils.booleanFromProperties(props, PREFIX + "autoFollow", autoFollow);
        autoFollowPctOffTollerance = PropUtils.intFromProperties(props, PREFIX + "autoFollowPctOffTollerance", autoFollowPctOffTollerance);
        lookAhead = PropUtils.booleanFromProperties(props, PREFIX + "lookAhead", lookAhead);
        cogVectorLength = PropUtils.doubleFromProperties(props, PREFIX + "cogVectorLength", cogVectorLength);
        showArrowScale = PropUtils.floatFromProperties(props, PREFIX + "showArrowScale", showArrowScale);
        showMinuteMarksSelf = PropUtils.intFromProperties(props, PREFIX + "showMinuteMarksSelf", showMinuteMarksSelf);
        defaultSpeed = PropUtils.doubleFromProperties(props, PREFIX + "defaultSpeed", defaultSpeed);
        defaultTurnRad = PropUtils.doubleFromProperties(props, PREFIX + "defaultTurnRad", defaultTurnRad);
        defaultXtd = PropUtils.doubleFromProperties(props, PREFIX + "defaultXtd", defaultXtd);
        minWpRadius = PropUtils.doubleFromProperties(props, PREFIX + "minWpRadius", minWpRadius);
        relaxedWpChange = PropUtils.booleanFromProperties(props, PREFIX + "relaxedWpChange", relaxedWpChange);
        routeWidth = (float)PropUtils.doubleFromProperties(props, PREFIX + "routeWidth", routeWidth);
        dynamicPrediction = PropUtils.booleanFromProperties(props, PREFIX + "dynamicPrediction", dynamicPrediction);
        showXtd = PropUtils.booleanFromProperties(props, PREFIX + "showXtd", showXtd);
    }
    
    public void setProperties(Properties props) {
        props.put(PREFIX + "autoFollow", Boolean.toString(autoFollow));
        props.put(PREFIX + "autoFollowPctOffTollerance", Integer.toString(autoFollowPctOffTollerance));
        props.put(PREFIX + "lookAhead", Boolean.toString(lookAhead));
        props.put(PREFIX + "cogVectorLength", Double.toString(cogVectorLength));
        props.put(PREFIX + "showArrowScale", Float.toString(showArrowScale));
        props.put(PREFIX + "showMinuteMarksSelf", Float.toString(showMinuteMarksSelf));
        props.put(PREFIX + "defaultSpeed", Double.toString(defaultSpeed));
        props.put(PREFIX + "defaultTurnRad", Double.toString(defaultTurnRad));
        props.put(PREFIX + "defaultXtd", Double.toString(defaultXtd));
        props.put(PREFIX + "minWpRadius", Double.toString(minWpRadius));
        props.put(PREFIX + "relaxedWpChange", Boolean.toString(relaxedWpChange));
        props.put(PREFIX + "routeWidth", Float.toString(routeWidth));
        props.put(PREFIX + "dynamicPrediction", Boolean.toString(dynamicPrediction));
        props.put(PREFIX + "showXtd", Boolean.toString(showXtd));        
    }
    
    public boolean isAutoFollow() {
        return autoFollow;
    }
    
    public void setAutoFollow(boolean autoFollow) {
        this.autoFollow = autoFollow;
    }
    
    public double getCogVectorLength() {
        return cogVectorLength;
    }
    
    public void setCogVectorLength(double cogVectorLength) {
        this.cogVectorLength = cogVectorLength;
    }
    
    public boolean isLookAhead() {
        return lookAhead;
    }
    
    public void setLookAhead(boolean lookAhead) {
        this.lookAhead = lookAhead;
    }
    
    public int getAutoFollowPctOffTollerance() {
        return autoFollowPctOffTollerance;
    }
    
    public void setAutoFollowPctOffTollerance(int autoFollowPctOffTollerance) {
        this.autoFollowPctOffTollerance = autoFollowPctOffTollerance;
    }
    
    public float getShowArrowScale() {
        return showArrowScale;
    }
    
    public void setShowArrowScale(float showArrowScale) {
        this.showArrowScale = showArrowScale;
    }

    public int getShowMinuteMarksSelf() {
        return showMinuteMarksSelf;
    }

    public void setShowMinuteMarksSelf(int showMinuteMarksSelf) {
        this.showMinuteMarksSelf = showMinuteMarksSelf;
    }

    public double getDefaultSpeed() {
        return defaultSpeed;
    }

    public void setDefaultSpeed(double defaultSpeed) {
        this.defaultSpeed = defaultSpeed;
    }

    public double getDefaultTurnRad() {
        return defaultTurnRad;
    }

    public void setDefaultTurnRad(double defaultTurnRad) {
        this.defaultTurnRad = defaultTurnRad;
    }

    public double getDefaultXtd() {
        return defaultXtd;
    }

    public void setDefaultXtd(double defaultXtd) {
        this.defaultXtd = defaultXtd;
    }
    
    public double getMinWpRadius() {
        return minWpRadius;
    }
    
    public boolean isRelaxedWpChange() {
        return relaxedWpChange;
    }
    
    public void setRelaxedWpChange(boolean relaxedWpChange) {
        this.relaxedWpChange = relaxedWpChange;
    }
    
    public float getRouteWidth() {
        return routeWidth;
    }
    
    public void setRouteWidth(float routeWidth) {
        this.routeWidth = routeWidth;
    }

    public boolean isDynamicPrediction() {
        return dynamicPrediction;
    }

    public void setDynamicPrediction(boolean dynamicPrediction) {
        this.dynamicPrediction = dynamicPrediction;
    }
    
    public boolean isShowXtd() {
        return showXtd;
    }
    
    public void setShowXtd(boolean showXtd) {
        this.showXtd = showXtd;
    }
    
}
