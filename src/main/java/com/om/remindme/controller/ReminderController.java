package com.om.remindme.controller;

import com.om.remindme.dto.ReminderRequest;
import com.om.remindme.dto.ReminderResponse;
import com.om.remindme.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(@Valid @RequestBody ReminderRequest request) {
        ReminderResponse response = reminderService.createReminder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getAllReminders() {
        List<ReminderResponse> reminders = reminderService.getAllReminders();
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderResponse> getReminderById(@PathVariable Long id) {
        ReminderResponse response = reminderService.getReminderById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderResponse> updateReminder(
            @PathVariable Long id,
            @Valid @RequestBody ReminderRequest request) {
        ReminderResponse response = reminderService.updateReminder(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }
}
