package com.project.formhub.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.formhub.domain.Answer;
import com.project.formhub.domain.Response;
import com.project.formhub.domain.Survey;
import com.project.formhub.domain.request.ReqAnswerDTO;
import com.project.formhub.domain.request.ReqResponseDTO;
import com.project.formhub.repository.QuestionRepository;
import com.project.formhub.repository.ResponseRepository;
import com.project.formhub.repository.SurveyRepository;

import jakarta.transaction.Transactional;

@Service
public class ResponseService {
    private final ResponseRepository responseRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final AnswerService answerService;

    public ResponseService(ResponseRepository responseRepository, SurveyRepository surveyRepository,
            QuestionRepository questionRepository, AnswerService answerService) {
        this.responseRepository = responseRepository;
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.answerService = answerService;
    }

    // @Transactional
    public Response saveResponse(Long surveyId, ReqResponseDTO request) {
        // Neu muon dang nhap thong tin tai khoan nguoi dung
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        Response response = new Response();
        response.setSurvey(survey);

        response = responseRepository.save(response);

        // Luu danh sach cau tra loi

        List<Answer> answers = new ArrayList<>();

        for (ReqAnswerDTO reqAnswerDTO : request.getAnswers()) {
            System.out.println(">>>>>>>>>>>>>>>>>>>" + reqAnswerDTO);
            Answer answer = answerService.createAnswer(response.getResponseId(), reqAnswerDTO);
            answer.setResponse(response);
            answers.add(answer);
        }

        response.setAnswers(answers);

        return responseRepository.save(response);

    }

}
