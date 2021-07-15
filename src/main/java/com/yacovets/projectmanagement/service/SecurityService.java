package com.yacovets.projectmanagement.service;

import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.entity.UserRole;
import com.yacovets.projectmanagement.entity.UserStatusEnum;
import com.yacovets.projectmanagement.model.UserRegistrationModel;
import com.yacovets.projectmanagement.repository.UserRepository;
import com.yacovets.projectmanagement.service.exception.EmailIsBusyServiceException;
import com.yacovets.projectmanagement.service.exception.UsernameIsBusyServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class SecurityService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public SecurityService(@Lazy PasswordEncoder passwordEncoder, ModelMapper modelMapper, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user_not_found = userRepository.findByUsernameOrEmail(s, s).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.out.println(user_not_found);
        return user_not_found;
    }

    public User registration(UserRegistrationModel userRegistrationModel) throws UsernameIsBusyServiceException, EmailIsBusyServiceException {
        if(userRepository.existsByUsername(userRegistrationModel.getUsername())) {
            throw new UsernameIsBusyServiceException();
        }
        if(userRepository.existsByEmail(userRegistrationModel.getEmail())) {
            throw new EmailIsBusyServiceException();
        }

        User user = modelMapper.map(userRegistrationModel, User.class);
        user.setPassword(passwordEncoder.encode(userRegistrationModel.getPassword()));
        user.setActive(true);
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setRoles(Stream.of(new UserRole(1)).collect(Collectors.toSet()));

        return userRepository.save(user);
    }
}
