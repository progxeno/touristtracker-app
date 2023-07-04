package htwg.trackingapp;

/**
 * Created by Alex on 25.06.14.
 */

/**
 * @description     This class handles the dailyRoute objects, provides getter & setter,
 *                  and covers the sustainability indicator calculation
 */
public class DailyRoute {
    private static final double walkPointsPerKm = 10;
    private static final double carPointsPerKm = 1;
    private static final double trainPointsPerKm = 7;
    private static final double ferryPointsPerKm = 4;

    private int id;
    private String date;
    private double walkDistance;
    private double carDistance;
    private double trainDistance;
    private double ferryDistance;
    private double totalDistance;
    private double sustainabilityIndicator;

    public DailyRoute() {
    }

    public DailyRoute(String date, double walkDistance,
                      double carDistance, double trainDistance,
                      double ferryDistance, double totalDistance) {
        this.date = date;
        this.walkDistance = walkDistance;
        this.carDistance = carDistance;
        this.trainDistance = trainDistance;
        this.ferryDistance = ferryDistance;
        this.totalDistance = totalDistance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(double walkDistance) {
        this.walkDistance = walkDistance;
    }

    public double getCarDistance() {
        return carDistance;
    }

    public void setCarDistance(double carDistance) {
        this.carDistance = carDistance;
    }

    public double getTrainDistance() {
        return trainDistance;
    }

    public void setTrainDistance(double trainDistance) {
        this.trainDistance = trainDistance;
    }

    public double getFerryDistance() {
        return ferryDistance;
    }

    public void setFerryDistance(double ferryDistance) {
        this.ferryDistance = ferryDistance;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    /**
     * @description         This method calculates the daily sustainability indicator
     * @return              sustainability indicator
     */
    public int getSustainabilityIndicator() {
        if (totalDistance > 0) {
            sustainabilityIndicator =   (walkDistance * walkPointsPerKm +
                                        carDistance * carPointsPerKm +
                                        trainDistance * trainPointsPerKm +
                                        ferryDistance * ferryPointsPerKm) /
                                        ((walkPointsPerKm - 1) * totalDistance);
        }
        else
            sustainabilityIndicator = 0;

        return (int)sustainabilityIndicator * 100;
    }


    @Override
    public String toString() {
        return "DailyRoute{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", walkDistance=" + walkDistance +
                ", carDistance=" + carDistance +
                ", trainDistance=" + trainDistance +
                ", ferryDistance=" + ferryDistance +
                ", totalDistance=" + totalDistance +
                '}';
    }
}
