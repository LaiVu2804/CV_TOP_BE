package vn.laivu.jobhunter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import vn.laivu.jobhunter.unity.User;
import vn.laivu.jobhunter.repository.UserRepository;
import vn.laivu.jobhunter.util.error.DuplicateResourceException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("Tạo mới người dùng")
    class CreateUser {

        @Test
        @DisplayName("Happy path: tạo user thành công, password được encode")
        void createUser_success() {
            // Arrange
            User user = new User();
            user.setEmail("test@example.com");
            user.setPassword("password");

            when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            User createdUser = userService.handleCreateUser(user);

            // Assert
            assertEquals(user.getEmail(), createdUser.getEmail());
            assertEquals("encodedPassword", createdUser.getPassword());

            InOrder inOrder = inOrder(userRepository, passwordEncoder);
            inOrder.verify(userRepository).existsByEmail(user.getEmail());
            inOrder.verify(passwordEncoder).encode("password");
            inOrder.verify(userRepository).save(argThat(new ArgumentMatcher<User>() {
                @Override
                public boolean matches(User user) {
                    return user.getPassword().equals("encodedPassword");
                }
            }));
        }

        @Test
        @DisplayName("Email đã tồn tại: throw DuplicateResourceException")
        void createUser_duplicateEmail_throwException() {
            // Arrange
            User user = new User();
            user.setEmail("test@example.com");
            user.setPassword("password");

            when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

            // Act & Assert
            assertThrows(DuplicateResourceException.class, () -> {
                userService.handleCreateUser(user);
            });

            verify(userRepository, times(1)).existsByEmail(user.getEmail());
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Verify password được encode TRƯỚC KHI save (dùng argThat)")
        void createUser_passwordEncodedBeforeSave() {
            // Arrange
            String rawPassword = "password123";
            String encodedPassword = "encodedPassword123";
            User user = new User();
            user.setEmail("test2@example.com");
            user.setPassword(rawPassword);

            when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            userService.handleCreateUser(user);

            // Assert
            verify(userRepository).save(argThat(savedUser -> {
                assertNotEquals(rawPassword, savedUser.getPassword());
                assertEquals(encodedPassword, savedUser.getPassword());
                return true;
            }));
        }

        @Test
        @DisplayName("Verify save() KHÔNG được gọi khi email duplicate")
        void createUser_saveNotCalledOnDuplicateEmail() {
            // Arrange
            User user = new User();
            user.setEmail("existing@example.com");
            user.setPassword("password");

            when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

            // Act
            assertThrows(DuplicateResourceException.class, () -> {
                userService.handleCreateUser(user);
            });

            // Assert
            verify(userRepository, never()).save(any(User.class));
        }
    }
}
