package com.foru.freebe.profile.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.profile.dto.CustomerViewProfileResponse;
import com.foru.freebe.profile.entity.Profile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {
    private final ProfileService profileService;

    public CustomerViewProfileResponse getPhotographerProfile(String profileName) {
        Profile profile = profileService.getProfile(profileName);
        return profileService.findCustomerViewProfile(profile);
    }
}
