package com.Elecciones.elections.dto;

public record VoteOut(Long id,
                      String userId,
                      String name,
                      Long optionId,
                      String optionName
                      )
{
}
