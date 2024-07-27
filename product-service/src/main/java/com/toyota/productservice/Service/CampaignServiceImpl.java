package com.toyota.productservice.Service;


import com.toyota.productservice.Entity.Campaign;
import com.toyota.productservice.Repository.CampaignRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CampaignServiceImpl implements CampaignService{

    private CampaignRepository campaignRepository;

    @Autowired
    public CampaignServiceImpl(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public Campaign findById(int id) {

        Campaign result;

        Optional<Campaign> campaign = campaignRepository.findById(id);

        if (campaign.isPresent()) {

            result = campaign.get();

        } else {

            log.error("campaign is not found");
            throw new RuntimeException("campaign is not found with id " + id);
        }

        return result;
    }
}
