package com.Elecciones.elections.controller;

import com.Elecciones.elections.service.VoteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vote")
public class VoteController
{
    private final VoteService voteService;
    
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }
    
}
