package com.user.userservice.controller;

import com.nimbusds.jose.util.Pair;
import com.user.userservice.dto.PasswordDTO;
import com.user.userservice.dto.UserLoginDTO;
import com.user.userservice.dto.UserResponseDTO;
import com.user.userservice.dto.UserUpdateDTO;
import com.user.userservice.entity.User;
import com.user.userservice.exception.IncorrectPasswordException;
import com.user.userservice.exception.InvalidPasswordException;
import com.user.userservice.exception.UserNotFoundException;
import com.user.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private User user;
    private UserLoginDTO userLoginDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setRole("USER");
        userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("user@example.com");
        userLoginDTO.setPassword("password");
    }

    @Test
    void loginSuccess() throws IncorrectPasswordException {
        Pair<String, User> responsePair = Pair.of("dummyToken", user);
        when(userService.login(userLoginDTO)).thenReturn(responsePair);
        ResponseEntity<UserResponseDTO> responseEntity = userController.login(userLoginDTO);
        assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
        UserResponseDTO responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals("dummyToken", responseBody.getToken());
        assertEquals("User Login Successfully", responseBody.getMessage());
        assertEquals(1L, responseBody.getUserId());
        assertEquals("USER", responseBody.getRole());
        verify(userService).login(userLoginDTO);
    }

    @Test
    void loginFailureIncorrectPassword() throws IncorrectPasswordException {
        when(userService.login(userLoginDTO)).thenThrow(new IncorrectPasswordException("Incorrect password provided"));
        Exception exception = assertThrows(IncorrectPasswordException.class, () -> userController.login(userLoginDTO));
        assertEquals("Incorrect password provided", exception.getMessage());
        verify(userService).login(userLoginDTO);
    }
    @Test
    void testGetUserById_UserNotFoundException() throws UserNotFoundException {
        when(userService.getUser(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        // Verifying
        assertThrows(UserNotFoundException.class, () -> userController.getUserById(1L));
    }


    @Test
    void testUpdateUser_UserNotFoundException() throws UserNotFoundException {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(); // Populate with test data
        doThrow(new UserNotFoundException("User not found")).when(userService).updateUser(userUpdateDTO, 1L);

        // Verifying
        assertThrows(UserNotFoundException.class, () -> userController.updateUser(userUpdateDTO, 1L));
    }


    @Test
    void testUpdatePassword_Success() throws UserNotFoundException, InvalidPasswordException {
        PasswordDTO passwordDTO = new PasswordDTO(); // Populate with test data

        // Calling the API
        ResponseEntity<?> response = userController.updatePassword(passwordDTO, 1L);

        // Verifying
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password Updated Successfully", response.getBody());
        verify(userService).updatePassword(passwordDTO, 1L);
    }

    @Test
    void testUpdatePassword_UserNotFoundException() throws UserNotFoundException, InvalidPasswordException {
        PasswordDTO passwordDTO = new PasswordDTO(); // Populate with test data
        doThrow(new UserNotFoundException("User not found")).when(userService).updatePassword(passwordDTO, 1L);

        // Verifying
        assertThrows(UserNotFoundException.class, () -> userController.updatePassword(passwordDTO, 1L));
    }

    @Test
    void testUpdatePassword_InvalidPasswordException() throws UserNotFoundException, InvalidPasswordException {
        PasswordDTO passwordDTO = new PasswordDTO(); // Populate with test data
        doThrow(new InvalidPasswordException("Invalid password")).when(userService).updatePassword(passwordDTO, 1L);

        // Verifying
        assertThrows(InvalidPasswordException.class, () -> userController.updatePassword(passwordDTO, 1L));
    }

    @Test
    void testGetProfileImage_Success() throws UserNotFoundException, IOException {
        byte[] image = new byte[]{1, 2, 3}; // Replace with your actual image data

        when(userService.getUserProfileImage(anyLong())).thenReturn(image);

        // Calling the API
        ResponseEntity<?> response = userController.getProfileImage(1L);

        // Verifying
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertArrayEquals(image, (byte[]) response.getBody());
    }

    @Test
    void testGetProfileImage_UserNotFoundException() throws UserNotFoundException, IOException {
        when(userService.getUserProfileImage(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        // Verifying
        assertThrows(UserNotFoundException.class, () -> userController.getProfileImage(1L));
    }

    @Test
    void testGetProfileImage_IOException() throws UserNotFoundException, IOException {
        when(userService.getUserProfileImage(anyLong())).thenThrow(new IOException("File not found"));

        // Verifying
        assertThrows(IOException.class, () -> userController.getProfileImage(1L));
    }
}