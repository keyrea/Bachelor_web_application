package com.example.demo.utils;

import com.example.demo.model.Organization;
import com.example.demo.model.Services;
import com.example.demo.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhysicianFactory {

    private final static Random random = new Random();
    private static final ServiceFactory serviceFactory = new ServiceFactory();


    public static User createPhysician() {

        User physician = new User();

        Organization organization = OrganizationFactory.createOrganization();

        List<Services> servicesList = new ArrayList<>();
        servicesList.add(serviceFactory.createService_1());
        servicesList.add(serviceFactory.createService_2());

        physician.setName("doctor_user");
        physician.setSurname("doctor_user_surname");
        physician.setSpeciality("speciality");
        physician.setLanguage("language");
        physician.setEducation("education");
        physician.setExperience("experience");
        physician.setEmail("doctor@mail.com");
        physician.setTelephone(randomTelephone());
        physician.setUsername("DocUser");
        physician.setRole("PHYSICIAN");
        physician.setPassword("my_secret_password");
        physician.setOrganization(organization);
        physician.setServices(servicesList);

        return physician;

    }

    private static String randomTelephone() {
        StringBuilder phone = new StringBuilder("+");
        for (int i = 0; i < 12; i++) {
            phone.append(random.nextInt(10));
        }
        return phone.toString();
    }
}


