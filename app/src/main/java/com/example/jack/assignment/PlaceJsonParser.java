/*
package com.example.jack.assignment;



        import java.util.ArrayList;
        import java.util.List;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;
        import android.util.Log;

        import com.google.android.gms.location.places.Place;

public class PlaceJsonParser {
    private static final String RESULTS = "results";
    private static final String GEOMETRY = "geometry";
    private static final String NAME = "name";
    private static final String ADDRESS = "vicinity";
    private static final String ICON = "icon";
    private static final String LOCATION = "location";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String STATUS = "status";

    public static List<Place> placeJsonParser(String json) {

        List<Place> placeList = new ArrayList<Place>();

        if(json.equals("")) {
            return placeList;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(json);

                if(jsonObject.getString(STATUS).equals("OK")) {
                    JSONArray results = jsonObject.getJSONArray(RESULTS);

                    String name = null;
                    String addr = null;
                    String icon = null;
                    double lat = 0;
                    double lng = 0;

                    for(int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);

                        name = result.getString(NAME);
                        addr = result.getString(ADDRESS);
                        icon = result.getString(ICON);
                        JSONObject geometry = result.getJSONObject(GEOMETRY);
                        JSONObject location = geometry.getJSONObject(LOCATION);
                        lat = location.getDouble(LAT);
                        lng = location.getDouble(LNG);

                        placeList.add(new Place(name, addr, lat, lng, icon, result.toString()));

                        Log.d("name : " + name + ", lat :" + lat + ", lng : " + lng);
                    }
                    return placeList;
                } else {

                    return placeList;
                }

            } catch (JSONException e) {
                return placeList;
            }
        }
    }
}*/
