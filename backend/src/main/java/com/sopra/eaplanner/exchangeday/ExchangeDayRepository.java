package com.sopra.eaplanner.exchangeday;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeDayRepository extends CrudRepository<ExchangeDay, Long> {

}
