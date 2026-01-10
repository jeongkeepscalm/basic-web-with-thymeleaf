package dev.study.basicweb.comment;

import dev.study.basicweb.answer.Answer;
import dev.study.basicweb.answer.AnswerService;
import dev.study.basicweb.user.SiteUser;
import dev.study.basicweb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final AnswerService answerService;
    private final UserService userService;

    @RequestMapping("/create/answer/{answerId}")
    public String create(@PathVariable("answerId") int answerId, Comment comment, Principal principal){

        Answer answer = answerService.getAnswer(answerId);
        SiteUser user = userService.getUser(principal.getName());
        commentService.create(answer, user, comment.getContent());

        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @RequestMapping("/delete/{commentId}")
    public String remove(@PathVariable("commentId") int id, Principal principal){

        Comment comment = commentService.getComment(id);
        int questionId = comment.getAnswer().getQuestion().getId();

        boolean isUserAuthenticated = comment.getAuthor().getUsername().equals(principal.getName());
        if (!isUserAuthenticated){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        commentService.delete(id);

        return String.format("redirect:/question/detail/%s", questionId);
    }

}
