package com.changhong.faq.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.changhong.faq.R;
import com.changhong.faq.activity.VoteSubmitActivity;
import com.changhong.faq.domain.Examination;
import com.changhong.faq.domain.QuestionType;
import com.changhong.faq.service.PreferenceService;

import java.util.ArrayList;
import java.util.Set;

public class VoteSubmitListAdapter extends BaseAdapter {

    /**
     * 保存设置的Service
     */
    private PreferenceService preferenceService;

    private VoteSubmitActivity mContext;

    private int examinationId;
    private Examination examination;

    private int questionIndex;

    private  QuestionType questiontype;

    private ArrayList<String> dataItems;

	public VoteSubmitListAdapter(VoteSubmitActivity context,Examination examination, int examinationId, QuestionType questiontype, int questionIndex, ArrayList<String> dataItems) {
		mContext = context;
		this.examination = examination;
        this.preferenceService = new PreferenceService(context);
        this.examinationId = examinationId;
        this.questionIndex = questionIndex;
        this.dataItems = dataItems;
        this.questiontype= questiontype;
    }

    public void cleanAnswer() {
        notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		return dataItems.size();
	}

	@Override
	public Object getItem(int position) {
		return dataItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    AnswerViewHolder holder = null;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new AnswerViewHolder();
			convertView = mContext.getLayoutInflater().inflate(R.layout.vote_submit_listview_item, null);

			holder.select_text = (CheckBox) convertView.findViewById(R.id.vote_submit_select_text);
			holder.question_index = (TextView) convertView.findViewById(R.id.vote_question_index);
            holder.question_type = (TextView) convertView.findViewById(R.id.vote_questiontype);
           
            
			convertView.setTag(holder);
		} else {
			holder = (AnswerViewHolder) convertView.getTag();
		}
		
			
			Set<String> selected = preferenceService.getAnswers(examination, String.valueOf(examinationId), String.valueOf(questionIndex));
		
        

        if (selected.contains(String.valueOf(position))) {
            holder.select_text.setChecked(true);
        } else {
            holder.select_text.setChecked(false);
        }
        
		
       holder.select_text.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.select_text.setText(dataItems.get(position));
        holder.question_index.setText(String.valueOf(questionIndex));
        holder.question_type.setText(""+questiontype);
        return convertView;
	}

	/**
	 * @author wisdomhu 自定义类
	 */
	public class AnswerViewHolder {

		CheckBox select_text;

		TextView question_index;

        TextView question_type;
        
       
    }
}
