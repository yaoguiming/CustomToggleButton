package cn.why.customtogglebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class CustomToggleButton extends View implements OnClickListener{

	private Bitmap backgroundBitmap;
	private Bitmap slideButtonBitmap;
	private Paint paint;
	private float slideBtn_left;
	private boolean currentState = false;
	private boolean isDrag = false;
	
	/**
	 * 背景图的资源ID
	 */
	private int backgroundId;
	/**
	 * 滑动图片的资源ID
	 */
	private int slideBtnId;
	
	public CustomToggleButton(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public CustomToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		//获得自定义的属性
				TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomToggleBtn);
				
				int N = ta.getIndexCount();
				for (int i = 0; i < N; i++) {
					/*
					 * 获得某个属性的ID值
					 */
					int itemId = ta.getIndex(i);
					switch (itemId) {
						case R.styleable.CustomToggleBtn_current_state:
							currentState = ta.getBoolean(itemId, false);
							break;
						case R.styleable.CustomToggleBtn_custom_background:
							backgroundId = ta.getResourceId(itemId, -1);
							if(backgroundId == -1){
								throw new RuntimeException("请设置背景图片");
							}
							backgroundBitmap = BitmapFactory.decodeResource(getResources(), backgroundId);
							break;
						case R.styleable.CustomToggleBtn_custom_slide_btn:
							slideBtnId = ta.getResourceId(itemId, -1);
							slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideBtnId);
							break;
					}
				}
		initView();
	}

	public CustomToggleButton(Context context) {
		super(context);
		initView();
	}

	
	private void initView() {
		backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.switch_background);
		slideButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slide_button);
		paint = new Paint();
//		paint.setAlpha(180);
		paint.setAntiAlias(true);
		setOnClickListener(this);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
	}
	
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
//	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
		canvas.drawBitmap(slideButtonBitmap, slideBtn_left, 0, paint);
	}

	@Override
	public void onClick(View arg0) {
		if (!isDrag) {
			currentState = !currentState;
			flushState();
		}
	}
	
	private float startX;
	private float endX;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = endX = event.getX();
				isDrag = false;
				break;
			case MotionEvent.ACTION_MOVE:
				endX = event.getX();
				if (Math.abs(endX - startX) > 5) {
					isDrag = true;
				}
				float swipeDistance = endX - startX;
				slideBtn_left = slideBtn_left + swipeDistance;
				break;
			case MotionEvent.ACTION_UP:
				if (isDrag) {
					float maxLeft = backgroundBitmap.getWidth()-slideButtonBitmap.getWidth();
					if(slideBtn_left > maxLeft/2){
						currentState = true;
					}else{
						currentState = false;
					}
					flushState();
				}
				break;
		}
		flushView();
		return true;
	}
	
	private void flushState(){
		if(currentState){
			slideBtn_left = backgroundBitmap.getWidth()-slideButtonBitmap.getWidth();
		}else{
			slideBtn_left = 0;
		}
		flushView(); 
	}
	private void flushView() {
		int maxLeft = backgroundBitmap.getWidth()-slideButtonBitmap.getWidth();	
		
		slideBtn_left = (slideBtn_left>0)?slideBtn_left:0;
		
		slideBtn_left = (slideBtn_left<maxLeft)?slideBtn_left:maxLeft;
		
		invalidate();
	}
}
