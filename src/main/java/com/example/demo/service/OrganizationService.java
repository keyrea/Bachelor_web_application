package com.example.demo.service;

import com.example.demo.model.Organization;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private HttpServletRequest request;

    public Organization findById(Long id) {
        Optional<Organization> organizationOptional = organizationRepository.findById(id);
        if (organizationOptional.isEmpty()) {

            throw new RuntimeException("Organization with ID " + id + " not found.");

        }
        return organizationOptional.get();
    }

    public boolean compareOrganizationIdOfAdminWithOrganizationIdOfPhysician(Long physicianId){
        // get JWT token from request header
        String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = requestTokenHeader.substring("Bearer ".length());

        // extract organization id from JWT token
        Long adminOrganizationId = jwtTokenUtil.extractOrganizationId(jwtToken);

        // compare organizationId of admin with organizationId entered for physician
        if(!physicianId.equals(adminOrganizationId)){
            return false;
        }
        return true;
    }

}
