package com.test.tms.repositories;

import com.test.tms.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findUsersByUsernameAndPassword(String username, String password);
    User findUsersByUsername(String username);
}
