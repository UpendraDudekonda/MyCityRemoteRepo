//package com.mycity.trip.utility;
//import com.github.davidmoten.geo.GeoHash;
//import com.github.davidmoten.geo.LatLong;
//import com.github.davidmoten.geo.util.GeoUtils;
//import com.mycity.shared.tripplannerdto.CoordinateDTO;
//import com.github.davidmoten.geo.polyline.PolylineEncoder;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class PolylineUtil {
//
//    public static String encodePolyline(List<CoordinateDTO> coords) {
//        List<LatLong> latLongs = coords.stream()
//                .map(coord -> new LatLong(coord.getLatitude(), coord.getLongitude()))
//                .collect(Collectors.toList());
//
//        return PolylineEncoder.encode(latLongs);
//    }
//}
