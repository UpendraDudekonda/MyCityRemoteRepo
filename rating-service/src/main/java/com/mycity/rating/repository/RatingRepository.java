package com.mycity.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycity.rating.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating,Long> 
{

}
