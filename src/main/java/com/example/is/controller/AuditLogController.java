package com.example.is.controller;

import com.example.is.model.AuditLog;
import com.example.is.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditlogs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // 🔹 Get all audit logs
    @GetMapping
    public List<AuditLog> getAllLogs() {
        System.out.println("..............");
        return auditLogRepository.findAll();
    }

    // 🔹 Filter logs by username (optional)
    @GetMapping("/user/{username}")
    public List<AuditLog> getLogsByUser(@PathVariable String username) {
        return auditLogRepository.findByUsername(username);
    }
}
