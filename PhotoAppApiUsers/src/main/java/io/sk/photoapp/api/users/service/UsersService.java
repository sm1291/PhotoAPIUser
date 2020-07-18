package io.sk.photoapp.api.users.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import io.sk.photoapp.api.users.shared.UserDto;

public interface UsersService extends UserDetailsService {
	UserDto createUse(UserDto userDetails);
	UserDto getUserDetailsByEmail(String email);

}
