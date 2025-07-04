package com.Elecciones.elections.service;

import com.Elecciones.elections.domain.Option;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.dto.OptionInput;
import com.Elecciones.elections.repository.OptionRepository;
import com.Elecciones.elections.repository.VotingEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OptionService
{
    
    private final OptionRepository optionRepository;
    private final VotingEventService votingEventService;
    private final Logger log = LoggerFactory.getLogger(OptionService.class);
    
    public OptionService(OptionRepository optionRepository, VotingEventService votingEventService) {
        this.optionRepository = optionRepository;
        this.votingEventService = votingEventService;
    }
    
    public List<Option> getOptionByVoteEvent(String eventId)
    {
        VotingEvent votingEvent = votingEventService.getVotingEventById(eventId);
        return optionRepository.findByVotingEvent(votingEvent);
    }
    
    public Option createOption(OptionInput optionInput, String votingEventId)
    {
        VotingEvent votingEvent = this.votingEventService.getVotingEventById(votingEventId);
        
        Option option = new Option(optionInput);
        option.setVotingEvent(votingEvent);
        
        return this.optionRepository.save(option);
    }
    
    public void deleteOption(Long id) {
        Option option = this.getOptionById(id);
        this.optionRepository.delete(option);
    }
    
    
    
    
    
    
    public Option patchOption(Long id, Option patch) {
        Option option = this.getOptionById(id);
        
        if (patch.getLabel() != null) {
            option.setLabel(patch.getLabel());
        }
        return this.optionRepository.save(option);
    }
    
    public Option putOption(Long id, Option putOption, String votingEventId) {
        Option option = this.getOptionById(id);
        
        VotingEvent votingEvent = this.votingEventService.getVotingEventById(votingEventId);
        
        putOption.setId(id);
        putOption.setCreatedAt(option.getCreatedAt());
        putOption.setVotingEvent(votingEvent);
        
        return this.optionRepository.save(putOption);
    }
    public Iterable<Option> getAllOptions() {
        this.log.info("Get all options");
        return this.optionRepository.findAll();
    }
    public Option getOptionById(Long id) {
        Option option = this.optionRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Option does not exist with ID: " + id)
        );
        return option;
    }
}
