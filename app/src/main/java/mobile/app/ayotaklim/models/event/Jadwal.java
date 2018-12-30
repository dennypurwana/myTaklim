package mobile.app.ayotaklim.models.event;

public class Jadwal {

    private int id;
    private String kegiatan;
    private String sampai;
    private String dari;
    private String namaUstadz;
    private int cEventId;
    private String imagebase64;
    private String nama_event;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKegiatan() {
        return kegiatan;
    }

    public void setKegiatan(String kegiatan) {
        this.kegiatan = kegiatan;
    }

    public String getSampai() {
        return sampai;
    }

    public void setSampai(String sampai) {
        this.sampai = sampai;
    }

    public String getDari() {
        return dari;
    }

    public void setDari(String dari) {
        this.dari = dari;
    }

    public String getNamaUstadz() {
        return namaUstadz;
    }

    public void setNamaUstadz(String namaUstadz) {
        this.namaUstadz = namaUstadz;
    }

    public int getcEventId() {
        return cEventId;
    }

    public void setcEventId(int cEventId) {
        this.cEventId = cEventId;
    }

    public String getImagebase64() {
        return imagebase64;
    }

    public void setImagebase64(String imagebase64) {
        this.imagebase64 = imagebase64;
    }

    public String getNama_event() {
        return nama_event;
    }

    public void setNama_event(String nama_event) {
        this.nama_event = nama_event;
    }
}
