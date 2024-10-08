package com.user.userservice.repository;

import com.user.userservice.dto.UserEmailDTO;
import com.user.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserEmailRepository extends JpaRepository<User, Long> {
    @Query("SELECT new com.user.userservice.dto.UserEmailDTO(u.id, u.email) FROM User u")
    List<UserEmailDTO> findAllEmails();
}
