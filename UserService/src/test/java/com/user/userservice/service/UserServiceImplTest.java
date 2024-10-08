package com.user.userservice.service;

import com.nimbusds.jose.util.Pair;
import com.user.userservice.dto.*;
import com.user.userservice.entity.User;
import com.user.userservice.exception.*;
import com.user.userservice.repository.UserRepository;
import com.user.userservice.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final String token = "mocked-token";
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private MultipartFile multipartFile;

    private User user;
    private UserLoginDTO userLoginDTO;
    private UserDisplayDTO userDisplayDTO;
    private UserUpdateDTO userUpdateDTO;
    private PasswordDTO passwordDTO;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encryptedPassword");
        user.setEnabled(true);
        userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("test@example.com");
        userLoginDTO.setPassword("password");
        ReflectionTestUtils.setField(userService, "path", "images/");
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("encodedPassword");
        user.setImage("image.jpg");

        userDisplayDTO = new UserDisplayDTO();
        userDisplayDTO.setFirstName("John");
        userDisplayDTO.setLastName("Doe");

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName("Jane");
        userUpdateDTO.setLastName("Doe");

        passwordDTO = new PasswordDTO();
        passwordDTO.setOldPassword("oldPassword");
        passwordDTO.setNewPassword("newPassword");
        passwordDTO.setConfirmPassword("newPassword");
    }

    @Test
    void testSuccessfulLogin() throws IncorrectPasswordException {
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenService.generateToken(any(Authentication.class))).thenReturn(token);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), user.getAuthorities());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        Pair<String, User> result = userService.login(userLoginDTO);
        assertEquals(token, result.getLeft());
        assertEquals(user, result.getRight());
    }

    @Test
    void testNoUsernameFound() {
        when(userDetailsService.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException("User Not found"));
           Exception exception = assertThrows(UsernameNotFoundException.class, () -> userService.login(userLoginDTO));
        assertEquals("User Not found", exception.getMessage());
    }

    @Test
    void testWrongPassword() {
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(IncorrectPasswordException.class, () -> userService.login(userLoginDTO));
    }

    @Test
    void testTokenGeneration() throws IncorrectPasswordException {
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenService.generateToken(any(Authentication.class))).thenReturn(token);
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), user.getAuthorities());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
        Pair<String, User> result = userService.login(userLoginDTO);
        assertEquals(token, result.getLeft());
    }

    @Test
    public void testGetUser_Success() throws UserNotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), any(Class.class))).thenReturn(userDisplayDTO);

        UserDisplayDTO result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetUser_UserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
        verify(userRepository, times(1)).findById(anyLong());
    }


    @Test
    public void testUpdateUser_UserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userUpdateDTO, 1L));
        verify(userRepository, times(1)).findById(anyLong());
    }


    @Test
    public void testUpdateUserImage_UserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserImage("path/to/images", multipartFile, 1L));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdatePassword_Success() throws UserNotFoundException, InvalidPasswordException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        userService.updatePassword(passwordDTO, 1L);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdatePassword_InvalidPasswordException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userService.updatePassword(passwordDTO, 1L));
    }



    @Test
    public void testGetUserProfileImage_UserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserProfileImage(1L));
    }

    @Test
    public void testGetUserProfileImage_FileNotFoundException() throws UserNotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(FileNotFoundException.class, () -> userService.getUserProfileImage(1L));
    }


    @Test
    public void testGetUserProfileImageUrl_UserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserProfileImageUrl(1L));
    }

    @Test
    public void testGetUserProfileImageUrl_FileNotFoundException() throws UserNotFoundException {
        user.setImage(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(FileNotFoundException.class, () -> userService.getUserProfileImageUrl(1L));
    }
}