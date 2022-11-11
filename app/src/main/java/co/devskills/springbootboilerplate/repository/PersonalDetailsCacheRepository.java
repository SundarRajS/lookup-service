package co.devskills.springbootboilerplate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.devskills.springbootboilerplate.entity.PersonalDetailsCache;

@Repository
public interface PersonalDetailsCacheRepository extends JpaRepository<PersonalDetailsCache, String> {
}