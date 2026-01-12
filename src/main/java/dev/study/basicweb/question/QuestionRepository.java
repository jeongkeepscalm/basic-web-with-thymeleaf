package dev.study.basicweb.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    List<Question> findBySubject(String subject);
    List<Question> findBySubjectLike(String subject);

    // 페이징
    Page<Question> findAll(Pageable pageable);

    // 검색 + 페이징
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);

    // JPQL
    @Query("select "
            + "distinct q "
            + "from Question q "
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Answer a on a.question=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% ")
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

    @Modifying // INSERT, UPDATE, DELETE 쿼리에 필수
    @Query("update Question q set q.viewCount = q.viewCount + 1 where q.id = :id")
    int updateViewCount(@Param("id") Integer id);





}
