package com.example.demo.utils;

import com.example.demo.model.Services;
import lombok.Data;

@Data
public class ServiceFactory {

    private static Services service_1;
    private static Services service_2;

    public ServiceFactory() {
        // Initialize service_1 and service_2 here
        service_1 = new Services();
        service_2 = new Services();
    }

    public static Services createService_1(){

        service_1.setId(1L);
        service_1.setServiceName("Service_1");

        return service_1;
    }

    public static Services createService_2(){

        service_2.setId(2L);
        service_2.setServiceName("Service_2");

        return service_2;

    }

}
