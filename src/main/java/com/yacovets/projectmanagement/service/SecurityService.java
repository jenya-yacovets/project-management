package com.yacovets.projectmanagement.service;

import com.yacovets.projectmanagement.entity.*;
import com.yacovets.projectmanagement.model.UserPasswordRecoveryModel;
import com.yacovets.projectmanagement.model.UserRegistrationModel;
import com.yacovets.projectmanagement.model.UserSetPasswordModel;
import com.yacovets.projectmanagement.repository.EmailTokenRepository;
import com.yacovets.projectmanagement.repository.UserRepository;
import com.yacovets.projectmanagement.service.exception.EmailIsBusyServiceException;
import com.yacovets.projectmanagement.service.exception.EmailTokenNotFoundServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotFoundServiceException;
import com.yacovets.projectmanagement.service.exception.UsernameIsBusyServiceException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class SecurityService implements UserDetailsService {
    @Value("${security.mail.time-expired-email-verification-link}")
    private int timeExpiredEmailVerificationLink;

    @Value("${security.mail.time-expired-email-recovery-password-link}")
    private int timeExpiredEmailRestorePasswordLink;

    @Value("${default.new-user-role-id}")
    private int defaultUserRole;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final EmailService emailService;

    public SecurityService(@Lazy PasswordEncoder passwordEncoder, ModelMapper modelMapper, UserRepository userRepository, EmailTokenRepository emailTokenRepository, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.emailTokenRepository = emailTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(s, s).orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
        user.setRoles(Stream.of(new UserRole(defaultUserRole)).collect(Collectors.toSet()));
        User userSave = userRepository.save(user);

        LocalDateTime expiredAtEmailToken = LocalDateTime.now().plusMinutes(timeExpiredEmailVerificationLink);
        EmailToken emailToken = emailTokenRepository.save(new EmailToken(userSave, EmailTokenTypeEnum.EMAIL_CONFIRMATION, expiredAtEmailToken));
        emailService.sendMailSuccessRegistrationAndEmailVerification(userSave.getEmail(), emailToken.getId());

        log.info("A new user has been registered: {}", userSave);
        return userSave;
    }

    public void emailVerification(UUID token) throws EmailTokenNotFoundServiceException {
        EmailToken emailToken = emailTokenRepository.findByIdAndAndIsUsedAndExpiredAtGreaterThanEqualAndType(token, false, LocalDateTime.now(), EmailTokenTypeEnum.EMAIL_CONFIRMATION).orElseThrow(EmailTokenNotFoundServiceException::new);
        emailToken.setUsed(true);
        emailTokenRepository.save(emailToken);

        emailToken.getUser().setEmailVerified(true);
        userRepository.save(emailToken.getUser());

        log.info("Success email verification: {}", emailToken);
    }

    public EmailToken passwordRecovery(UserPasswordRecoveryModel model) throws UserNotFoundServiceException {
        User user = userRepository.findByUsernameAndEmail(model.getUsername(), model.getEmail()).orElseThrow(UserNotFoundServiceException::new);

        EmailToken emailToken = new EmailToken();
        emailToken.setUser(user);
        emailToken.setType(EmailTokenTypeEnum.PASSWORD_RECOVERY);
        LocalDateTime expiredAtEmailToken = LocalDateTime.now().plusMinutes(timeExpiredEmailRestorePasswordLink);
        emailToken.setExpiredAt(expiredAtEmailToken);
        EmailToken saveEmailToken = emailTokenRepository.save(emailToken);

        emailService.sendMailPasswordRecovery(user.getEmail(), saveEmailToken.getId());

        log.info("User password recovery: {}", saveEmailToken);
        return saveEmailToken;
    }

    public void passwordRecoveryIsActive(UUID token) throws EmailTokenNotFoundServiceException {
        if (!emailTokenRepository.existsByIdAndAndIsUsedAndExpiredAtGreaterThanEqualAndType(token, false, LocalDateTime.now(), EmailTokenTypeEnum.PASSWORD_RECOVERY)) {
            throw new EmailTokenNotFoundServiceException();
        }
    }

    public void setPassword(UserSetPasswordModel model, UUID token) throws EmailTokenNotFoundServiceException {
        EmailToken emailToken = emailTokenRepository.findByIdAndAndIsUsedAndExpiredAtGreaterThanEqualAndType(token, false, LocalDateTime.now(), EmailTokenTypeEnum.PASSWORD_RECOVERY).orElseThrow(EmailTokenNotFoundServiceException::new);

        emailToken.setUsed(true);
        emailTokenRepository.save(emailToken);

        emailToken.getUser().setPassword(passwordEncoder.encode(model.getPassword()));
        userRepository.save(emailToken.getUser());

        log.info("Success set new password: {}", emailToken);
    }
}
