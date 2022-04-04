package cz.acrobits.csp.app_users_management.service;


import cz.acrobits.csp.app_users_management.domain.mailing.EmailFactory;
import cz.acrobits.csp.app_users_management.domain.mailing.MailerService;
import cz.acrobits.csp.app_users_management.domain.user.factory.PasswordResetRequestMapper;
import cz.acrobits.csp.app_users_management.domain.user.model.PasswordResetRequest;
import cz.acrobits.csp.app_users_management.domain.user.model.User;
import cz.acrobits.csp.app_users_management.domain.user.repository.PasswordResetRequestRepository;
import cz.acrobits.csp.app_users_management.domain.user.repository.UserRepository;
import cz.acrobits.csp.app_users_management.rest.dto.PasswordResetRequestDto;

import cz.acrobits.csp.app_users_management.rest.dto.PasswordSetRequestDto;
import cz.acrobits.csp.mailer.command.mail.request.CreateUnprocessedEmailCommand;
import cz.acrobits.csp.security.core.anonymoususer.AnonymousUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
public class PasswordOperations {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordResetRequestRepository passwordResetRequestRepository;
    @Autowired
    private PasswordResetRequestMapper passwordResetRequestMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailFactory emailFactory;
    @Autowired
    private MailerService mailerService;

    @Transactional
    @AnonymousUser
    public void createPasswordResetRequest(PasswordResetRequestDto createDto) {

        Optional<User> optionalUser = userRepository.findOneByEmail(createDto.getEmail());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            PasswordResetRequest passwordResetRequest = passwordResetRequestMapper.toEntity(user);

            CreateUnprocessedEmailCommand resetPasswordEmailCommand = emailFactory.createResetPasswordEmailCommand(passwordResetRequest);
            mailerService.sendEmail(resetPasswordEmailCommand);

            passwordResetRequestRepository.save(passwordResetRequest);
        }
    }

    @Transactional
    public void setPassword(PasswordSetRequestDto passwordSet) {

        Optional<PasswordResetRequest> passwordResetRequestOptional = passwordResetRequestRepository.findOneByToken(passwordSet.getToken());

        if (passwordResetRequestOptional.isPresent()) {
            PasswordResetRequest passwordResetRequest = passwordResetRequestOptional.get();
            User user = passwordResetRequest.getUser();
            user.setPassword(passwordSet.getPassword());
            userRepository.save(user);
            passwordResetRequestRepository.delete(passwordResetRequest);
        }
    }

    public void createPasswordResetRequestByAdmin(Long userId) {
        User user = userService.findUser(userId);
        PasswordResetRequest passwordResetRequest = passwordResetRequestMapper.toEntity(user);
        passwordResetRequestRepository.save(passwordResetRequest);
    }
}
