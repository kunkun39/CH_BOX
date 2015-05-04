package com.changhong.faq.assember;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changhong.common.utils.StringUtils;
import com.changhong.faq.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class FaqAssember {

    public static AppDescription convertToAppDescription(String json) throws Exception {
        JSONObject o = JSON.parseObject(json);
        String appDescription = o.getString("appDescription");
        return new AppDescription(appDescription);
    }

    public static List<Category> convertToAppCategories(String json) throws Exception {
        JSONObject o = JSON.parseObject(json);
        JSONArray array = o.getJSONArray("categories");

        List<Category> categories = new ArrayList<Category>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            int id = object.getInteger("id");
            String title = object.getString("title");
            categories.add(new Category(id, title));
        }

        return categories;
    }

    public static Examination convertToExamination(String json) throws Exception {
        Examination examination = new Examination();
        JSONObject jExamination = JSON.parseObject(json);

        //handle examination info
        int id = jExamination.getInteger("id");
        String title = jExamination.getString("title");
        String description = jExamination.getString("description");
        examination.setId(id);
        examination.setTitle(title);
        examination.setDescription(description);

        //hanle question
        JSONArray jQuestions = jExamination.getJSONArray("questions");
        for (int i = 0; i < jQuestions.size(); i++) {
            JSONObject jQuestion = jQuestions.getJSONObject(i);
            int sequence = jQuestion.getInteger("sequence");
            String questionTitle = jQuestion.getString("title");
            String questionType = jQuestion.getString("questionType");
            Question question = new Question();
            question.setSequence(sequence);
            question.setTitle(questionTitle);
            question.setQuestionType(QuestionType.valueOf(questionType));

            //handle answer
            JSONArray jAnswers = jQuestion.getJSONArray("answers");
            for (int j = 0; j < jAnswers.size(); j++) {
                JSONObject jAnswer = jAnswers.getJSONObject(j);
                String result = jAnswer.getString("result");
                String[] tokens = StringUtils.delimitedListToStringArray(result, ":");
                Answer answer = new Answer();
                answer.setSequence(tokens[0]);
                answer.setResult(tokens[1]);
                question.addAnswer(answer);
            }

            examination.addQuestion(question);
        }
        return examination;
    }
}
