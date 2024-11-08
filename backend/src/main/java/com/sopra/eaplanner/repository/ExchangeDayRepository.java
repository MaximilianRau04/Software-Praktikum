package com.sopra.eaplanner.repository;

import com.sopra.eaplanner.model.ExchangeDay;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeDayRepository extends CrudRepository<ExchangeDay, Long> {

}
