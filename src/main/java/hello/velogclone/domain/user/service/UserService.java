package hello.velogclone.domain.user.service;

import hello.velogclone.domain.user.dto.UserDto;
import hello.velogclone.domain.user.entity.Role;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(UserDto userDto) {
        User user = convertToEntity(userDto);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public UserDto findByLoginId(String loginId) {
        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
        return optionalUser.map(UserDto::of).orElse(null);
    }

    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setLoginId(userDto.getLoginId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRole(Role.valueOf(userDto.getRole()));
        return user;
    }
}
