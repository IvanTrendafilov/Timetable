package uk.ac.ed.timetable.activities;

import uk.ac.ed.timetable.*;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/**
 * The course management activity.
 */
public class ActivityList extends CommonActivity {

    private static final int MENU_SEARCH = 1;
  	protected Bundle bundleOtherDataStartup = new Bundle();
    private TextView mTextView;
    private ListView mList;
    private static int back = 0;
    private HashSet<String> courselist = new HashSet<String>();
    private ArrayList<String> crlist = new ArrayList<String>();
    
    protected static void goBack(int a) {
    	back=a;
    }
    
    @Override
		public void onResume() {
    	super.onResume();
    		if(back==1) { back=0; finish(); }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase baza = userdb.GetSQLiteDb();
        Intent intent = getIntent();
        setContentView(R.layout.search);
        mTextView = (TextView) findViewById(R.id.coursePrompt);
        mList = (ListView) findViewById(R.id.list);
//        mTextView.setText(" ");
        Cursor cr;
        cr = baza.rawQuery("SELECT DISTINCT Subject from courses", null);
        cr.moveToFirst();
        if(cr.getCount()!=0) {
        	for(int i=0;i<cr.getCount();i++) {
        		courselist.add("Delete "+cr.getString(0)+"?");
        		cr.moveToNext();
        	}
        	crlist.add("Add a course?");
        	crlist.add("Go back?"); 
        	crlist.addAll(courselist);
        }
        else {
        	crlist.add("You have no courses. Add one?");
        	crlist.add("Go back?");
        }
        cr.close();
        final ArrayAdapter<String> adpt = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,crlist);
        mList.setAdapter(adpt);
        mList.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position,
							long arg3)
					{
						String msg="";
						if(mList.getItemAtPosition(position).toString().trim().equalsIgnoreCase("Go back?")) {
							finish();
						}
						else if(mList.getItemAtPosition(position).toString().trim().contains("Add a course") || mList.getItemAtPosition(position).toString().trim().contains("Add one")) {
							onSearchRequested();
							msg="Type course name above.";
						}
						else {
							String query = mList.getItemAtPosition(position).toString().trim().replace("Delete ", "");
							query = query.substring(0,query.length()-1);
							Log.d("SQL delete course",query);
							SQLiteDatabase baza = userdb.GetSQLiteDb();
							baza.execSQL("delete from courses where subject = \""+query.trim()+"\"");
							msg=query+" successfully deleted!";
							finish();
						}
						
						if(!(msg.equalsIgnoreCase(""))) {
						mList.getItemAtPosition(position);
						Toast.makeText(getBaseContext(),
								msg,
								Toast.LENGTH_LONG).show(); 
						}
						
					}
        });
// depricated        
/*        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sm.setOnDismissListener(new OnDismissListener(){
					@Override
					public void onDismiss()
					{
						// what to do when search dialog is closed.
					}
        	
        	
        }); */
        
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Details.getInstance().ensureLoaded(getResources());
            String course = intent.getDataString();
            Details.Course theCourse = Details.getInstance().getMatches(course).get(0);
            launchCourse(theCourse);
            finish();
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mTextView.setText(" "+getString(R.string.search_results, query));
            CourseAdapter courseAdapter = new CourseAdapter(Details.getInstance().getMatches(query));
            mList.setAdapter(courseAdapter);
            mList.setOnItemClickListener(courseAdapter);
         }

        Log.d("Lookup", intent.toString());
        if (intent.getExtras() != null) {
            Log.d("Lookup", intent.getExtras().keySet().toString());
        }
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SEARCH, 0, R.string.menu_search)
                .setIcon(android.R.drawable.ic_search_category_default)
                .setAlphabeticShortcut(SearchManager.MENU_KEY);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SEARCH:
//                onSearchRequested();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
      
    private void launchCourse(Details.Course theCourse) {
        Intent next = new Intent();
        next.setClass(this, ActivityCourseAction.class);
        next.putExtra("course", theCourse.course);
        next.putExtra("details", theCourse.details);
        startActivity(next);
    }

    class CourseAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private final List<Details.Course> mCourses;
        private final LayoutInflater mInflater;

        public CourseAdapter(List<Details.Course> courses) {
            mCourses = courses;
            mInflater = (LayoutInflater) ActivityList.this.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return mCourses.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TwoLineListItem view = (convertView != null) ? (TwoLineListItem) convertView :
                    createView(parent);
            bindView(view, mCourses.get(position));
            return view;
        }

        private TwoLineListItem createView(ViewGroup parent) {
            TwoLineListItem item = (TwoLineListItem) mInflater.inflate(R.layout.list2, parent, false);
            item.getText2().setSingleLine();
            item.getText2().setTextColor(Color.WHITE);
            item.getText2().setEllipsize(TextUtils.TruncateAt.END);
            return item;
        }

        private void bindView(TwoLineListItem view, Details.Course course) {
            view.getText1().setText(course.course);
            view.getText2().setText(course.details);
            view.getText2().setTextColor(Color.WHITE);
            }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            launchCourse(mCourses.get(position));
            finish();
            }        
    }
    
    public void onStart() {
    	super.onStart();
    	if(back==1) finish();
    }
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
		@Override
		protected void restoreStateFromFreeze()
		{
			// TODO Auto-generated method stub
			
		}
}
