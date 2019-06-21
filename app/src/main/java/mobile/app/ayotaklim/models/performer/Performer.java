package mobile.app.ayotaklim.models.performer;

import java.io.Serializable;

public class Performer  implements Serializable {

    private int record_id;
    private String nama;
    private String tglLahir;
    private String alamat;
    private String pendidikan;
    private String deskripsi;
    private String phone;
    private String email;
    private String instagram;
    private String facebook;
    private String youtube;
    private String imageUstadz;

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public String getImageUstadz() {
        return imageUstadz;
    }

    public void setImageUstadz(String imageUstadz) {
        this.imageUstadz = imageUstadz;
    }

    public Performer() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTglLahir() {
        return tglLahir;
    }

    public void setTglLahir(String tglLahir) {
        this.tglLahir = tglLahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPendidikan() {
        return pendidikan;
    }

    public void setPendidikan(String pendidikan) {
        this.pendidikan = pendidikan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Performer(String nama, String tglLahir, String alamat, String pendidikan, String deskripsi) {
        this.nama = nama;
        this.tglLahir = tglLahir;
        this.alamat = alamat;
        this.pendidikan = pendidikan;
        this.deskripsi = deskripsi;
    }
}
