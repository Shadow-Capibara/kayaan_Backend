package com.kayaan.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kayaan.content.entity.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {
}


