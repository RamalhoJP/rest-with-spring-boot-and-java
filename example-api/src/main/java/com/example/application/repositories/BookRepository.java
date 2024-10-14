package com.example.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.application.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {}