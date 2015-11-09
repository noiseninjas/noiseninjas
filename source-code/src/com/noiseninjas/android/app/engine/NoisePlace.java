/**
 * 
 */
package com.noiseninjas.android.app.engine;

import com.google.android.gms.maps.model.LatLng;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vishal gaurav
 *
 */
public class NoisePlace implements Parcelable {

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
    
    private NoisePlace(Parcel parcel){
        placeId = parcel.readString();
        name = parcel.readString();
        location = (LatLng) parcel.readValue(LatLng.class.getClassLoader());
        placeType = parcel.readString();
        intensity = (PlaceIntesity) PlaceIntesity.getIntensityFromLevel(parcel.readInt());
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName() + " " + getPlaceType();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(name);
        dest.writeValue(location);
        dest.writeString(placeType);
        dest.writeInt(intensity.getLevel());
        
    }

    public static final Parcelable.Creator<NoisePlace> CREATOR = new Parcelable.Creator<NoisePlace>() {

        @Override
        public NoisePlace createFromParcel(Parcel source) {
           
            return new NoisePlace(source);
        }

        @Override
        public NoisePlace[] newArray(int size) {
            return new NoisePlace[size];
        } 
        
        
    };
}
