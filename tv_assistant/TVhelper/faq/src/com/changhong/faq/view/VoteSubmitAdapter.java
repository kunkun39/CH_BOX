package com.changhong.faq.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.changhong.faq.R;
import com.changhong.faq.activity.QuestionListActivity;
import com.changhong.faq.activity.VoteSubmitActivity;
import com.changhong.faq.domain.Examination;
import com.changhong.faq.domain.Question;
import com.changhong.faq.domain.QuestionType;
import com.changhong.faq.service.HttpClientService;
import com.changhong.faq.service.PreferenceService;

import java.util.*;

public class VoteSubmitAdapter extends PagerAdapter {

    private final String TAG = "VoteSubmitAdapter";
    private VoteSubmitActivity mContext;

    /**
     * 保存设置的Service
     */
    private PreferenceService preferenceService;
    /**
     * 总共问题的数目
     */
    private int totalQuestions;

    /**
     * 现在正在进行的调查问卷
     */
    private Examination examination;

    /**
     * 传递过来的页面view的集合
     */
    private List<View> viewItems;

    /**
     * 每个item的页面view
     */
    private View convertView;

    /**
     * 传递过来的所有数据
     */
    private ArrayList<VoteSubmitItem> dataItems;

    /**
     * 题目选项的adapter
     */
    private VoteSubmitListAdapter listAdapter;

    /**
     * ViewHolder
     */
    private QuestionViewHolder holder = null;

    /**
     * 主线程处理
     */
    private Handler handler;
    /*
    问题类型
    */
    private QuestionType questiontype;


