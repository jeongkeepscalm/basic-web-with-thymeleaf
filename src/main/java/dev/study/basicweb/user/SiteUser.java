package dev.study.basicweb.user;

import dev.study.basicweb.answer.Answer;
import dev.study.basicweb.comment.Comment;
import dev.study.basicweb.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    // 내가 쓴 질문 리스트
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Question> questionList;

    // 내가 쓴 답변 리스트
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    // 내가 쓴 댓글 리스트
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

}
