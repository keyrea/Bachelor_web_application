package com.example.demo.utils;

import com.example.demo.model.Organization;

public class OrganizationFactory {

    public static Organization createOrganization() {
        Organization organization = new Organization();
        organization.setName("Your Organization Name");
        organization.setKind("Your Organization Kind");
        organization.setCountry("Your Organization Country");
        organization.setCity("Your Organization City");
        organization.setPostalCode("Your Organization Postal Code");
        organization.setStreet("Your Organization Street");
        return organization;
    }

}
