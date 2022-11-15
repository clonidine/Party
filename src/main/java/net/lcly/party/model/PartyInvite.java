package net.lcly.party.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class PartyInvite {

    private final UUID inviter;
    private final UUID invited;
    private final Date moment = new Date();
}
