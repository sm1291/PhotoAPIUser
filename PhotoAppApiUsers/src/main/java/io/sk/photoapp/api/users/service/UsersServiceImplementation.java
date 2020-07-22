package io.sk.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.ribbon.proxy.annotation.Http;

import io.sk.photoapp.api.users.data.AlbumsServiceClient;
import io.sk.photoapp.api.users.data.UserEntity;
import io.sk.photoapp.api.users.data.UserRepository;
import io.sk.photoapp.api.users.shared.UserDto;
import io.sk.photoapp.api.users.ui.model.AlbumResponseModel;

@Service
public class UsersServiceImplementation implements UsersService {

	UserRepository userRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	AlbumsServiceClient albumsServiceClient;

	//@Autowired
	//private RestTemplate restTemplate;
	@Autowired
	private Environment environment;

	@Autowired
	public UsersServiceImplementation(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AlbumsServiceClient albumsServiceClient) {
		// super();
		// TODO Auto-generated constructor stub
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.albumsServiceClient = albumsServiceClient;
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
		if (userEntity == null)
			throw new UsernameNotFoundException(username);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true,
				new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new ModelMapper().map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		// TODO Auto-generated method stub

		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException("user not found");
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
		/*String url = String.format(environment.getProperty("albums.url"), userId) ;
		ResponseEntity<List<AlbumResponseModel>> album = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<AlbumResponseModel>>() {
				});
		List<AlbumResponseModel> albList = album.getBody();
		*/
		
		List<AlbumResponseModel> albList = albumsServiceClient.getAlbums(userId);
		userDto.setAlbums(albList);
		return userDto;
	}

}
