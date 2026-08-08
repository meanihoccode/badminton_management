package com.example.java_basic.mapper;

import com.example.java_basic.dto.MatchHistoryResponseDTO;
import com.example.java_basic.entity.MatchParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MatchHistoryMapper {
    @Mapping(source = "match.id", target = "matchId")
    @Mapping(source = "match.session.courtName", target = "courtName")
    @Mapping(source = "match.session.sessionDate", target = "sessionDate")
    @Mapping(source = "match.teamAScore", target = "teamAScore")
    @Mapping(source = "match.teamBScore", target = "teamBScore")
    MatchHistoryResponseDTO toDto(MatchParticipant mp);
}
