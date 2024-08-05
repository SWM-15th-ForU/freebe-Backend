package com.foru.freebe.profile.service;

import com.foru.freebe.profile.entity.ApiResponseDto;

public interface ProfileService {
	ApiResponseDto<String> registerUniqueUrl();
}
