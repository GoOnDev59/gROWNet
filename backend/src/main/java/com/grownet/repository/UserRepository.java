
package com.grownet.repository;

import com.grownet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByRoleAndApprovedFalse(User.Role role);

    List<User> findByRoleAndApprovedTrue(User.Role role);
}
