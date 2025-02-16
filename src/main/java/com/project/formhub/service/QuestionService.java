package com.project.formhub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.formhub.domain.Choice;
import com.project.formhub.domain.Question;
import com.project.formhub.domain.Survey;
import com.project.formhub.domain.request.ReqQuestionDTO;
import com.project.formhub.repository.ChoiceRepository;
import com.project.formhub.repository.QuestionRepository;
import com.project.formhub.repository.SurveyRepository;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final SurveyService surveyService;
    private final ChoiceRepository choiceRepository;
    private final SurveyRepository surveyRepository;

    public QuestionService(QuestionRepository questionRepository, SurveyService surveyService,
            ChoiceRepository choiceRepository, SurveyRepository surveyRepository) {
        this.questionRepository = questionRepository;
        this.surveyService = surveyService;
        this.choiceRepository = choiceRepository;
        this.surveyRepository = surveyRepository;
    }

    public Question createQuestion(ReqQuestionDTO question, long surveyId) {
        Survey survey = surveyService.getSurveyById(surveyId);

        Question newQuestion = new Question();
        newQuestion.setSurvey(survey);
        newQuestion.setQuestionName(question.getQuestionText());
        newQuestion.setQuestionType(question.getQuestionType());

        Question resQuestion = questionRepository.save(newQuestion);

        // Luu danh sách các lựa chọn câu hỏi nếu có
        if (question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE
                || question.getQuestionType() == Question.QuestionType.CHECKBOX ||
                question.getQuestionType() == Question.QuestionType.DROPDOWN) {

            List<Choice> choices = question.getChoices().stream().map(choiceDTO -> {
                Choice choice = new Choice();
                choice.setQuestion(resQuestion);
                choice.setChoiceText(choiceDTO.getChoiceText());
                return choice;
            }).collect(Collectors.toList());

            choiceRepository.saveAll(choices);
        }

        return resQuestion;
    }

    public Question updateQuestion(long surveyId, long questionId, ReqQuestionDTO request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi"));

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Form không tồn tại"));
        // Cập nhật dữ liệu
        question.setSurvey(survey);
        question.setQuestionName(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());

        // Xóa tất cả lựa chọn cũ (nếu có)
        if (request.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE ||
                request.getQuestionType() == Question.QuestionType.CHECKBOX ||
                request.getQuestionType() == Question.QuestionType.DROPDOWN) {

            // delete choice
            // choiceRepository.deleteByQuestion_Id(questionId);

            List<Choice> choices = request.getChoices().stream().map(choiceDTO -> {
                Choice choice = new Choice();
                choice.setQuestion(question);
                choice.setChoiceText(choiceDTO.getChoiceText());
                return choice;
            }).collect(Collectors.toList());

            choiceRepository.saveAll(choices);
        }

        return questionRepository.save(question);
    }

    public void deleteQuestion(long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow();

        // choiceRepository.deleteByQuestionId(questionId);
        questionRepository.delete(question);
    }

}
