package com.digitalwallet.model;

public class Service {
    private int serviceId;
    private String serviceName;
    private String serviceProvider;

    public Service(int serviceId, String serviceName, String serviceProvider) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceProvider = serviceProvider;
    }

    // Getters
    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    // Setters
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
} 