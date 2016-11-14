package com.main.getOpenData.DAO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @NotNull

    private String name;

    @NotNull
    private String address;

    @NotNull
    @Column(name = "lng")
    private double longitude;

    @NotNull
    @Column(name = "lat")
    private double latitude;

    @NotNull
    private int idType;

    @Column(name = "parent_id")
    private int parentId;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date date;

    @NotNull
    private String url;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "work_time")
    private String workTime;

    @Column(name = "additional_info")
    private String additionalInfo;

    @Column(name = "id_from_source")
    private long idFromSource;

    public Company() {
    }

    public Company(String name, String address, double longitude, double latitude, int idType, int parentId,
                   Date date, String url, String phoneNumber, String workTime, String additionalInfo, long idFromSource) {
        this.name = name;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.idType = idType;
        this.parentId = parentId;
        this.date = date;
        this.url = url;
        this.phoneNumber = phoneNumber;
        this.workTime = workTime;
        this.additionalInfo = additionalInfo;
        this.idFromSource = idFromSource;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getIdType() {
        return idType;
    }

    public Date getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public int getParentId() {
        return parentId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWorkTime() {
        return workTime;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public long getIdFromSource() {
        return idFromSource;
    }

    @PersistenceContext
    public void setId(long value) {
        this.id = value;
    }

    @PersistenceContext
    public void setName(String name) {
        this.name = name;
    }

    @PersistenceContext
    public void setAddress(String address) {
        this.address = address;
    }

    @PersistenceContext
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @PersistenceContext
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @PersistenceContext
    public void setIdType(int idType) {
        this.idType = idType;
    }

    @PersistenceContext
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @PersistenceContext
    public void setDate(Date date) {
        this.date = date;
    }

    @PersistenceContext
    public void setUrl(String url) {
        this.url = url;
    }

    @PersistenceContext
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @PersistenceContext
    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    @PersistenceContext
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @PersistenceContext
    public void setIdFromSource(long idFromSource) {
        this.idFromSource = idFromSource;
    }
}

