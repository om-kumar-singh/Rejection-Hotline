package com.om.remindme.service;

import com.om.remindme.dto.ReminderRequest;
import com.om.remindme.dto.ReminderResponse;
import com.om.remindme.entity.Reminder;
import com.om.remindme.exception.ResourceNotFoundException;
import com.om.remindme.repository.ReminderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public ReminderResponse createReminder(ReminderRequest request) {
        Reminder reminder = toEntity(request);
        Reminder savedReminder = reminderRepository.save(reminder);
        return toResponse(savedReminder);
    }

    public List<ReminderResponse> getAllReminders() {
        return reminderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ReminderResponse getReminderById(Long id) {
        Reminder reminder = findReminderById(id);
        return toResponse(reminder);
    }

    public ReminderResponse updateReminder(Long id, ReminderRequest request) {
        Reminder reminder = findReminderById(id);
        reminder.setTitle(request.getTitle());
        reminder.setDescription(request.getDescription());
        reminder.setReminderTime(request.getReminderTime());
        Reminder updatedReminder = reminderRepository.save(reminder);
        return toResponse(updatedReminder);
    }

    public void deleteReminder(Long id) {
        Reminder reminder = findReminderById(id);
        reminderRepository.delete(reminder);
    }

    private Reminder findReminderById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
    }

    private Reminder toEntity(ReminderRequest request) {
        return Reminder.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .reminderTime(request.getReminderTime())
                .completed(false)
                .build();
    }

    private ReminderResponse toResponse(Reminder reminder) {
        return ReminderResponse.builder()
                .id(reminder.getId())
                .title(reminder.getTitle())
                .description(reminder.getDescription())
                .reminderTime(reminder.getReminderTime())
                .completed(reminder.isCompleted())
                .build();
    }
}
