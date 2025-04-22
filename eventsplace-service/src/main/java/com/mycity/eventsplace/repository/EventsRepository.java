package com.mycity.eventsplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.eventsplace.entity.Event;


public interface EventsRepository extends JpaRepository<Event, Long> {

}
