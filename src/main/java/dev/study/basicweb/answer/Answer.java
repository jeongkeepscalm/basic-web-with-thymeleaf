package dev.study.basicweb.answer;

import dev.study.basicweb.comment.Comment;
import dev.study.basicweb.question.Question;
import dev.study.basicweb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    // 답변은 여러 사람이 남길 수 있고,
    // 여러사람이 하나의 답변을 달 수 있다.
    @ManyToMany
    Set<SiteUser> voter;

    // 답변에 댓글
    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

}