    public VoteSubmitAdapter(VoteSubmitActivity context, Examination examination, List<View> viewItems, ArrayList<VoteSubmitItem> dataItems) {
        /**
         * 初始化系统组件
         */
        mContext = context;
        this.preferenceService = new PreferenceService(context);
        this.totalQuestions = examination.getQuestions().size();
        this.examination = examination;
        this.viewItems = viewItems;
        this.dataItems = dataItems;

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewItems.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int questionIndex) {
        holder = new QuestionViewHolder();
        convertView = viewItems.get(questionIndex);
        holder.title = (TextView) convertView.findViewById(R.id.vote_submit_title);
        holder.question = (TextView) convertView.findViewById(R.id.vote_submit_question);
        holder.listView = (ListView) convertView.findViewById(R.id.vote_submit_listview);
        holder.previousBtn = (LinearLayout) convertView.findViewById(R.id.vote_submit_linear_previous);
        holder.nextBtn = (LinearLayout) convertView.findViewById(R.id.vote_submit_linear_next);
        holder.nextText = (TextView) convertView.findViewById(R.id.vote_submit_next_text);
        holder.previousText = (TextView) convertView.findViewById(R.id.vote_submit_previous_text);
        holder.nextImage = (ImageView) convertView.findViewById(R.id.vote_submit_next_image);
        //编辑框
        holder.editText = (EditText) convertView.findViewById(R.id.vote_submit_editview);

        questiontype = dataItems.get(questionIndex).questiontype;
        String questiontype_string = null;
        if (questiontype.equals(QuestionType.SINGLE)) {
            questiontype_string = "单选";
        } else if (questiontype.equals(QuestionType.MUTI)) {

            questiontype_string = "多选";
        } else if (questiontype.equals(QuestionType.OBJECTIVE)) {
            questiontype_string = "主观题";
        }
        holder.title.setText("总共" + totalQuestions + "题，当前在" + (questionIndex + 1) + "题" + "(" + questiontype_string + ")");

        listAdapter = new VoteSubmitListAdapter(mContext, examination, examination.getId(), questiontype, questionIndex, dataItems.get(questionIndex).voteAnswers);
        holder.question.setText(dataItems.get(questionIndex).voteQuestion);
        questiontype = dataItems.get(questionIndex).questiontype;
        holder.listView.setDividerHeight(0);
        holder.listView.setAdapter(listAdapter);
        //xb abb 
        if (questiontype.equals(QuestionType.OBJECTIVE)) {
            holder.editText.setVisibility(0);
            Set<String> Objectiveset = preferenceService.getAnswers(examination, String.valueOf(examination.getId()), String.valueOf(questionIndex));
            if (!Objectiveset.isEmpty()) {
                Iterator i = Objectiveset.iterator();
                String temp = "";
                while (i.hasNext()) {
                    temp += i.next() + ",";
                }
                temp = temp.substring(0, temp.length() - 1);
                holder.editText.setText(temp);
            }

        }

        if (questiontype.equals(QuestionType.SINGLE)) {
            holder.listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        } else {
            holder.listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
        holder.listView.setOnItemClickListener(new ListViewOnClickListener(listAdapter));

        // 第一页隐藏"上一步"按钮
        if (questionIndex == 0) {
            holder.previousText.setText("返回");
            holder.previousBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, QuestionListActivity.class);
                    if (QuestionListActivity.sp != null && QuestionListActivity.music > 0) {
                        QuestionListActivity.sp.play(QuestionListActivity.music, 1, 1, 0, 0, 1);
                    }
                    mContext.startActivity(intent);
                }

            });
        } else {
            holder.previousBtn.setVisibility(View.VISIBLE);

            holder.previousBtn.setOnClickListener(new LinearOnClickListener(questionIndex - 1, holder.editText, questionIndex));
        }

        // 最后一页修改"下一步"按钮文字
        if (questionIndex == viewItems.size() - 1) {
            holder.nextText.setText("提交");
            holder.nextImage.setImageResource(R.drawable.vote_submit_finish);
        }

        holder.nextBtn.setOnClickListener(new LinearOnClickListener(questionIndex + 1, holder.editText, questionIndex));

        container.addView(viewItems.get(questionIndex));
        return viewItems.get(questionIndex);
    }

    /**
     * 自定义listview的item点击事件
     */
    class ListViewOnClickListener implements OnItemClickListener {

        private VoteSubmitListAdapter mListAdapter;

        public ListViewOnClickListener(VoteSubmitListAdapter VoteSubmlistAdapteritListAdapter) {
            mListAdapter = listAdapter;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int answerIndex, long id) {
            VoteSubmitListAdapter.AnswerViewHolder answerItem = (VoteSubmitListAdapter.AnswerViewHolder) view.getTag();

            /**
             * 设置更新选中项图片和文本变化
             */
            int currentQuestionIndex = Integer.valueOf(String.valueOf(answerItem.question_index.getText()));
            Set<String> answerSelected = preferenceService.getAnswers(examination, String.valueOf(examination.getId()), String.valueOf(currentQuestionIndex));

            if (answerSelected == null) {
                answerSelected = new HashSet<String>();
            }
            String answerIndexStr = String.valueOf(answerIndex);
            if (!answerSelected.contains(answerIndexStr)) {
                if ((answerItem.question_type.getText()).equals("SINGLE")) {
                    answerSelected.clear();
                }
                answerSelected.add(answerIndexStr);
            } else {
                answerSelected.remove(answerIndexStr);


            }
            answerItem.select_text.toggle();
            /**
             * 缓存选择题答案
             */

            preferenceService.saveAnswers(examination, String.valueOf(examination.getId()), String.valueOf(currentQuestionIndex), answerSelected);

            if ((answerItem.question_type.getText().equals("SINGLE")) && (answerSelected.contains(answerIndexStr))) {
                mContext.setCurrentView(currentQuestionIndex + 1);
            }


        }
    }

    /**
     * @author wisdomhu 设置上一步和下一步按钮监听
     */
    class LinearOnClickListener implements OnClickListener {

        private int mPosition;
        private EditText et;
        private int QuestionIndex;

        public LinearOnClickListener(int position, EditText editText, int questionIndex) {
            mPosition = position;
            et = editText;
            QuestionIndex = questionIndex;
        }

        @Override
        public void onClick(View v) {
            /**
             * 提交答案，清楚零时记录的答案
             */

            //xb abb
            List<Question> questions = examination.getQuestions();
            Question curques = questions.get(QuestionIndex);
            if (curques.getQuestionType().name().equals("OBJECTIVE")) {
                Set<String> Objectiveset = new HashSet<String>();
                String ObjectiveString = et.getText().toString();
                Log.i(TAG, "ObjectiveString" + ObjectiveString);
                if (!ObjectiveString.isEmpty()) {
                    Log.i(TAG, "preferenceService------>");
                    Objectiveset.add(ObjectiveString);
                    preferenceService.saveAnswers(examination, String.valueOf(examination.getId()), String.valueOf(QuestionIndex), Objectiveset);
                }

            }
            if (mPosition == viewItems.size()) {
                /**
                 * 判断问卷是否做完
                 */
                final String allAnswers = preferenceService.getAllSecAnswers(examination);
                final String SecObjAnswers = preferenceService.getAllAnswers(examination);
                Log.i(TAG, " test SecObjAnswers =" + SecObjAnswers);

                if (allAnswers.equals("")) {
                    if (QuestionListActivity.sp != null && QuestionListActivity.music > 0) {
                        QuestionListActivity.sp.play(QuestionListActivity.music, 1, 1, 0, 0, 1);
                    }
                    Toast.makeText(mContext, "请填写完所有的问题，谢谢!", Toast.LENGTH_SHORT).show();
                } else {
                    if (QuestionListActivity.sp != null && QuestionListActivity.music > 0) {
                        QuestionListActivity.sp.play(QuestionListActivity.music, 1, 1, 0, 0, 1);
                    }


                    String result = preferenceService.getAllAnswersShow(examination);

                    Dialog dialog = new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.btn_plus)
                            .setTitle("确定提交反馈信息")
//                            .setMessage(result)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (QuestionListActivity.sp != null && QuestionListActivity.music > 0) {
                                        QuestionListActivity.sp.play(QuestionListActivity.music, 1, 1, 0, 0, 1);
                                    }

                                    /**
                                     * 清楚这次选择的结果
                                     */
                                    preferenceService.cleanAllAnswers(examination);
                                    listAdapter.cleanAnswer();

                                    /**
                                     * 上传代码到服务器
                                     */
                                    Thread thread = new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                HttpClientService.sendExaminationResult(examination.getId(), SecObjAnswers);
                                            } catch (Exception e) {
                                                handler.sendMessage(handler.obtainMessage(22, "网络设置有错，请重新设置网络"));
                                            }
                                        }
                                    };
                                    thread.start();

                                    /**
                                     * 返回主界面问卷列表
                                     */
                                    Intent intent = new Intent(mContext, QuestionListActivity.class);
                                    mContext.startActivity(intent);
                                }
                            }).create();
                    dialog.show();


                    WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
//                    layoutParams.width = 600;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(layoutParams);
                }
            } else {
                if (QuestionListActivity.sp != null && QuestionListActivity.music > 0) {
                    QuestionListActivity.sp.play(QuestionListActivity.music, 1, 1, 0, 0, 1);
                }
                mContext.setCurrentView(mPosition);
            }
        }

    }

    @Override
    public int getCount() {
        if (viewItems == null) {
            return 0;
        }
        return viewItems.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    /**
     * @author wisdomhu 自定义类
     */
    class QuestionViewHolder {

        ListView listView;

        TextView title;

        TextView question;

        TextView answer;

        LinearLayout previousBtn, nextBtn;

        TextView nextText;

        TextView previousText;

        ImageView nextImage;

        EditText editText;

    }

}
