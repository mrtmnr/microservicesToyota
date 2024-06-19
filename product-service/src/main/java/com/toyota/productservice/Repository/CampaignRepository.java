package com.toyota.productservice.Repository;


import com.toyota.productservice.Entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign,Integer> {
}
