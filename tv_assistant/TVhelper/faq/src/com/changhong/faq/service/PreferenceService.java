package com.changhong.faq.service;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.changhong.faq.domain.Answer;
import com.changhong.faq.domain.Examination;
import com.changhong.faq.domain.Question;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Jack Wang
 * 
 * Revise  by xiebo
 */

public class PreferenceService {
	private static final String TAG = "PreferenceService";
    private Context context;

    public PreferenceService(Context context) {
        this.context = context;
    }

    public void saveAnswers(Examination examination, String examinationId, String questionId, Set<String> answers) {
    	
        SharedPreferences preferences = context.getSharedPreferences("changhong_wenjuan", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        List<Question> questions = examination.getQuestions();
        for (Question question : questions){
        	 if(question.getQuestionType().name().equals("SINGLE")||question.getQuestionType().name().equals("MUTI")){
        	        editor.putStringSet(examinationId + "_" + questionId, answers);
        	        Log.i(TAG, "questionId= " + questionId + "answers" + answers.toString());
        	        editor.commit();
        	   }else{
        		   editor.putStringSet(examinationId + "_" + questionId, answers);
        		   Log.i(TAG, "objquestionId= " + questionId + "answers" + answers.toString());
        		   editor.commit();
        	   }
        	 
        }
       
    }
    /**
     * 得到某一张问卷，第几个问题的答案
     * @param examination
     * @param examinationId
     * @param questionId
     * @return
     */
	public Set<String> getAnswers(Examination examination, String examinationId, String questionId) {
        
		List<Question> questions = examination.getQuestions();
		
		SharedPreferences preferences = context.getSharedPreferences("changhong_wenjuan", Context.MODE_PRIVATE);
		for (Question question : questions){
			if(question.getQuestionType().name().equals("SINGLE")||question.getQuestionType().name().equals("MUTI")){
				 Set<String> answers = preferences.getStringSet(examinationId + "_" + questionId, new HashSet<String>());
			        return answers;
			}else{
				Set<String> result = preferences.getStringSet(examinationId+"_"+questionId,new HashSet<String>() );
				return result;
			}
		}
       return null;
    }

    /**
     * 得到选择题答案，主要用于判断是否答完题
     * @param examination
     * @return
     */
	public String getAllSecAnswers(Examination examination) {
        boolean answerAll = true;
        int examinationId = examination.getId();
        List<Question> questions = examination.getQuestions();
        
        SharedPreferences preferences = context.getSharedPreferences("changhong_wenjuan", Context.MODE_PRIVATE);
        StringBuffer buffer = new StringBuffer();
        
        for (Question question : questions) {
        	Log.i(TAG, "question.getQuestionType=" + question.getQuestionType());
        	if(question.getQuestionType().name().equals("SINGLE")||question.getQuestionType().name().equals("MUTI")){
        		 Set<String> answers = preferences.getStringSet(examinationId + "_" + (question.getSequence() - 1), new HashSet<String>());
                 Log.i(TAG, answers.toString());
                  
                  if (answers == null || answers.isEmpty()) {
                      answerAll = false;
                      Log.i(TAG, "answerAll" + answerAll);
                  }
                  buffer.append(Answer.getAnswer(answers) + "|");
                  Log.i(TAG, "answerbuffer=" + buffer);
        	}
           
       }

        if (!answerAll) {
            return "";
        }
        return buffer.toString();
    }
	/**
	 * 得到所有的答案，包括主客观题
	 * @param examination
	 * @return
	 */
	public String getAllAnswers(Examination examination) {
        int examinationId = examination.getId();
        List<Question> questions = examination.getQuestions();
        SharedPreferences preferences = context.getSharedPreferences("changhong_wenjuan", Context.MODE_PRIVATE);
        StringBuffer buffer = new StringBuffer();
        for (Question question : questions) {
    		 Set<String> answers = preferences.getStringSet(examinationId + "_" + (question.getSequence() - 1), new HashSet<String>());
              if(question.getQuestionType().name().equals("SINGLE")||question.getQuestionType().name().equals("MUTI")){
            	  buffer.append(Answer.getAnswer(answers) + "|");
              }else{
            	  if(answers.isEmpty()){
            		  buffer.append(" "+"|");   
            	  }else{
            		  Iterator i = answers.iterator();
                	  String temp="";
                	  while(i.hasNext())
                	  { 
                		 temp+=i.next()+",";
                	  }
                	  buffer.append(temp.substring(0,temp.length()-1)+"|");
            	  }
              }
              Log.i(TAG, "answerbuffer=" + buffer);
        }
         
        return buffer.substring(0, buffer.length()-1).toString();
	}

    public void cleanAllAnswers(Examination examination) {
        int examinationId = examination.getId();
        List<Question> questions = examination.getQuestions();

        SharedPreferences preferences = context.getSharedPreferences("changhong_wenjuan", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for (Question question : questions) {
            editor.remove(examinationId + "_" + (question.getSequence() - 1));
        }
        editor.commit();
    }

    /**
     * 提取问题答案用作显示
     * @param examination 问卷
     * @return 
     */
	public String getAllAnswersShow(Examination examination) {
        int examinationId = examination.getId();
        List<Question> questions = examination.getQuestions();

        SharedPreferences preferences = context.getSharedPreferences("changhong_wenjuan", Context.MODE_PRIVATE);
        StringBuffer buffer = new StringBuffer();

        for (Question question : questions) {
        	if(question.getQuestionType().name().equals("SINGLE")||question.getQuestionType().name().equals("MUTI")){
        		 Set<String> answers = preferences.getStringSet(examinationId + "_" + (question.getSequence() - 1), new HashSet<String>());
        		 Log.i(TAG, "question.getSequence()=" + question.getSequence());
        		 Log.i(TAG, "answers=" + answers.toString());
        		 buffer.append(question.getAnswerShow(answers));
        	}else{
        		Set<String> result = preferences.getStringSet(examinationId+"_"+(question.getSequence() - 1),new HashSet<String>() );
        		buffer.append("问题" + question.getSequence() + ": " + question.getTitle() + "\n");
        		Log.i(TAG, "question.getSequence()=" + question.getSequence());
        		Log.i(TAG, "result=" + result);
        		if(!result.isEmpty()){
        			boolean flag = result.isEmpty();
        			Log.i(TAG, String.valueOf(flag));
            		Iterator i = result.iterator();
            		String temp="";
            		while(i.hasNext())
            		{ 
            		   temp+=i.next()+",";
            		}
            		buffer.append("你的答案："+temp.substring(0,temp.length()-1)+ "\n"+"\n");
        		}else{
        			buffer.append("你的答案："+"<空>"+ "\n"+"\n");
        		}
        			
        	
        		
        	}
 
        }

        return buffer.toString();
    }

	

	

	
}
