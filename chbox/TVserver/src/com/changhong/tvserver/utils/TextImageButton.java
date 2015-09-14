package com.changhong.tvserver.utils;

import com.changhong.tvserver.R;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageButton;


public class TextImageButton extends Button {
	private String text = "";
	private int textSize = 24;
	private int textColor = Color.WHITE;
	private Context context;
	Paint paint = new Paint();
	

	public TextImageButton(Context context) {
		super(context);
		this.context = context;
		init(null);
	}

	public TextImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	public TextImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		setFocusable(true);
		paint.setTextAlign(Align.CENTER);
		
		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextImageButton);
			text = typedArray.getString(R.styleable.TextImageButton_text);
			text = text == null ? "" : text;
			textSize = (int) typedArray.getDimension(R.styleable.TextImageButton_textSize, 24);
			textColor = typedArray.getColor(R.styleable.TextImageButton_textColor, Color.WHITE);
		}
	}
	
	public void setText(String text) {
		this.text=text;	
		super.setText(text);
		invalidate();
	}
	
	public void setTextSize(int textSize) {
		this.textSize=textSize;
		super.setTextSize(textSize);
		paint.setTextSize(textSize);
		invalidate();
	}
	
	public void setTextColor(int textColor) {
		this.textColor=textColor;
		paint.setColor(textColor);
		super.setTextColor(textColor);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawText(text, canvas.getWidth() / 2, (canvas.getHeight() / 2)+textSize/2-5, paint);
	}

}
