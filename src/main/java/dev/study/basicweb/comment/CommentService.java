package dev.study.basicweb.comment;

import dev.study.basicweb.DataNotFoundException;
import dev.study.basicweb.answer.Answer;
import dev.study.basicweb.answer.AnswerRepository;
import dev.study.basicweb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // 답변에 댓글 생성
    public Comment create(Answer answer, SiteUser author, String content) {
        Comment c = new Comment();
        c.setContent(content);
        c.setCreateDate(LocalDateTime.now());
        c.setAnswer(answer);
        c.setAuthor(author);
        return this.commentRepository.save(c);
    }

    // 댓글 조회 (삭제 전 존재 확인용)
    public Comment getComment(Integer id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    // 댓글 삭제
    public void delete(int commentId) {
        this.commentRepository.deleteById(commentId);
    }
}
