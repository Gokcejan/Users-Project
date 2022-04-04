package cz.acrobits.csp.app_users_management.service;

import cz.acrobits.csp.app_users_management.domain.user.factory.UserMapper;
import cz.acrobits.csp.app_users_management.domain.user.model.User;
import cz.acrobits.csp.app_users_management.domain.user.repository.UserJpaQuerySpecificationFactory;
import cz.acrobits.csp.app_users_management.domain.user.repository.UserRepository;
import cz.acrobits.csp.app_users_management.rest.dto.UserAuthenticateDto;
import cz.acrobits.csp.app_users_management.rest.dto.UserCreateDto;
import cz.acrobits.csp.app_users_management.rest.dto.UserDto;
import cz.acrobits.csp.app_users_management.rest.dto.UserPatchDto;
import cz.acrobits.csp.core.common.servicelayer.exceptions.BadRequestException;
import cz.acrobits.csp.core.common.servicelayer.exceptions.NotFoundException;
import cz.acrobits.csp.core.jpa.query.PageSpecification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static java.lang.String.format;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserJpaQuerySpecificationFactory userJpaQuerySpecificationFactory;
    private static final String remove = "*";

    @PreAuthorize(
            "@permissionEvaluator.canExecute(" +
                    "'User', 'create'," +
                    "{'organizationId': #createDto.organizationId" +
                    "})")
    public UserDto createUser(UserCreateDto createDto) {
        User user = userMapper.toEntity(createDto);
        User savedUser = userRepository.save(user);


        return userMapper.toDto(savedUser);
    }

    @PreAuthorize("@permissionEvaluator.canExecute('User', 'read', {'userId': #userId})")
    public UserDto getUser(Long userId) {
        User user = findUser(userId);
        return userMapper.toDto(user);
    }

    public User findUser(Long userId) {
        Optional<User> userOptional = userRepository.findOneById(userId);
        return userOptional.orElseThrow(() -> new NotFoundException(format("User with UserId: %d not found.",
                userId)));
    }

    @PreAuthorize("@permissionEvaluator.canExecute('User', 'delete', {'userId': #userId})")
    public void deleteUser(Long userId) {
        User user = findUser(userId);
        userRepository.delete(user);
    }

    public boolean authenticateUser(UserAuthenticateDto authenticateDto) {

        String normalizeCloudIdAsterisk = normalizeCloudIdAsterisk(authenticateDto.getCloudId());

        Optional<User> userOptional = userRepository.findOneByUsernameAndCloudId(authenticateDto.getUsername(),
                normalizeCloudIdAsterisk);

        if (userOptional.isEmpty()) {
            return false;
        } else {
            User user = userOptional.get();
            return user.getEmail().equals(authenticateDto.getEmail());
            //return user.getEmail().equals(authenticateDto.getEmail());
        }

    }

    private String normalizeCloudIdAsterisk(String cloudId) {

        String lastCharacter = cloudId.substring(cloudId.length() - 1);

        if (lastCharacter.equals(remove)) {
            String id = cloudId.substring(0, cloudId.length() - 1);
            return id;
        } else {
            return cloudId;
        }

    }

    public UserDto patchIssue(Long userId, UserPatchDto dto) {
        User user = findUser(userId);
        userMapper.mapPatch(dto, user);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);

    }


    public Page<UserDto> getUsersPage(Map<String, String> queryParams) {

        PageSpecification<User> spec = userJpaQuerySpecificationFactory.create(queryParams);
        Page<User> users = userRepository.findAll(spec.getSpecification(), spec.getPageable());
        return users.map(user -> userMapper.toDto(user));
    }

    @Transactional
    public void importUsers(MultipartFile file, String cloudId, Long applicationId, Long organizationId) {
        try (Scanner scanner = new Scanner(file.getInputStream())) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.equals("username,email")) {
                    User user = userMapper.toEntityFromCsv(line, cloudId, applicationId, organizationId);
                    userRepository.save(user);
                }
            }
        } catch (IOException e) {
            throw new BadRequestException("Invalid csv file");
        }
    }

}
