package mobile.app.ayotaklim.models.event;

import java.io.Serializable;

public class Event implements Serializable {

    private int id;
    private int venueId;
    private int performerId;
    private String namaEvent;
    private String bannerImage;
    private String namaVenue;
    private String alamatVenue;

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getNamaVenue() {
        return namaVenue;
    }

    public void setNamaVenue(String namaVenue) {
        this.namaVenue = namaVenue;
    }

    public String getAlamatVenue() {
        return alamatVenue;
    }

    public void setAlamatVenue(String alamatVenue) {
        this.alamatVenue = alamatVenue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String tglMulai;
    private String tglBerakhir;

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public int getPerformerId() {
        return performerId;
    }

    public void setPerformerId(int performerId) {
        this.performerId = performerId;
    }

    public String getNamaEvent() {
        return namaEvent;
    }

    public void setNamaEvent(String namaEvent) {
        this.namaEvent = namaEvent;
    }

    public String getTglMulai() {
        return tglMulai;
    }

    public void setTglMulai(String tglMulai) {
        this.tglMulai = tglMulai;
    }

    public String getTglBerakhir() {
        return tglBerakhir;
    }

    public void setTglBerakhir(String tglBerakhir) {
        this.tglBerakhir = tglBerakhir;
    }

    public Event() {
    }
}
