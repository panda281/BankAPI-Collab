package com.gebeya.bankapi.Model.DTO;

public class TopUpResponseDTO {
    private String senum;
    private String scnum;
    private String expDate;

    public TopUpResponseDTO() {
    }

    public TopUpResponseDTO(String senum, String scnum, String expDate) {
        this.senum = senum;
        this.scnum = scnum;
        this.expDate = expDate;
    }

    public String getSenum() {
        return senum;
    }

    public void setSenum(String senum) {
        this.senum = senum;
    }

    public String getScnum() {
        return scnum;
    }

    public void setScnum(String scnum) {
        this.scnum = scnum;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    @Override
    public String toString() {
        return "topUp{" +
                "senum='" + senum + '\'' +
                ", scnum='" + scnum + '\'' +
                ", endDate='" + expDate + '\'' +
                '}';
    }
}
