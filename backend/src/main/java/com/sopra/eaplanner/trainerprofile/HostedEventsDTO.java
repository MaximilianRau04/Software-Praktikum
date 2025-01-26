package com.sopra.eaplanner.trainerprofile;

import com.sopra.eaplanner.event.dtos.RatedEventDTO;

import java.util.List;

public class HostedEventsDTO {

    List<RatedEventDTO> pastEvents;

    List<RatedEventDTO> futureEvents;

    public HostedEventsDTO(List<RatedEventDTO> pastEvents, List<RatedEventDTO> futureEvents){
        this.pastEvents = pastEvents;
        this.futureEvents = futureEvents;
    }

    public List<RatedEventDTO> getPastEvents(){
        return pastEvents;
    }

    public List<RatedEventDTO> getFutureEvents() {
        return futureEvents;
    }

    public void setPastEvents(List<RatedEventDTO> pastEvents){
        this.pastEvents = pastEvents;
    }

    public void setFutureEvents(List<RatedEventDTO> futureEvents){
        this.futureEvents = futureEvents;
    }
}
