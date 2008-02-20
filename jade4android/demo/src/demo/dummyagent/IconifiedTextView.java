package demo.dummyagent;

import android.content.Context; 
import android.graphics.drawable.Drawable; 
import android.util.TypedValue;
import android.widget.ImageView; 
import android.widget.LinearLayout; 
import android.widget.TextView; 

public class IconifiedTextView extends LinearLayout { 
      
     private TextView mText; 
     private ImageView mIcon; 
     
     public IconifiedTextView(Context context, IconifiedText aIconifiedText, int txtSize) { 
          super(context); 

          /* First Icon and the Text to the right (horizontal), 
           * not above and below (vertical) */ 
          this.setOrientation(HORIZONTAL); 

          
          if (aIconifiedText.hasIcon()) {
        	  mIcon = new ImageView(context); 
        	  mIcon.setImageDrawable(aIconifiedText.getIcon()); 
        	  // left, top, right, bottom 
        	  mIcon.setPadding(0, 2, 3, 0); // 5px to the right 
           
        	  /* At first, add the Icon to ourself 
        	   * (! we are extending LinearLayout) */ 
        	  addView(mIcon,  new LinearLayout.LayoutParams( 
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
      
          }
          
          mText = new TextView(context); 
          mText.setText(aIconifiedText.getText());
          mText.setTextColor(aIconifiedText.getTextColor());
          mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSize);
          /* Now the text (after the icon) */ 
          addView(mText, new LinearLayout.LayoutParams( 
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
     } 

     public IconifiedTextView(Context context, IconifiedText aIconifiedText)
     {
    	 this(context,aIconifiedText,15);
     }
     
     public void setText(String words) { 
          mText.setText(words); 
     } 

     public void setTextColor(int color) {
    	 mText.setTextColor(color);
     }
    	 
     public void setIcon(Drawable bullet) { 
    	 if (mIcon != null)
    		 mIcon.setImageDrawable(bullet); 
     } 
}