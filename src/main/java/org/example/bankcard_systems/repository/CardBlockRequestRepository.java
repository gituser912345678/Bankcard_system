package org.example.bankcard_systems.repository;

import org.example.bankcard_systems.model.CardBlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {
}