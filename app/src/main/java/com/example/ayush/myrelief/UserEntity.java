package com.example.ayush.myrelief;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class UserEntity extends TableServiceEntity{

    String email, name, description,  latitude, longitude;
    Boolean status;

    public UserEntity(String partitionKey, String rowKey, String email, String name, String description, Boolean status, String latitude, String longitude) {
        super(partitionKey, rowKey);
        this.email = email;
        this.name = name;
        this.description = description;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UserEntity(){
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
