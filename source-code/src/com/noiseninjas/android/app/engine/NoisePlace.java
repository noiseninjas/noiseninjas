/**
 * 
 */
package com.noiseninjas.android.app.engine;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author vishal gaurav
 *
 */
public class NoisePlace {

    private String placeId;
    private String name;
    private LatLng location;
    private String placeType;
    private PlaceIntesity intensity;

    public NoisePlace(String placeId, String name, LatLng location, String placeType, PlaceIntesity intensity) {
        super();
        this.placeId = placeId;
        this.name = name;
        this.location = location;
        this.placeType = placeType;
        this.intensity = intensity;
    }

    /**
     * @return the placeId
     */
    public String getPlaceId() {
        return placeId;
    }

    /**
     * @param placeId
     *            the placeId to set
     */
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the location
     */
    public LatLng getLocation() {
        return location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(LatLng location) {
        this.location = location;
    }

    /**
     * @return the placeType
     */
    public String getPlaceType() {
        return placeType;
    }

    /**
     * @param placeType
     *            the placeType to set
     */
    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    /**
     * @return the intensity
     */
    public PlaceIntesity getIntensity() {
        return intensity;
    }

    /**
     * @param intensity
     *            the intensity to set
     */
    public void setIntensity(PlaceIntesity intensity) {
        this.intensity = intensity;
    }

}
