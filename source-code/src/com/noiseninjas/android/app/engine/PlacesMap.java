/**
 * 
 */
package com.noiseninjas.android.app.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vishal gaurav
 *
 */
public final class PlacesMap {
    
    private volatile static Map<String, PlaceIntesity> PLACES_MAP = null;
    public static final String TYPE_HOSPITAL = "hospital" ;
    public static final String TYPE_DOCTOR = "doctor" ;
    public static final String TYPE_SCHOOL = "school" ;
    public static final String TYPE_UNIVERSITY = "university" ;
    public static final String TYPE_CEMETRY = "cemetry" ;
    public static final String TYPE_MOSQUE = "mosque" ;
    public static final String TYPE_TEMPLE = "hindu_temple" ;
    public static final String TYPE_ZOO = "zoo" ;
    public static final String TYPE_LIBRARY = "library" ;
    
    public static final String[] ALL_PLACES_TYPES = {TYPE_HOSPITAL, 
                                                     TYPE_DOCTOR, 
                                                     TYPE_SCHOOL,
                                                     TYPE_UNIVERSITY,
                                                     TYPE_CEMETRY,
                                                     TYPE_MOSQUE,
                                                     TYPE_TEMPLE,
                                                     TYPE_ZOO,
                                                     TYPE_LIBRARY};
 
    public static final String QUERY_TOKEN_CHAR = "|";

   public static String getAllPlacesTypeString(){
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < ALL_PLACES_TYPES.length; i++) {
            queryBuilder.append(ALL_PLACES_TYPES[i]);
            queryBuilder.append((i == (ALL_PLACES_TYPES.length - 1 ) ?"" : QUERY_TOKEN_CHAR ));
        }
        return queryBuilder.toString();
    }
    
    /**
     * private constructor to establish a singleton pattern
     */
    private PlacesMap(){
        
    }
    private static void initializeMapIfNecessary(){
        if(PLACES_MAP != null){
            synchronized (PlacesMap.class) {
                PLACES_MAP = new ConcurrentHashMap<String, PlaceIntesity>();
                addFiltersToMap();
            }
        }
    }
    
    private static void addFiltersToMap() {
       PLACES_MAP.put(TYPE_HOSPITAL, PlaceIntesity.HIGH);
       PLACES_MAP.put(TYPE_DOCTOR, PlaceIntesity.HIGH);
       PLACES_MAP.put(TYPE_SCHOOL, PlaceIntesity.HIGH);
       PLACES_MAP.put(TYPE_UNIVERSITY, PlaceIntesity.HIGH);
       PLACES_MAP.put(TYPE_MOSQUE, PlaceIntesity.LOW);
       PLACES_MAP.put(TYPE_TEMPLE, PlaceIntesity.LOW);
       PLACES_MAP.put(TYPE_ZOO, PlaceIntesity.MEDIUM);
       PLACES_MAP.put(TYPE_LIBRARY, PlaceIntesity.HIGH);
    }
    public static PlaceIntesity getPlaceType(String placeString){
        PlaceIntesity placeType = PlaceIntesity.NONE ;
        initializeMapIfNecessary();
        if(PLACES_MAP.containsKey(placeString)){
            placeType = PLACES_MAP.get(placeString);
        }
        return placeType ;
    }
}
