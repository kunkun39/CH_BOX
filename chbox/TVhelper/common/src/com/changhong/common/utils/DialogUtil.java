package com.changhong.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.changhong.common.R;

public class DialogUtil {
	public interface DialogBtnOnClickListener {

		void onSubmit(DialogMessage dialogMessage);

		void onCancel(DialogMessage dialogMessage);

	}

	public static class DialogMessage {
		public Dialog dialog;// 弹出的对话框对象
		public String msg;// 文本信息

		public DialogMessage() {
		}

		public DialogMessage(Dialog dialog) {
			this.dialog = dialog;
		}
	}

	/**
	 * 鏄剧ず閫忎紶娑堟伅瀵硅瘽妗?
	 * 
	 * @param context
	 * @param title
	 *            鏍囬 锛堝彲涓虹┖锛?
	 * @param content
	 *            鍐呭 锛堝彲涓虹┖锛?
	 * @param listener
	 *            鎸夐挳鐐瑰嚮鍥炶皟
	 * @return
	 */
	public static Dialog showPassInformationDialog(final Context context,
			String title, String content,
			final DialogBtnOnClickListener listener) {

		final Dialog dialog = new Dialog(context, R.style.Dialog_nowindowbg);

		View view = LayoutInflater.from(context).inflate(
				R.layout.view_folatdialog, null);
		dialog.setContentView(view);
		LayoutParams param = dialog.getWindow().getAttributes();
		param.gravity = Gravity.CENTER;
		param.width = (int) context.getResources().getDimension(
				R.dimen.dialog_width);

		Button bt_return = (Button) view.findViewById(R.id.bt_alertdia_cancel);

		bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onCancel(new DialogMessage(dialog));
				}
			}
		});

		TextView tv_title = (TextView) view
				.findViewById(R.id.tv_alertdia_title);
		TextView tv_content = (TextView) view
				.findViewById(R.id.tv_alertdia_content);
		tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());

		if (!TextUtils.isEmpty(content)) {
			{
				tv_content.setText(content);

			}
			if (!TextUtils.isEmpty(title)) {
				tv_title.setText(title);
			}
		} else {
			if (!TextUtils.isEmpty(title)) {
				tv_content.setText(title);
			}
		}

		dialog.getWindow().setAttributes(param);
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		try {
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dialog;

	}

	/**
	 * 显示对话框，按钮默认为确认、取消
	 * 
	 * @param context
	 * @param title
	 *            标题 （可为空）
	 * @param content
	 *            内容 （可为空）
	 * @param listener
	 *            按钮点击回调
	 * @return
	 */
	public static Dialog showAlertDialog(Context context, String title,
			String content, final DialogBtnOnClickListener listener) {
		return showAlertDialog(context, title, content, context.getString(R.string.confrim), context.getString(R.string.cancel),
				listener);
	}

	/**
	 * 显示对话框
	 * 
	 * @param context
	 * @param title
	 *            标题 （可为空）
	 * @param content
	 *            内容 （可为空）
	 * @param positiveBtnName
	 *            确认按钮名字（空值默认为“确认”）
	 * @param NegtiveBtnName
	 *            取消按钮名字（空值默认为“取消”）
	 * @param listener
	 *            按钮点击回调 （可为空）
	 * @return
	 */
	public static Dialog showAlertDialog(Context context, String title,
			String content, String positiveBtnName, String negtiveBtnName,
			final DialogBtnOnClickListener listener) {
		final Dialog dialog = new Dialog(context, R.style.Dialog_nowindowbg);

		View view = LayoutInflater.from(context).inflate(
				R.layout.view_alertdialog, null);
		dialog.setContentView(view);
		LayoutParams param = dialog.getWindow().getAttributes();
		param.gravity = Gravity.CENTER;
		param.width = (int) context.getResources().getDimension(R.dimen.dialog_width);
		param.height = (int) context.getResources().getDimension(R.dimen.dialog_height);

		Button bt_submit = (Button) view.findViewById(R.id.bt_alertdia_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_alertdia_cancel);
		if (!TextUtils.isEmpty(positiveBtnName)) {
			bt_submit.setText(positiveBtnName);
		}
		if (!TextUtils.isEmpty(negtiveBtnName)) {
			bt_cancel.setText(negtiveBtnName);
		}
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onSubmit(new DialogMessage(dialog));
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onCancel(new DialogMessage(dialog));
				}
			}
		});
		TextView tv_title = (TextView) view
				.findViewById(R.id.tv_alertdia_title);
		TextView tv_content = (TextView) view
				.findViewById(R.id.tv_alertdia_content);

		if (!TextUtils.isEmpty(content)) {
			tv_content.setText(content);
			if (!TextUtils.isEmpty(title)) {
				tv_title.setText(title);
			}
		} else {// 如果内容为空，内容显示标题信息，标题采用默认标题
			if (!TextUtils.isEmpty(title)) {
				tv_content.setText(title);
			}
		}

		dialog.getWindow().setAttributes(param);
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		try {
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dialog;
	}

	public static Dialog showEditDialog(Context context, String title,
			String positiveBtnName, String negtiveBtnName,
			final DialogBtnOnClickListener listener) {
		final Dialog dialog = new Dialog(context, R.style.Dialog_nowindowbg);

		View view = LayoutInflater.from(context).inflate(
				R.layout.view_editdialog_pan, null);
		dialog.setContentView(view);
		LayoutParams param = dialog.getWindow().getAttributes();
		param.gravity = Gravity.CENTER;
		param.width = (int) context.getResources().getDimension(R.dimen.dialog_width);
		param.height = (int) context.getResources().getDimension(R.dimen.dialog_height);

		final Button bt_submit = (Button) view.findViewById(R.id.bt_editdia_submit);
		final Button bt_cancel = (Button) view.findViewById(R.id.bt_editdia_cancel);
		if (!TextUtils.isEmpty(positiveBtnName)) {
			bt_submit.setText(positiveBtnName);
		}
		if (!TextUtils.isEmpty(negtiveBtnName)) {
			bt_cancel.setText(negtiveBtnName);
		}
		final EditText edText=(EditText)view.findViewById(R.id.edt_editdia_content);
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogMessage dialogMessage=new DialogMessage(dialog);
				dialogMessage.msg=edText.getText().toString().trim();
				if (listener != null) {
					listener.onSubmit(dialogMessage);
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogMessage dialogMessage=new DialogMessage(dialog);
				dialogMessage.msg=edText.getText().toString().trim();
				if (listener != null) {
					listener.onCancel(dialogMessage);
				}
			}
		});
		//TextView tv_title = (TextView) view.findViewById(R.id.tv_editdia_title);
		TextView tv_title = (TextView) view.findViewById(R.id.edit_dialog_title);
		if (!TextUtils.isEmpty(title)) {
			tv_title.setText(title);
		}

		dialog.getWindow().setAttributes(param);
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		try {
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dialog;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dipTopx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int pxTodip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
