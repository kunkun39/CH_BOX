package com.changhong.faq.view;

import com.changhong.faq.domain.QuestionType;

import java.util.ArrayList;

public class VoteSubmitItem {

    public int itemId; // 标题栏之下的新闻内容id

    public String voteQuestion; // 调查问题
    public QuestionType questiontype;//问题类型
    public ArrayList<String> voteAnswers = new ArrayList<String>(); // 问卷调查选项
}
