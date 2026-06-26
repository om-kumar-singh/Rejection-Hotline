package com.om.remindme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime reminderTime;
    private boolean completed;
}
