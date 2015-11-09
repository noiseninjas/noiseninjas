/**
 * 
 */
package com.noiseninjas.android.app.engine;

import android.os.Parcelable;

/**
 * @author Vishal Gaurav <br/>
 *         constants to be used in place filtering
 */
public enum PlaceIntesity {

    HIGH(3), MEDIUM(2), LOW(1), NORMAL(0), NONE(-1);

    private int level;

    private PlaceIntesity(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static PlaceIntesity getIntensityFromLevel(int level) {
        switch (level) {
            case -1:
                return PlaceIntesity.NONE;
            case 0:
                return PlaceIntesity.NONE;
            case 1:
                return PlaceIntesity.LOW;
            case 2:
                return PlaceIntesity.MEDIUM;
            case 3:
                return PlaceIntesity.HIGH;
            default:
                return PlaceIntesity.NONE;
        }
    }

}
