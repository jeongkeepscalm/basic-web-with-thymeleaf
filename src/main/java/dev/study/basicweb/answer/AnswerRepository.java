package dev.study.basicweb.answer;

import dev.study.basicweb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    @Query(
            "select a " +
                    "from Answer a " +
                    "left join a.voter v " +
                    "where a.question = :question " +
                    "group by a " +
                    "order by count(v) desc, a.createDate"
    )
    Page<Answer> findAllByQuestion(@RequestParam("question") Question question, Pageable pageable);

}
