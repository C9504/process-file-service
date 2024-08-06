package com.georeference.appregca.repositories;

import com.georeference.appregca.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.nuIdUser = :document")
    User findByDocument(String document);
}
