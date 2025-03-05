package com.ironnomad.vivid.service;

import com.ironnomad.vivid.entity.Comment;
import com.ironnomad.vivid.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment postComment(Comment comment) {
        return commentRepository.save(comment);
    }
}