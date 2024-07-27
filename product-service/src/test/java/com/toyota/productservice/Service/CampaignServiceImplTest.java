package com.toyota.productservice.Service;

import com.toyota.productservice.Entity.Campaign;
import com.toyota.productservice.Repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceImplTest {

    CampaignService underTest;
    @Mock
    CampaignRepository campaignRepository;

    @BeforeEach
    void setUp() {
        underTest=new CampaignServiceImpl(campaignRepository);
    }

    @Test
    void shouldReturnCampaignById(){
        //given
        int id=1;
        Campaign campaign = new Campaign("%3 sale", 3, true, false);

        when(campaignRepository.findById(id)).thenReturn(Optional.of(campaign));
        //when
        Campaign campaign1=underTest.findById(id);
        //then
        assertThat(campaign1).isEqualTo(campaign);
    }

    @Test
    void shouldThrowExceptionWithInvalidId() {
        //given
        int invalidId=9;

        //when
        //then
        assertThatThrownBy(()->
                underTest.findById(invalidId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("campaign is not found with id " + invalidId);

    }
}