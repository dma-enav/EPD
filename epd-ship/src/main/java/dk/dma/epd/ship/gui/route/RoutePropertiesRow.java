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
package dk.dma.epd.ship.gui.route;

import java.awt.Color;


public class RoutePropertiesRow {

    WaypointJTextField name;
    WaypointJTextField latitude;
    WaypointJTextField longitude;
    WaypointJTextField rad;
    WaypointJTextField rot;
    WaypointJTextField ttg;
    WaypointJTextField eta;
    WaypointJTextField rng;
    WaypointJTextField brg;
    WaypointJTextField heading;
    WaypointJTextField sog;
    WaypointJTextField xtds;
    WaypointJTextField xtdp;
    WaypointJTextField sfwidth;
    WaypointJTextField sflen;

    LockLabel lockLbl;

    int id;
    boolean last;
    boolean locked;
    int offset = 9;
    Color selectedColor = Color.yellow;
    Color notSelected = Color.white;

    Color selectNotEdit = new Color(255, 215, 0);
    Color notSelectNotEdit = new Color(240, 240, 240);

    public RoutePropertiesRow(WaypointJTextField nameTxT,
            WaypointJTextField latTxT, WaypointJTextField lonTxT,
            WaypointJTextField radTxT, WaypointJTextField rotTxT,
            WaypointJTextField ttgTxT, WaypointJTextField etaTxT,
            WaypointJTextField rngTxT, WaypointJTextField brgTxT,
            WaypointJTextField headingTxT, WaypointJTextField sogTxT,
            WaypointJTextField xtdsTxT, WaypointJTextField xtdPTxT,
            WaypointJTextField sfwTxT, WaypointJTextField sfhTxT, int id,
            boolean last, LockLabel lockLbl) {

        this.name = nameTxT;
        this.latitude = latTxT;
        this.longitude = lonTxT;
        this.rad = radTxT;
        this.rot = rotTxT;
        this.ttg = ttgTxT;
        this.eta = etaTxT;
        this.rng = rngTxT;
        this.brg = brgTxT;
        this.heading = headingTxT;
        this.sog = sogTxT;
        this.xtds = xtdsTxT;
        this.xtdp = xtdPTxT;
        this.sfwidth = sfwTxT;
        this.sflen = sfhTxT;
        this.id = id;
        this.last = last;
        this.lockLbl = lockLbl;
    }

    public void updateId() {
        int id = name.getId() - 1;
        name.setId(id);
        latitude.setId(id);
        longitude.setId(id);
        rad.setId(id);
        rot.setId(id);
        ttg.setId(id);
        eta.setId(id);
        rng.setId(id);
        brg.setId(id);
        heading.setId(id);
        sog.setId(id);
        xtds.setId(id);
        xtdp.setId(id);
        sfwidth.setId(id);
        sflen.setId(id);
    }

    public void setFirst() {
        rad.setEnabled(false);
        rot.setEditable(false);
    }

    public LockLabel getLockLbl() {
        return lockLbl;
    }

    public void moveRow(int y) {
        lockLbl.setLocation((int) lockLbl.getLocation().getX(), y);
        name.setLocation((int) name.getLocation().getX(), y);
        latitude.setLocation((int) latitude.getLocation().getX(), y);
        longitude.setLocation((int) longitude.getLocation().getX(), y);
        rad.setLocation((int) rad.getLocation().getX(), y);
        rot.setLocation((int) rot.getLocation().getX(), y);
        ttg.setLocation((int) ttg.getLocation().getX(), y);
        eta.setLocation((int) eta.getLocation().getX(), y);
        rng.setLocation((int) rng.getLocation().getX(), y + offset);
        brg.setLocation((int) brg.getLocation().getX(), y + offset);
        heading.setLocation((int) heading.getLocation().getX(), y + offset);
        sog.setLocation((int) sog.getLocation().getX(), y + offset);
        xtds.setLocation((int) xtds.getLocation().getX(), y + offset);
        xtdp.setLocation((int) xtdp.getLocation().getX(), y + offset);
        sfwidth.setLocation((int) sfwidth.getLocation().getX(), y + offset);
        sflen.setLocation((int) sflen.getLocation().getX(), y + offset);

    }

    public WaypointJTextField getName() {
        return name;
    }

    public WaypointJTextField getLongitude() {
        return longitude;
    }

    public WaypointJTextField getLatitude() {
        return latitude;
    }

    public WaypointJTextField getRad() {
        return rad;
    }

    public WaypointJTextField getRot() {
        return rot;
    }

    public WaypointJTextField getTtg() {
        return ttg;
    }

    public WaypointJTextField getEta() {
        return eta;
    }

    public WaypointJTextField getRng() {
        return rng;
    }

    public WaypointJTextField getBrg() {
        return brg;
    }

    public WaypointJTextField getHeading() {
        return heading;
    }

    public WaypointJTextField getSog() {
        return sog;
    }

    public WaypointJTextField getXtds() {
        return xtds;
    }

    public WaypointJTextField getXtdp() {
        return xtdp;
    }

    public WaypointJTextField getSfwidth() {
        return sfwidth;
    }

    public WaypointJTextField getSflen() {
        return sflen;
    }

