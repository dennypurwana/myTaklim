package mobile.app.ayotaklim.models.venue;

import java.io.Serializable;

public class Venue implements Serializable {

    private int id;
    private String nama;
    private String alamat;
    private String noTlp;
    private String dkm;
    private String dkmPhone;
    private String imageVenue;
    private String deskripsi;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private double longitude;
    private double latitude;

    public String getDkm() {
        return dkm;
    }

    public void setDkm(String dkm) {
        this.dkm = dkm;
    }

    public String getDkmPhone() {
        return dkmPhone;
    }

    public void setDkmPhone(String dkmPhone) {
        this.dkmPhone = dkmPhone;
    }

    public String getImageVenue() {
        return imageVenue;
    }

    public void setImageVenue(String imageVenue) {
        this.imageVenue = imageVenue;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }




    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTlp() {
        return noTlp;
    }

    public void setNoTlp(String noTlp) {
        this.noTlp = noTlp;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Venue(String nama, String alamat, String noTlp, double longitude, double latitude) {
        this.nama = nama;
        this.alamat = alamat;
        this.noTlp = noTlp;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Venue() {
    }
}
