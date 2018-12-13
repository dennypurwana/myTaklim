package mobile.app.ayotaklim.models.event;

import java.io.Serializable;

public class Event implements Serializable {

    private String title;
    private String venue;
    private String venueAddress;
    private String topic;
    private String category;
    private String image;
    private String date;
    private String startTime;
    private String endTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageE() {
        return image;
    }

    public void setImageE(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public Event(String title, String venue, String venueAddress, String topic, String category, String image, String date, String startTime, String endTime) {
        this.title = title;
        this.venue = venue;
        this.venueAddress = venueAddress;
        this.topic = topic;
        this.category = category;
        this.image = image;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
