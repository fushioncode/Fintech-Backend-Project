package com.fintech.bepc.model.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class Auditable<U> {

    @CreatedBy
    protected U createdBy;

    @CreationTimestamp
    protected LocalDateTime createdOn;

    @LastModifiedBy
    protected U lastModifiedBy;

    @UpdateTimestamp
    protected LocalDateTime updatedOn;
}
