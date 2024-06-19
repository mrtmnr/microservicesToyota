package com.toyota.productservice.Service;


import com.toyota.productservice.Entity.Campaign;
import com.toyota.productservice.Repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
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

            throw new RuntimeException("campaign is not found with id " + id);

        }

        return result;
    }
}
