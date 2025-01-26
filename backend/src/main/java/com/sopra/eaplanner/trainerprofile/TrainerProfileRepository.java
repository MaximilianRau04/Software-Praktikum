package com.sopra.eaplanner.trainerprofile;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerProfileRepository extends CrudRepository<TrainerProfile, Long> {

}
