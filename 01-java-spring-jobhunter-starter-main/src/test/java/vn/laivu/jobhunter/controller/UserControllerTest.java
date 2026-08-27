package vn.laivu.jobhunter.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static vn.laivu.jobhunter.util.constant.Gender.FEMALE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.laivu.jobhunter.unity.User;
import vn.laivu.jobhunter.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User existingUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        existingUser = new User();
        existingUser.setName("Existing User");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(existingUser);
    }

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("201 Created khi data hợp lệ")
        void createUser_validData_returns201() throws Exception {

            User newUser = new User();
            newUser.setName("New User");
            newUser.setEmail("new@example.com");
            newUser.setPassword("password123");

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value("New User"))
                    .andExpect(jsonPath("$.data.email").value("new@example.com"))
                    .andExpect(jsonPath("$.data.password").doesNotExist());

            User dbUser = userRepository.findByEmail("new@example.com");

            assertNotNull(dbUser);

            assertTrue(
                    passwordEncoder.matches(
                            "password123",
                            dbUser.getPassword()
                    )
            );
        }
    }

    @Nested
    @DisplayName("Validation Errors (400 Bad Request)")
    class ValidationErrors {

        @Test
        @DisplayName("Name để trống")
        void createUser_blankName_returns400() throws Exception {
            User newUser = new User();
            newUser.setName("");
            newUser.setEmail("test@example.com");
            newUser.setPassword("password123");

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest());

            assert (userRepository.count() == 1);
        }

        @Test
        @DisplayName("Email không đúng định dạng")
        void createUser_invalidEmail_returns400() throws Exception {
            User newUser = new User();
            newUser.setName("Test User");
            newUser.setEmail("invalid-email");
            newUser.setPassword("password123");

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest());

            assert (userRepository.count() == 1);
        }

        @Test
        @DisplayName("Password ít hơn 6 ký tự")
        void createUser_shortPassword_returns400() throws Exception {
            User newUser = new User();
            newUser.setName("Test User");
            newUser.setEmail("test@example.com");
            newUser.setPassword("12345");

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest());

            assert (userRepository.count() == 1);
        }
    }

    @Nested
    @DisplayName("Business Logic Error")
    class BusinessErrors {

        @Test
        @DisplayName("400 Bad request khi email đã tồn tại")
        void createUser_duplicateEmail_returns400() throws Exception {
            User newUser = new User();
            newUser.setName("Another User");
            newUser.setEmail("existing@example.com");
            newUser.setPassword("password123");
            newUser.setGender(FEMALE);
            newUser.setAge(25);
            newUser.setAddress("123 Main St");

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest());

            assert (userRepository.count() == 1);
        }
    }
}
