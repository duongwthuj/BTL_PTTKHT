package model;

import java.sql.Timestamp;

public class RestaurantTable {
    private int id;
    private int tableNumber;
    private String name;
    private String location;
    private int capacity;
    private String status; // "available", "occupied", "reserved"
    private Timestamp createdAt;

    // Constructor rỗng
    public RestaurantTable() {
    }

    // Constructor đầy đủ
    public RestaurantTable(int id, int tableNumber, String name, String location, int capacity, String status, Timestamp createdAt) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor cho insert
    public RestaurantTable(int tableNumber, String name, String location, int capacity, String status) {
        this.tableNumber = tableNumber;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "RestaurantTable{" +
                "id=" + id +
                ", tableNumber=" + tableNumber +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
