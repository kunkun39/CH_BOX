package com.changhong.common.utils;

import com.changhong.common.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogUtil {
	public static interface DialogBtnOnClickListener {

		public void onSubmit(Dialog dialog);

		public void onCancel(Dialog dialog);

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
		return showAlertDialog(context, title, content, "确    认", "取    消", listener);
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
		param.width = dipTopx(context, 280);
		param.height = dipTopx(context, 170);

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
					listener.onSubmit(dialog);
				}
				if (dialog != null && dialog.isShowing()) {
					dialog.cancel();
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onCancel(dialog);
				}
				if (dialog != null && dialog.isShowing()) {
					dialog.cancel();
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
		dialog.show();
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