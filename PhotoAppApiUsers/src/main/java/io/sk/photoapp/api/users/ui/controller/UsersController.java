package io.sk.photoapp.api.users.ui.controller;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sk.photoapp.api.users.service.UsersService;
import io.sk.photoapp.api.users.shared.UserDto;
import io.sk.photoapp.api.users.ui.model.CreateUserRequestModel;
import io.sk.photoapp.api.users.ui.model.CreateUserResponseModel;

@RestController
@RequestMapping("/users")
public class UsersController {
	
	@Autowired
	UsersService usersService;
	
	@Autowired
	private Environment env;
	
	@GetMapping("/status/check")
	public String status() {
		return "working on port "+ env.getProperty("local.server.port")+", with token = "+ env.getProperty("token.secret");
	}
	
	@PostMapping(consumes = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML},
			produces = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userDetails) {
		
		
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto createdUser = usersService.createUse(userDto);
		CreateUserResponseModel cr = modelMapper.map(createdUser,CreateUserResponseModel.class);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(cr);
	}

}
