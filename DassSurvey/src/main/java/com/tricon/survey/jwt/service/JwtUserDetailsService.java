package com.tricon.survey.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.tricon.survey.db.entity.DassUser;
import com.tricon.survey.jpa.repository.DassUserRepo;
import com.tricon.survey.security.JwtUserFactory;

@Service
public class JwtUserDetailsService implements UserDetailsService{

	@Autowired
	DassUserRepo dassUserRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		DassUser user = dassUserRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("No user found with email '%s'.",email));
		} else {
			return JwtUserFactory.create(user);
		}
	}
}
