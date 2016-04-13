package com.nkcsio.android.myview;

import java.text.DecimalFormat;



import com.nkcsio.android.activity.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

public class MoneyView extends EditText {
	public double value;
	public MoneyView(Context context) {
		super(context);
		init();
	}
	public MoneyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public MoneyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init(){
		setTextColor(getResources().getColor(R.color.table_text_color));
		setTextSize(getResources().getDimension(R.dimen.table_font_size2));
		setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
		setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
		setSelectAllOnFocus(true);
		setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(!arg1){
					
					if(getValue()){
						setText(new DecimalFormat("#########.##").format(value));						
					}
				}
				
			}
		});
		
	}
	public boolean getValue(){
		double d;
		try{
			d=Double.parseDouble(getText().toString());
			d=(double)(((long)(d*100))/100.0);
			if(d>999999999.99){
				d=999999999.99d;
			}
		}catch(Exception e){
			value=0;
			return false;
		}
		value=d;
		return true;
	}
	public void setValue(double d){
		double v=d;
		if(d<0){
			setText("");
			value=0;
			return;
		}else if(d>999999999.99d){
			v=999999999.99d;
		}
		setText(new DecimalFormat("#########.##").format(v));
		value=v;
	}

	public static void drawCell(View view, Canvas canvas){
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.GRAY);
		canvas.drawRect(new Rect(1*view.getWidth()/11, 0, 1*view.getWidth()/11+1, view.getHeight()), paint);
		canvas.drawRect(new Rect(2*view.getWidth()/11, 0, 2*view.getWidth()/11+1, view.getHeight()), paint);
		paint.setColor(Color.BLUE);
		canvas.drawRect(new Rect(3*view.getWidth()/11, 0, 3*view.getWidth()/11+1, view.getHeight()), paint);
		paint.setColor(Color.GRAY);
		canvas.drawRect(new Rect(4*view.getWidth()/11, 0, 4*view.getWidth()/11+1, view.getHeight()), paint);
		canvas.drawRect(new Rect(5*view.getWidth()/11, 0, 5*view.getWidth()/11+1, view.getHeight()), paint);
		paint.setColor(Color.BLUE);
		canvas.drawRect(new Rect(6*view.getWidth()/11, 0, 6*view.getWidth()/11+1, view.getHeight()), paint);
		paint.setColor(Color.GRAY);
		canvas.drawRect(new Rect(7*view.getWidth()/11, 0, 7*view.getWidth()/11+1, view.getHeight()), paint);
		canvas.drawRect(new Rect(8*view.getWidth()/11, 0, 8*view.getWidth()/11+1, view.getHeight()), paint);
		paint.setColor(Color.RED);
		canvas.drawRect(new Rect(9*view.getWidth()/11, 0, 9*view.getWidth()/11+1, view.getHeight()), paint);
		paint.setColor(Color.GRAY);
		canvas.drawRect(new Rect(10*view.getWidth()/11, 0, 10*view.getWidth()/11+1, view.getHeight()), paint);
	}
	public void drawValue(Canvas canvas){
		Paint paint = new Paint();
		if(getValue()){
			paint.setColor(Color.BLACK);
			paint.setTextSize(getResources().getDimension(R.dimen.table_font_size));
			paint.setTextAlign(Align.CENTER);
			long num=(long)(value*100);
			if(num==0){
				canvas.drawText("0", 21*getWidth()/22, getHeight()-10, paint);
				return;
			}
			for(int i=11;i>0;i--){
				if(num==0){
					break;
				}
				long rest=num%10;
				num=num/10;
				canvas.drawText(""+rest, (i*2-1)*getWidth()/22, getHeight()-10, paint);
			}
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		if(!isFocused()){
			drawCell(this, canvas);
			drawValue(canvas);
		}else{
			Paint paint = new Paint();
			paint.setStyle(Style.FILL);
			paint.setColor(getResources().getColor(R.color.table_editable_color));
			canvas.drawRect(new Rect(0, 0, getWidth(),getHeight()), paint);
			super.onDraw(canvas);
		}		
	}
}
