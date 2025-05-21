package com.plataformaEducativa.proyectoestructuradatos.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.plataformaEducativa.proyectoestructuradatos.enums.HelpRequestPriority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpRequest implements Comparable<HelpRequest> {
    private UUID id;
    private String title;
    private String description;
    private String topic;
    private HelpRequestPriority priority;
    private Student requester;
    private Student helper;
    private boolean resolved;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public int compareTo(HelpRequest other) {
        // Compare first by priority (higher priority first)
        int priorityCompare = other.priority.ordinal() - this.priority.ordinal();
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        // If same priority, older requests come first
        return this.createdAt.compareTo(other.createdAt);
    }
}
