package uk.ac.ed.timetable.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import uk.ac.ed.timetable.*;
import uk.ac.ed.timetable.database.DBHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Displays a course and its details, add it to the database.
 */
public class ActivityCourseAction extends CommonActivity {
	
    private TextView mCourse;
    private TextView mDetails;
    private ListView mLvDetails;
    private SQLiteDatabase dbs;
    private ArrayList<StringBuffer> todbs = new ArrayList<StringBuffer>(); // final list of dates fed to dbs
    private ArrayList<StringBuffer> exactdates = new ArrayList<StringBuffer>();
    private ArrayList<StringBuffer> dlist = new ArrayList<StringBuffer>(); // contains delivery times
    private ArrayList<String> options = new ArrayList<String>(); // contains delivery options in easier to parse format
    private ArrayList<String> humanoptions = new ArrayList<String>(); // contains delivery options in Human format
    private String tmpitr,tmpass,opt=""; // just temporary assignments
    private int i=0,j=0;
    private boolean clash=false;
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	dbs.close();
    }
    @Override
		public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.courseaction);
      dbs = (new DBHelper(this)).getWritableDatabase();
      mCourse = (TextView) findViewById(R.id.course);
      mDetails = (TextView) findViewById(R.id.details);
      mLvDetails = (ListView) findViewById(R.id.listdetails);
      Intent intent = getIntent();
      String course = intent.getStringExtra("course");
      String details = intent.getStringExtra("details");
      StringBuffer cor = new StringBuffer(course.trim());
      // begin parsing the document...
      for(i=0;i<cor.length();i++) {
      	if(i==0) cor.replace(0,1,""+Character.toUpperCase(cor.charAt(0)));
      	if(i!=0 && (cor.charAt(i-1)==' ' || Character.isDigit(cor.charAt(i-1))==true || cor.charAt(i-1)=='-')) {
      		cor.replace(i,i+1,""+Character.toUpperCase(cor.charAt(i)));
      	}
      }
      String[] junk = details.trim().split("\\|");
      String tmpc = new String();
      // gets delivery options for the selected course. E.g. [1,2,3,0] becomes 1230
      for(i=0;i<junk[1].length();i++) {
      	if(junk[1].charAt(i) == '[' || junk[1].charAt(i)==',') { 
      		continue; 
      		}
      	else if(junk[1].charAt(i)==']') { 
      		options.add(tmpc.trim()); tmpc=""; 
      		}
      	else { 
      		tmpc=tmpc+junk[1].charAt(i); }
      }
      // converts a course lecture/lab times to human time and adds them to a list
      // e.g. 670 to Monday 11:10
      String[] irr = details.trim().split("%");
      String[] iterm = irr[1].split("\\|");
      final String[] courseTimes = iterm[0].split(",");
      String[] irr2 = details.split("%");
      final String semester = irr2[0].trim();
      Log.d("Semester:",semester);
      for(i=0;i<courseTimes.length;i++) {
      	StringBuffer tmp = new StringBuffer((getHumanDay(courseTimes[i])).trim());
      	dlist.add(tmp);
      }
      // matches delivery options with course lecture/lab times to populate options
      // a nice O(n^2) algorithm
      for(i=0;i<options.size();i++) {
      	tmpitr=options.get(i).trim();
      	
      	for(j=0;j<tmpitr.length();j++) {
      		tmpass = ""+tmpitr.charAt(j);
      		if(opt.contains(dlist.get(Integer.parseInt(tmpass.trim())))) continue;
      		opt = opt+" "+dlist.get(Integer.parseInt(tmpass.trim()))+"\n";
      		tmpass="";
      	}
      	humanoptions.add(opt);
      	opt="";
      }
   // add elements to al, including duplicates
      mCourse.setText(cor);
      mLvDetails.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, humanoptions));
      if(semester.equals("s1")) details="Semester 1. Delivery options:\n";
      if(semester.equals("s2")) details="Semester 2. Delivery options:\n";
      if(semester.equals("y")) details="Full year course. Delivery options: \n";
      mDetails.setText(details);
        mLvDetails.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position,
							long arg3)
					{
						if(!semester.equalsIgnoreCase("y")) { // handle full year courses
						for(i=0;i<courseTimes.length;i++) {
		        	StringBuffer tmp = new StringBuffer(getPCDay((courseTimes[i]).trim(), semester));
		        	exactdates.add(tmp);
						}
						String bla = options.get(position);
						for(i=0;i<bla.length();i++) {
							todbs.add(exactdates.get(Integer.parseInt(""+bla.charAt(i))));
						}
						}
						else { // this is to make sure I don't populate the Christmas vacation when adding full-year courses
							for(i=0;i<courseTimes.length;i++) {
								StringBuffer tmp = new StringBuffer(getPCDay((courseTimes[i]).trim(), "s1"));
								exactdates.add(tmp);
							}
							String bla = options.get(position);
							for(i=0;i<bla.length();i++) {
								todbs.add(exactdates.get(Integer.parseInt(""+bla.charAt(i))));
							}
							exactdates.clear();
							for(i=0;i<courseTimes.length;i++) {
								StringBuffer tmp = new StringBuffer(getPCDay((courseTimes[i]).trim(),"s2"));
								exactdates.add(tmp);
							}
							for(i=0;i<bla.length();i++) {
								todbs.add(exactdates.get(Integer.parseInt(""+bla.charAt(i))));
							}
						}
						// remove duplicates
						removeDuplicates(todbs);
						String msg ="";
						for(i=0;i<todbs.size();i++) {
							String[] times=todbs.get(i).toString().split("@");
							Cursor cur,cur2;
							cur = dbs.rawQuery("select subject from courses where startdate="+times[0],null);
							cur2 = dbs.rawQuery("select subject from courses where RepeatEndOnDate="+times[1],null);
							cur.moveToFirst();
							cur2.moveToFirst();
							if(cur.getCount()!=0 || cur2.getCount()!=0) {
								if(mCourse.getText().toString().contains(cur.getString(0))) {
									msg = "An instance of " + mCourse.getText() +" already exist in schedule!";
									clash=true;
								}
								else {
								msg="Timetable clash with "+cur.getString(0)+"! Cannot add course to schedule."; 
									clash=true;
								}
								break;
							}
							cur.close();
							cur2.close();
						}
						if(clash!=true) {
							for(i=0;i<todbs.size();i++) {
								String[] times=todbs.get(i).toString().split("@");
								dbs.execSQL("INSERT INTO Courses (Subject, StartDate, DurationInMinutes, AllDay, Alarm, RepeatType, RepeatEvery, RepeatEndOnDate) VALUES (\'"+mCourse.getText()+"\',"+times[0]+",50,0,0,2,1,"+times[1]+")");
								Log.d("SQL course times",times[0]+" "+times[1]);
								}	
							msg=mCourse.getText()+" successfully added to schedule!";
						}
							Toast.makeText(getBaseContext(),
									msg,
									Toast.LENGTH_LONG).show(); 
							ActivityList.goBack(1);
							finish();
						}
        });
        
    }
    private String getHumanDay(String day) {
    	int a = Integer.parseInt(day.trim());
    	String sMinutes,sHours,result=null;
    	int b,wholeHour,wholeMinutes;
    	b=a/(24*60);
    	wholeHour=a/60 - b*24;
    	wholeMinutes=a - (a/60)*60;
    	if(wholeHour < 10)	sHours="0"+Integer.toString(wholeHour);
    	else sHours = Integer.toString(wholeHour);
    	if(wholeMinutes==0)	sMinutes="0"+Integer.toString(wholeMinutes);
    	else sMinutes=Integer.toString(wholeMinutes);
    	if(b==0) result="Monday";
    	if(b==1) result="Tuesday";
    	if(b==2) result="Wednesday";
    	if(b==3) result="Thursday";
    	if(b==4) result="Friday";
    	if(b==5) result="Saturday";
    	if(b==6) result="Sunday";
    	if(b==-1) result=null;
    	result=sHours+":"+sMinutes+" "+result;
    	return result;
    }
   
    private String getPCDay(String zz, String semester) {
    ArrayList<Long> numlist = new ArrayList<Long>(); // contains all possible days for a course
    int a = Integer.parseInt(zz.trim());
    int b,wholeHour,wholeMinutes;
  	b=a/(24*60);
  	wholeHour=a/60 - b*24;
  	wholeMinutes=a - (a/60)*60;
  	long ms=0; // milliseconds 
    a = a/(24*60)+2;
    Calendar cal = Calendar.getInstance();
    Calendar tmp = Calendar.getInstance();
    int week = cal.get(Calendar.WEEK_OF_YEAR);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int date = cal.get(Calendar.DATE);
    
    if(week >= 37 || week <= 22) {
     do { 
    			int now = cal.get(Calendar.DAY_OF_WEEK);
    			if(now == a) {
    				tmp.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),wholeHour,wholeMinutes,0);
    				ms=tmp.getTimeInMillis();
    				ms=(ms/1000)*1000;
    				if(semester.trim().equals("s1")) 
    					if(cal.get(Calendar.WEEK_OF_YEAR) >= 38 && cal.get(Calendar.WEEK_OF_YEAR) <= 48) 
    						numlist.add(ms);
    				if(semester.trim().equals("s2"))
    						if(cal.get(Calendar.WEEK_OF_YEAR) >= 2 && cal.get(Calendar.WEEK_OF_YEAR) <= 12)
    							numlist.add(ms);
    				cal.add(Calendar.DAY_OF_YEAR,7); // found day of interest, skipping unnecessary iterations
    				continue;
    				}
    			cal.add(Calendar.DAY_OF_YEAR,1);
    	} while (cal.get(Calendar.WEEK_OF_YEAR) >= 37 || cal.get(Calendar.WEEK_OF_YEAR) <= 22); 
    	cal.set(year,month,date);
   	do {
    			int now = cal.get(Calendar.DAY_OF_WEEK);
    			if(now == a) {
    				tmp.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),wholeHour,wholeMinutes,0);
    				ms=tmp.getTimeInMillis();
    				ms=(ms/1000)*1000;
    				if(semester.trim().equals("s1")) 
    					if(cal.get(Calendar.WEEK_OF_YEAR) >= 38 && cal.get(Calendar.WEEK_OF_YEAR) <= 48) 
    						numlist.add(ms);
    				if(semester.trim().equals("s2"))
    						if(cal.get(Calendar.WEEK_OF_YEAR) >= 2 && cal.get(Calendar.WEEK_OF_YEAR) <= 12)
    							numlist.add(ms);
    				cal.add(Calendar.DAY_OF_YEAR,-7); // found day of interest, skipping unnecessary iterations
    				continue;
    				}
    			cal.add(Calendar.DAY_OF_YEAR,-1);
    			} while (cal.get(Calendar.WEEK_OF_YEAR) >= 37 || cal.get(Calendar.WEEK_OF_YEAR) <= 22);
    	}
    else {
    	boolean flag = true;
    	do {
    		int now = cal.get(Calendar.DAY_OF_WEEK);
    		if(now == Calendar.MONDAY) {
    			int nowWeek = cal.get(Calendar.WEEK_OF_YEAR);
    			if(nowWeek == 37) {
    			cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
    			flag=false;
    			}
    			else {
    				cal.add(Calendar.DAY_OF_YEAR,7);
    			}
    		}
    		else {
    			cal.add(Calendar.DAY_OF_YEAR,1);
    		}
    	} while(flag);
    	
    	week = cal.get(Calendar.WEEK_OF_YEAR);
    	Calendar tmp2 = Calendar.getInstance();
      week = cal.get(Calendar.WEEK_OF_YEAR);
      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      date = cal.get(Calendar.DATE);
      
    	if(week >= 37 || week <= 22) {
        do { 
       			int now = cal.get(Calendar.DAY_OF_WEEK);
       			if(now == a) {
       				tmp2.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),wholeHour,wholeMinutes,0);
       				ms=tmp2.getTimeInMillis();
       				ms=(ms/1000)*1000;
       				if(semester.trim().equals("s1")) 
       					if(cal.get(Calendar.WEEK_OF_YEAR) >= 38 && cal.get(Calendar.WEEK_OF_YEAR) <= 48) 
       						numlist.add(ms);
       				if(semester.trim().equals("s2"))
       						if(cal.get(Calendar.WEEK_OF_YEAR) >= 2 && cal.get(Calendar.WEEK_OF_YEAR) <= 12)
       							numlist.add(ms);
       				cal.add(Calendar.DAY_OF_YEAR,7); // found day of interest, skipping unnecessary iterations
       				continue;
       				}
       			cal.add(Calendar.DAY_OF_YEAR,1);
       	} while (cal.get(Calendar.WEEK_OF_YEAR) >= 37 || cal.get(Calendar.WEEK_OF_YEAR) <= 22); 
    	}
    }
    Object max = Collections.max(numlist);
    Object min = Collections.min(numlist);
    zz = min.toString() + "@" + max.toString();
    numlist.clear();
    return zz.trim();
    }
    // another nice n^2 algorithm to remove duplicates
		protected void removeDuplicates(ArrayList<StringBuffer> list) {
			for(i=0;i<list.size();i++) {
				for(j=0;j<list.size();j++) {
					if(j!=i) {
						try {
							if(list.get(i).toString().trim().contentEquals(list.get(j).toString().trim())) {
								list.remove(i);
							}
							else continue;
						}
						catch(IndexOutOfBoundsException e) { list.trimToSize(); i=0; j=0; }
					}
				}
			}
		}

		@Override
		protected void restoreStateFromFreeze()
		{
			// TODO Auto-generated method stub
			
		}
}
