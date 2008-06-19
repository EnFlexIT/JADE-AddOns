package buyer;

import jade.android.ConnectionListener;
import jade.android.JadeGateway;
import jade.core.Profile;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.util.Logger;
import jade.util.leap.Properties;

import java.net.ConnectException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

public class BuyerAgentActivity extends Activity implements ConnectionListener, BookBuyerGui {
    
	private EditText titleTF;
	private EditText desiredCostTF;
	private EditText maxCostTF;
	private EditText deadlineTF;
	private Button buyB;
	private Button resetB;
	
	private OnClickListener resetListener = new OnClickListener() {
		public void onClick(View v) {
			titleTF.setText("");
			desiredCostTF.setText("");
			maxCostTF.setText("");
			deadlineTF.setText("");
			deadline = null;
		}
	};
	private Button exitB;
	
	private OnClickListener exitListener = new OnClickListener(){
		public void onClick(View v) {
			finish();
		}
	};
	
	
	private Button setDeadlineB;

	private LogAdapter aAdapter;
	private ListView logList;

	private String title;
	private int desiredCost;
	private int maxCost;
	private Date deadline=new Date();
	
	private JadeGateway theGateway;
	
	private final String DIALOG_TITLE_ERROR="Error";
	
	private final Logger myLogger = Logger.getMyLogger(BuyerAgentActivity.class.getName());
	
	private String pad(int c) {
		return (c<10)?"0"+String.valueOf(c):String.valueOf(c);
	}
	
	private OnClickListener setDeadLineListener = new OnClickListener() {
		public void onClick(View v) {
			new TimePickerDialog(BuyerAgentActivity.this, mTimeSetListener,"Set the time", 0, 0, true).show();
		}
	};
	
