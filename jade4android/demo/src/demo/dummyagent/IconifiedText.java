package demo.dummyagent;
import android.graphics.drawable.Drawable; 

public class IconifiedText implements Comparable<IconifiedText>{ 
    
     private String mText = ""; 
     private Drawable mIcon; 
     private int mTextColor;
     
     public IconifiedText(String text, Drawable bullet) { 
          mIcon = bullet; 
          mText = text; 
     } 
      
            
     public String getText() { 
          return mText; 
     } 
      
     public void setText(String text) { 
          mText = text; 
     } 
      
     public void setIcon(Drawable icon) { 
          mIcon = icon; 
     } 
      
     public Drawable getIcon() { 
          return mIcon; 
     } 

     /** Make IconifiedText comparable by its name */ 

     public int compareTo(IconifiedText other) { 
          if(this.mText != null) 
               return this.mText.compareTo(other.getText()); 
          else 
               throw new IllegalArgumentException(); 
     }

	public int getTextColor() {
		return mTextColor;
	}

	public boolean hasIcon(){
		return (mIcon != null);
	}
	
	public void setTextColor(int color) {
		mTextColor = color;
	} 

} 

