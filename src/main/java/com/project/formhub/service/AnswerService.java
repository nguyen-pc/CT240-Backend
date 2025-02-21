package com.project.formhub.service;

import org.springframework.stereotype.Service;

import com.project.formhub.domain.Answer;
import com.project.formhub.domain.Choice;
import com.project.formhub.domain.Question;
import com.project.formhub.domain.Response;
import com.project.formhub.domain.request.ReqAnswerDTO;
import com.project.formhub.repository.AnswerRepository;
import com.project.formhub.repository.ChoiceRepository;
import com.project.formhub.repository.QuestionRepository;
import com.project.formhub.repository.ResponseRepository;
import com.project.formhub.repository.SurveyRepository;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final ChoiceRepository choiceRepository;
    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;

    public AnswerService(AnswerRepository answerRepository, ChoiceRepository choiceRepository,
            QuestionRepository questionRepository, ResponseRepository responseRepository) {
        this.answerRepository = answerRepository;
        this.choiceRepository = choiceRepository;
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;

    }

    public Answer createAnswer(Long responseId, ReqAnswerDTO request) {
        Answer answer = new Answer();

        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu trả lời"));

        answer.setResponse(response);

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi"));

        answer.setQuestion(question);

        if (request.getChoiceId() != null) {
            Choice choice = choiceRepository.findById(request.getChoiceId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lựa chọn"));
            answer.setChoice(choice);
        } else {
            answer.setAnswerText(request.getAnswerText());
        }

        return answerRepository.save(answer);
    }

}
