package htwg.trackingapp;

import android.location.Location;


/**
 * Created by Alex on 25.06.14.
 */

/**
 * @description     This class handles the GPSlog objects, provides getter & setter,
 *                  and covers the distance calculation
 */
public class GPSLog {
    private int id;
    private int vehicle;
    private long timeStamp;
    private double longitude;
    private double latitude;
    private double distance;
    private boolean isSentToServer;


    // specifies max valid distance between two gps positions
    private static final double MAX_DISTANCE_FOR_SET = 10;
    private static final double DEFAULT_DISTANCE = 0;

    public GPSLog(){}

    public GPSLog(int vehicle, Location location) {

        if (vehicle >= 0 && vehicle < 4)
            this.vehicle = vehicle;
        else
            this.vehicle = 0;

        this.timeStamp = (location.getTime()/1000);
        this.longitude = location.getLongitude();
        this.latitude =  location.getLatitude();
        this.distance = 0;
        this.isSentToServer = false;


    }

    public boolean isSentToServer() {
        return isSentToServer;
    }

    public void setSentToServer(boolean isSentToServer) {
        this.isSentToServer = isSentToServer;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public int getVehicle() {
        return vehicle;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setVehicle(int vehicle) {
        if (vehicle >= 0 && vehicle < 4)
            this.vehicle = vehicle;
        else
            this.vehicle = 0;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "GPSLog{" +
                "id=" + id +
                ", vehicle=" + vehicle +
                ", timeStamp=" + timeStamp +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", distance=" + distance +
                ", isSentToServer=" + isSentToServer +
                '}';
    }

    public double calculateDistanceTwoPoints(double oldLatitude, double oldLongitude) {


        int R = 6371; // km
        double phi1 = oldLatitude * Math.PI/180;
        double phi2 = latitude * Math.PI/180;
        double deltaPhi = (latitude-oldLatitude) * Math.PI/180;
        double deltaLambda = (longitude-oldLongitude) * Math.PI/180;

        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                   Math.cos(phi1) * Math.cos(phi2) *
                   Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double distance = R * c;

        // TODO Plausibilitaetspruefung aktivieren
        if (distance > MAX_DISTANCE_FOR_SET)
            return DEFAULT_DISTANCE;
        else
        return distance;
    }
}

