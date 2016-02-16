package com.example.colortest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
	private Paint paint;

	public float touch_x;
	public float touch_y;

	Context context;
	public boolean isTouched;

	public DrawingView(Context context) {
		super(context);
		paint = new Paint();
		paint.setColor(0xeed7d7d7);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
	}

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setColor(0xeed7d7d7);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub

		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		this.setMeasuredDimension(parentWidth, parentHeight);
		touch_x = parentWidth / 2;
		touch_y = parentHeight / 2;

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void onDraw(Canvas canvas) {

		canvas.drawLine(touch_x - 100, touch_y - 100, touch_x - 100 + 30,
				touch_y - 100, paint);
		canvas.drawLine(touch_x + 100 - 30, touch_y - 100, touch_x + 100,
				touch_y - 100, paint);
		canvas.drawLine(touch_x - 100, touch_y - 100, touch_x - 100,
				touch_y - 100 + 30, paint);
		canvas.drawLine(touch_x + 100, touch_y - 100, touch_x + 100,
				touch_y - 100 + 30, paint);
		canvas.drawLine(touch_x - 100, touch_y + 100 - 30, touch_x - 100,
				touch_y + 100, paint);
		canvas.drawLine(touch_x - 100, touch_y + 100, touch_x - 100 + 30,
				touch_y + 100, paint);
		canvas.drawLine(touch_x + 100 - 30, touch_y + 100, touch_x + 100,
				touch_y + 100, paint);
		canvas.drawLine(touch_x + 100, touch_y + 100 - 30, touch_x + 100,
				touch_y + 100, paint);
		canvas.drawCircle(touch_x, touch_y, 20, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			isTouched = true;
			break;
		case MotionEvent.ACTION_MOVE:
			isTouched = false;
			if (event.getX() - 1>= 0 && event.getY() - 1>= 0) {
				touch_x = event.getX();
				touch_y = event.getY();
				this.invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			isTouched = false;
			break;
		}

		return true;
	}

}