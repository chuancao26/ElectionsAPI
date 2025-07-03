package com.Elecciones.elections.service;

import com.Elecciones.elections.domain.Option;
import com.Elecciones.elections.domain.VotingEvent;
import com.Elecciones.elections.repository.OptionRepository;
import com.Elecciones.elections.repository.VotingEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class OptionService
{
    
    private final OptionRepository optionRepository;
    private final VotingEventRepository votingEventRepository;
    
    private final Logger log = LoggerFactory.getLogger(OptionService.class);
    
    public OptionService(OptionRepository optionRepository, VotingEventRepository votingEventRepository) {
        this.optionRepository = optionRepository;
        this.votingEventRepository = votingEventRepository;
    }
    
    public Iterable<Option> getAllOptions() {
        this.log.info("Get all options");
        return this.optionRepository.findAll();
    }
    
    private Option existOptionById(Long id) {
        Option option = this.optionRepository.findById(id).orElse(null);
        if (option == null) {
            throw new RuntimeException("Option does not exist with ID: " + id);
        }
        return option;
    }
    
    public Option getOptionById(Long id) {
        return this.existOptionById(id);
    }
    
    public Option createOption(Option option, String votingEventId) {
        this.log.info("Create option with label: {}", option.getLabel());
        
        VotingEvent votingEvent = this.votingEventRepository.findById(votingEventId)
                .orElseThrow(() -> new RuntimeException("Voting event not found with ID: " + votingEventId));
        
        option.setVotingEvent(votingEvent);
        
        return this.optionRepository.save(option);
    }
    
    public Option patchOption(Long id, Option patch) {
        Option option = this.existOptionById(id);
        
        if (patch.getLabel() != null) {
            option.setLabel(patch.getLabel());
        }
        return this.optionRepository.save(option);
    }
    
    public Option putOption(Long id, Option putOption, String votingEventId) {
        Option option = this.existOptionById(id);
        
        VotingEvent votingEvent = this.votingEventRepository.findById(votingEventId)
                .orElseThrow(() -> new RuntimeException("Voting event not found with ID: " + votingEventId));
        
        putOption.setId(id);
        putOption.setCreatedAt(option.getCreatedAt());
        putOption.setVotingEvent(votingEvent);
        
        return this.optionRepository.save(putOption);
    }
    public void deleteOption(Long id) {
        Option option = this.existOptionById(id);
        this.optionRepository.delete(option);
        this.log.info("Deleted option with id {}", id);
    }
}
