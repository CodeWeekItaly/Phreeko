package it.phreeko.objects;

public class LocationRange {

    private float minLat, maxLat, minLng, maxLng;

    public LocationRange() {}

    public float getMinLat() {
        return minLat;
    }

    public void setMinLat(float minLat) {
        this.minLat = minLat;
    }

    public float getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(float maxLat) {
        this.maxLat = maxLat;
    }

    public float getMinLng() {
        return minLng;
    }

    public void setMinLng(float minLng) {
        this.minLng = minLng;
    }

    public float getMaxLng() {
        return maxLng;
    }

    public void setMaxLng(float maxLng) {
        this.maxLng = maxLng;
    }
}
