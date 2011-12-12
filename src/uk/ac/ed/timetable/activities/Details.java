package uk.ac.ed.timetable.activities;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import uk.ac.ed.timetable.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Details {

    public static class Course {
        public final String course;
        public final String details;

        public Course(String course, String details) {
            this.course = course;
            this.details = details;
        }
    }

    private static final Details sInstance = new Details();

    public static Details getInstance() {
        return sInstance;
    }

    private final Map<String, List<Course>> mDict = new ConcurrentHashMap<String, List<Course>>();

    private Details() {
    }

    private boolean mLoaded = false;

    /**
     * Loads the courses and details if they haven't been loaded already.
     *
     * @param resources Used to load the file containing the courses and details.
     */
    public synchronized void ensureLoaded(final Resources resources) {
        if (mLoaded) return;

        new Thread(new Runnable() {
            public void run() {
                try {
                    loadCourses(resources);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private synchronized void loadCourses(Resources resources) throws IOException {
        if (mLoaded) return;

        Log.d("lookup", "loading courses");
        InputStream inputStream = resources.openRawResource(R.raw.courses);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, "@");
                if (strings.length < 2) continue;
                String blank = "        				                                                                           "+strings[1];	
                addCourse(strings[0].trim(), blank);
            }
        } finally {
            reader.close();
        }
        mLoaded = true;
    }


    public List<Course> getMatches(String query) {
        List<Course> list = mDict.get(query);
        return list == null ? Collections.EMPTY_LIST : list;
    }

    private void addCourse(String course, String details) {
        final Course theCourse = new Course(course, details);

        final int len = course.length();
        for (int i = 0; i < len; i++) {
            final String prefix = course.substring(0, len - i);
            addMatch(prefix, theCourse);
        }
    }

    private void addMatch(String query, Course course) {
        List<Course> matches = mDict.get(query);
        if (matches == null) {
            matches = new ArrayList<Course>();
            mDict.put(query, matches);
        }
        matches.add(course);
    }
}
