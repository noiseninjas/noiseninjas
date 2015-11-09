/**
 * 
 */
package com.noiseninjas.android.app.engine;

/**
 * @author Vishal Gaurav <br/>
 *         constants to determine Place type
 */
public enum PlaceType {
    School(PlacesMap.TYPE_SCHOOL), Hospital(PlacesMap.TYPE_HOSPITAL), Zoo(PlacesMap.TYPE_SCHOOL);

    private final String name;

    private PlaceType(String placeName) {
        this.name = placeName;
    }

    @Override
    public String toString() {
        return name;
    }

}
