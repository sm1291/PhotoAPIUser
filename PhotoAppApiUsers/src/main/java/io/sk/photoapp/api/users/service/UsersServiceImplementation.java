package io.sk.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.sk.photoapp.api.users.data.UserEntity;
import io.sk.photoapp.api.users.data.UserRepository;
import io.sk.photoapp.api.users.shared.UserDto;

@Service
public class UsersServiceImplementation implements UsersService {
	
	UserRepository userRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
    @Autowired
	public UsersServiceImplementation(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		//super();
		// TODO Auto-generated constructor stub
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}



	@Override
	public UserDto createUse(UserDto userDetails) {
		// TODO Auto-generated method stub
		
		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
		userRepository.save(userEntity);
		UserDto udto = modelMapper.map(userEntity, UserDto.class); 
		return udto;
	}



	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findByEmail(username);
		if(userEntity == null) throw new UsernameNotFoundException(username);
		
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true,true, true, new ArrayList<>());
	}



	@Override
	public UserDto getUserDetailsByEmail(String email) {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findByEmail(email);
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}

}