	private TimePicker.OnTimeSetListener mTimeSetListener = new TimePicker.OnTimeSetListener() {
		public void timeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.add(Calendar.MINUTE, minute);
			deadline = cal.getTime();
			deadlineTF.setText(pad(hourOfDay)+":"+pad(minute));
		}
	};

	
	
	private OnClickListener buyListener = new OnClickListener() {
		public void onClick(View view) {
			//parameter initialization
			title = titleTF.getText().toString();
			desiredCost = -1;
			maxCost = -1;
			if (title != null && title.length() > 0) {
				if (deadline != null && deadline.getTime() > System.currentTimeMillis()) {
					try {
						desiredCost = Integer.parseInt(desiredCostTF.getText().toString());
						try {
							maxCost = Integer.parseInt(maxCostTF.getText().toString());
							if (maxCost >= desiredCost) {
								AgentCommand cmd = new AgentCommand(BookBuyerAgent.PurchaseManager.CMD_NAME_PURCHASE);
								cmd.addCommandParam(BookBuyerAgent.PurchaseManager.PARAM_NAME_BOOK_TITLE, title);
								cmd.addCommandParam(BookBuyerAgent.PurchaseManager.PARAM_NAME_DEADLINE, deadline.getTime());
								cmd.addCommandParam(BookBuyerAgent.PurchaseManager.PARAM_NAME_DESIRED_COST, desiredCost);
								cmd.addCommandParam(BookBuyerAgent.PurchaseManager.PARAM_NAME_MAXCOST, maxCost);
								if (theGateway != null){
									theGateway.execute(cmd);
								}
							} else {
								new AlertDialog.Builder(BuyerAgentActivity.this)
								.setTitle(DIALOG_TITLE_ERROR)
								.setCancelable(false)
								.setMessage("Max cost must be greater than best cost")
								.setNeutralButton("Close", new DialogInterface.OnClickListener() {
		                                                    public void onClick(DialogInterface d, int i) {
		                                                    	d.dismiss();
		                                                    }
								})
								.show();
									
							}
						} catch (Exception ex1) {
							// Invalid max cost
							myLogger.log(Logger.SEVERE,ex1.getMessage(),ex1);
							new AlertDialog.Builder(BuyerAgentActivity.this)
							.setTitle(DIALOG_TITLE_ERROR)
							.setCancelable(false)
							.setMessage("Invalid max cost")
							.setNeutralButton("Close", new DialogInterface.OnClickListener() {
		                                                    public void onClick(DialogInterface d, int i) {
		                                                    	d.dismiss();
		                                                    }
	                        })
							.show();
						}
					} catch (Exception ex2) {
						// Invalid desired cost
						myLogger.log(Logger.SEVERE,ex2.getMessage(),ex2);
						new AlertDialog.Builder(BuyerAgentActivity.this)
						.setTitle(DIALOG_TITLE_ERROR)
						.setCancelable(false)
						.setMessage("Invalid best cost")
						.setNeutralButton("Close", new DialogInterface.OnClickListener() {
		                                                    public void onClick(DialogInterface d, int i) {
		                                                    	d.dismiss();
		                                                    }
	                    })
						.show();
					}
				} else {
					// No deadline specified
					new AlertDialog.Builder(BuyerAgentActivity.this)
					.setTitle(DIALOG_TITLE_ERROR)
					.setCancelable(false)
					.setMessage("Invalid deadline")
					.setNeutralButton("Close", new DialogInterface.OnClickListener() {
		                                                    public void onClick(DialogInterface d, int i) {
		                                                    	d.dismiss();
		                                                    }
	                })
					.show();
				}
			} else {
				// No book title specified
				new AlertDialog.Builder(BuyerAgentActivity.this)
				.setTitle(DIALOG_TITLE_ERROR)
				.setCancelable(false)
				.setMessage("No book title specified!")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
		                                                    public void onClick(DialogInterface d, int i) {
		                                                    	d.dismiss();
		                                                    }
	             })
				.show();
			}

		}

	
	};
	

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.android_buyer_gui);
        
      //SAVE GUI COMPONENTS TO BE USED
		titleTF = (EditText) findViewById(R.id.BookEditText);
		desiredCostTF = (EditText) findViewById(R.id.BestCostEditText);
		maxCostTF = (EditText) findViewById(R.id.MazCostEditText);
		deadlineTF = (EditText) findViewById(R.id.DeadlineEditText);
		//BUY BUTTON
		buyB = (Button) findViewById(R.id.BuyButton);
		buyB.setOnClickListener(buyListener);
		//RESETB BUTTON
		resetB = (Button) findViewById(R.id.ResetButton);
		resetB.setOnClickListener(resetListener);
		//EXITB BUTTON
		exitB = (Button) findViewById(R.id.ExitButton);
		exitB.setOnClickListener(exitListener);
		//SETDEADLINEB BUTTON
		setDeadlineB = (Button) findViewById(R.id.SetButton);
		setDeadlineB.setOnClickListener(setDeadLineListener);
		
		aAdapter = new LogAdapter(this);
		logList = (ListView)findViewById(R.id.MessageListView);
        logList.setVerticalScrollBarEnabled(true);
        logList.setBackgroundColor(Color.WHITE);
        logList.setAdapter(aAdapter);
        
        Properties props = new Properties();
		props.setProperty(Profile.MAIN_HOST, getResources().getString(R.string.jade_platform_address));
		props.setProperty(Profile.MAIN_PORT, getResources().getString(R.string.jade_platform_port));
		props.setProperty(JICPProtocol.MSISDN_KEY, getResources().getString(R.string.agent_msisdn));
	

        
        try {
			JadeGateway.connect(BookBuyerAgent.class.getName(), null,props, this, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			myLogger.log(Logger.SEVERE, e.getMessage(), e);
		}
    }

	@Override
	public void onConnected(JadeGateway gateway) {
		// TODO Auto-generated method stub
		//pass an instance of the BookBuyerGui
		try {
			theGateway = gateway;
			gateway.execute(this);
		} catch (Exception e) {
			myLogger.log(Logger.SEVERE, e.getMessage(),e);
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAgent(BookBuyerAgent a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	private class ShowMessageRunnable implements Runnable{

		private String message;
		
		public ShowMessageRunnable(String msg){
			message = msg;
		}
		
		@Override
		public void run() {
			aAdapter.addHaed(message);
			logList.setAdapter(aAdapter);
		}
		
	}
	
	@Override
	public void showMessage(String message) {
		runOnUIThread(new ShowMessageRunnable(message));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (theGateway != null){
			try {
				theGateway.shutdownJADE();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				myLogger.log(Logger.FINE, e.getMessage(), e);
			}
			theGateway.disconnect(this);
		}
	}
}