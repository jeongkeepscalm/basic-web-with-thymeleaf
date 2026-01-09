package dev.study.basicweb;

import dev.study.basicweb.answer.Answer;
import dev.study.basicweb.answer.AnswerRepository;
import dev.study.basicweb.question.Question;
import dev.study.basicweb.question.QuestionRepository;
import dev.study.basicweb.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BasicWebApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    void testJpa() {
        Question q1 = new Question();
        q1.setSubject("what is this?");
        q1.setContent("-");
        q1.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q1);

        Question q2 = new Question();
        q2.setSubject("what is that?");
        q2.setContent("- - - ");
        q2.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q2);
    }

    @Test
    void findall() {
        List<Question> all = this.questionRepository.findAll();
        assertEquals(6, all.size());

        Question question = all.get(0);
        assertEquals("what is this?", question.getSubject());
    }

    @Test
    void findById() {
        Optional<Question> oq = this.questionRepository.findById(1);
        if (oq.isPresent()) {
            Question question = oq.get();
            assertEquals("-", question.getContent());
        }
    }

    @Test
    void findBySubject() {
        List<Question> qL = this.questionRepository.findBySubject("what is this?");
        assertEquals(1, qL.get(0).getId());
    }

    @Test
    void findBySubjectLike() {
        List<Question> qL = this.questionRepository.findBySubjectLike("what%");
        assertEquals(1, qL.get(0).getId());
    }

    @Test
    void update() {
        Optional<Question> oQ = this.questionRepository.findById(2);
        oQ.ifPresent(question -> question.setSubject("updated Subject 1"));
        this.questionRepository.save(oQ.get());

        Optional<Question> q = this.questionRepository.findById(2);
        q.ifPresent(question -> assertEquals("updated Subject 1", question.getSubject()));
    }

    @Test
    void insertAnswer() {
        Optional<Question> oq = this.questionRepository.findById(3);
        assertTrue(oq.isPresent());
        Question q = oq.get();

        Answer a = new Answer();
        a.setContent("네 자동으로 생성됩니다.");
        a.setQuestion(q);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
        a.setCreateDate(LocalDateTime.now());
        this.answerRepository.save(a);

    }

    /**
     *  @OneToMany
     *      기본 fetch 전략은 FetchType.LAZY 이다.
     *      @OneToMany: AnswerList 는 가짜 객체 상태로 만들고 필요할 때 DB 접근해서 가져오는 전략.
     *      findById 실행 후 DB 접속은 끊기며 getAnswerList 실행 LazyInitializationException 오류 발생.
     *
     *  해결방안
     *      1. @Transactional : 메소드 종료될 때까지 DB 세션 유지.
     *      2. fetch=FetchType.EAGER 명시 ( 성능 면에서 치명적인 문제가 발생 )
     */
    @Test
    @Transactional
    void getAnswerList() {
        Optional<Question> oq = this.questionRepository.findById(3);
        assertTrue(oq.isPresent());
        Question q = oq.get();

        List<Answer> answerList = q.getAnswerList();

        assertEquals(1, answerList.size());
        assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
    }

    @Test
    void insertBulkData() {
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("test data : [%03d]", i);
            String content = "no content";
            questionService.create(subject, content);
        }
    }

    // 선택 삭제
    @Test
    void deleteQuestionsChosen() {
        List<Question> allById = questionRepository.findAllById(List.of(3, 6, 7));
        questionRepository.deleteAll(allById);
        assertEquals(300, questionRepository.findAll().size());
    }

}
