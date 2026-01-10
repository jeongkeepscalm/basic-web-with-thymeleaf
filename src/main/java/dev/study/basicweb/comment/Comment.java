package dev.study.basicweb.comment;

import dev.study.basicweb.answer.Answer;
import dev.study.basicweb.question.Question;
import dev.study.basicweb.user.SiteUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createDate=" + createDate +
                ", author=" + author +
                ", answer=" + answer +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    @NotEmpty
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private SiteUser author;

    @ManyToOne
    private Answer answer;

    @ManyToOne
    private Question question;


}
