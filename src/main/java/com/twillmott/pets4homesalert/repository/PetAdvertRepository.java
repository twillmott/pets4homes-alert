package com.twillmott.pets4homesalert.repository;

import com.twillmott.pets4homesalert.entity.PetAdvertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PetAdvertRepository extends JpaRepository<PetAdvertEntity, UUID> {

    List<PetAdvertEntity> findLatestByOrderByCreatedDesc();

    boolean existsByUrl(String url);

}