    public void setSelected(boolean activeRoute) {

        if (activeRoute) {
            name.setBackground(selectNotEdit);
            latitude.setBackground(selectNotEdit);
            longitude.setBackground(selectNotEdit);
            rad.setBackground(selectNotEdit);
            rot.setBackground(selectNotEdit);
            ttg.setBackground(selectNotEdit);
            eta.setBackground(selectNotEdit);
            rng.setBackground(selectNotEdit);
            brg.setBackground(selectNotEdit);
            heading.setBackground(selectNotEdit);
            sog.setBackground(selectNotEdit);
            xtds.setBackground(selectNotEdit);
            xtdp.setBackground(selectNotEdit);
            sfwidth.setBackground(selectNotEdit);
            sflen.setBackground(selectNotEdit);

        } else {

            name.setBackground(selectedColor);
            latitude.setBackground(selectedColor);
            longitude.setBackground(selectedColor);

            if (id == 0) {
                rad.setBackground(selectNotEdit);
            } else {
                rad.setBackground(selectedColor);
            }

            rot.setBackground(selectNotEdit);
            ttg.setBackground(selectNotEdit);
            eta.setBackground(selectNotEdit);
            rng.setBackground(selectNotEdit);
            brg.setBackground(selectNotEdit);

            if (last) {
                heading.setBackground(selectNotEdit);
                sog.setBackground(selectNotEdit);
                xtds.setBackground(selectNotEdit);
                xtdp.setBackground(selectNotEdit);
                sfwidth.setBackground(selectNotEdit);
                sflen.setBackground(selectNotEdit);
            } else {

                heading.setBackground(selectedColor);
                sog.setBackground(selectedColor);
                xtds.setBackground(selectedColor);
                xtdp.setBackground(selectedColor);
                sfwidth.setBackground(selectedColor);
                sflen.setBackground(selectedColor);
            }
        }
    }

    public void deSelect(boolean activeRoute) {
        if (activeRoute) {
            name.setBackground(notSelectNotEdit);
            latitude.setBackground(notSelectNotEdit);
            longitude.setBackground(notSelectNotEdit);
            rad.setBackground(notSelectNotEdit);
            rot.setBackground(notSelectNotEdit);
            ttg.setBackground(notSelectNotEdit);
            eta.setBackground(notSelectNotEdit);
            rng.setBackground(notSelectNotEdit);
            brg.setBackground(notSelectNotEdit);
            heading.setBackground(notSelectNotEdit);
            sog.setBackground(notSelectNotEdit);
            xtds.setBackground(notSelectNotEdit);
            xtdp.setBackground(notSelectNotEdit);
            sfwidth.setBackground(notSelectNotEdit);
            sflen.setBackground(notSelectNotEdit);
        } else {

            name.setBackground(notSelected);
            latitude.setBackground(notSelected);
            longitude.setBackground(notSelected);

            if (id == 0) {
                rad.setBackground(notSelectNotEdit);
            } else {
                rad.setBackground(notSelected);
            }

            rot.setBackground(notSelectNotEdit);
            ttg.setBackground(notSelectNotEdit);
            eta.setBackground(notSelectNotEdit);
            rng.setBackground(notSelectNotEdit);
            brg.setBackground(notSelectNotEdit);

            if (last) {
                heading.setBackground(notSelectNotEdit);
                sog.setBackground(notSelectNotEdit);
                xtds.setBackground(notSelectNotEdit);
                xtdp.setBackground(notSelectNotEdit);
                sfwidth.setBackground(notSelectNotEdit);
                sflen.setBackground(notSelectNotEdit);
            } else {
                heading.setBackground(notSelected);
                sog.setBackground(notSelected);
                xtds.setBackground(notSelected);
                xtdp.setBackground(notSelected);
                sfwidth.setBackground(notSelected);
                sflen.setBackground(notSelected);
            }

        }
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
        name.setEnabled(false);
        latitude.setEnabled(false);
        longitude.setEnabled(false);
        rad.setEnabled(false);
        rot.setEnabled(false);
        ttg.setEnabled(false);
        eta.setEnabled(false);
        rng.setEnabled(false);
        brg.setEnabled(false);
        heading.setEnabled(false);
        sog.setEnabled(false);
        xtds.setEnabled(false);
        xtdp.setEnabled(false);
        sfwidth.setEnabled(false);
        sflen.setEnabled(false);
    }

    public void unlock() {
        locked = false;
        name.setEnabled(true);
        latitude.setEnabled(true);
        longitude.setEnabled(true);
        rad.setEnabled(true);
        rot.setEnabled(true);
        ttg.setEnabled(true);
        eta.setEnabled(true);
        rng.setEnabled(true);
        brg.setEnabled(true);
        heading.setEnabled(true);
        sog.setEnabled(true);
        xtds.setEnabled(true);
        xtdp.setEnabled(true);
        sfwidth.setEnabled(true);
        sflen.setEnabled(true);
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;

        rng.setVisible(false);
        brg.setVisible(false);
        heading.setVisible(false);
        sog.setVisible(false);
        xtds.setVisible(false);
        xtdp.setVisible(false);
        sflen.setVisible(false);
        sfwidth.setVisible(false);
    }

}
