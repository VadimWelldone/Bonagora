package com.yalantis.androidtechtest.app;

import android.animation.LayoutTransition;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener{

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /*
    json tags
     */
    private static final String TAG_TITLE = "title";
    private static final String TAG_CHILDREN = "children";

    /*
    array indices
     */
    private static final int KEY_NESTING_LEVEL = 0;
    private static final int KEY_POSITION = 1;
    private static final int KEY_PARENT_POSITION = 2;

    //TextView padding
    private static final int TEXT_VIEW_PADDING = 30;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ScrollView mDrawerScrollView;
    private ViewGroup mDrawerViewGroup;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerScrollView = (ScrollView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerViewGroup = (ViewGroup) mDrawerScrollView.findViewById(R.id.nav_drawer_view_group);
        parseJsonData();
        return mDrawerScrollView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showInitialState();
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onClick(View v) {
        //retrieve view tag
        int[] tag = (int[])v.getTag();

        //hide all elements from the same level
        ViewGroup nestedViewGroup = (ViewGroup)mDrawerViewGroup.getChildAt(tag[KEY_NESTING_LEVEL]);
        for(int position = 0; position < nestedViewGroup.getChildCount(); position++) {
            if(position != tag[KEY_POSITION]) {
                nestedViewGroup.getChildAt(position).setVisibility(View.GONE);
            }
        }

        //hide all elements from the previous levels
        for(int position = tag[KEY_NESTING_LEVEL] + 2; position <= Utils.NESTING_LEVEL.FOURTH_LEVEL.ordinal(); position++) {
            mDrawerViewGroup.getChildAt(position).setVisibility(View.GONE);
        }

        //show belong elements from next level (current_level + 1)
        if(tag[KEY_NESTING_LEVEL] + 1 < mDrawerViewGroup.getChildCount()) {
            ViewGroup descendantsViewGroup = (ViewGroup) mDrawerViewGroup.getChildAt(tag[KEY_NESTING_LEVEL] + 1);
            descendantsViewGroup.setVisibility(View.VISIBLE);
            Log.e("Child count", descendantsViewGroup.getChildCount() + "");
            for (int position = 0; position < descendantsViewGroup.getChildCount(); position++) {
                View view = descendantsViewGroup.getChildAt(position);
                int[] tag1 = (int[]) view.getTag();
               if (tag[KEY_POSITION] == tag1[KEY_PARENT_POSITION]) {
                    view.setVisibility(View.VISIBLE);
              }
               else {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    //show only first level menu items
    private void showInitialState() {
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.SECOND_LEVEL.ordinal()).setVisibility(View.GONE);
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.THIRD_LEVEL.ordinal()).setVisibility(View.GONE);
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.FOURTH_LEVEL.ordinal()).setVisibility(View.GONE);
    }

    //load JSON initial data
    private String loadJsonData() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    //read JSON initial data
    private void parseJsonData() {
        String jsonData = loadJsonData();
        try {
            //create container for first-level menu items
            LinearLayout firstLevelViewGroup = new LinearLayout(getActivity());
            //enable layout transition
            firstLevelViewGroup.setLayoutTransition(new LayoutTransition());
            //set orientation for LinearLayout
            firstLevelViewGroup.setOrientation(LinearLayout.VERTICAL);

            //create container for second-level menu items
            LinearLayout secondLevelViewGroup = new LinearLayout(getActivity());
            //enable layout transition
            secondLevelViewGroup.setLayoutTransition(new LayoutTransition());
            //set orientation for LinearLayout
            secondLevelViewGroup.setOrientation(LinearLayout.VERTICAL);

            //create container for third-level menu items
            LinearLayout thirdLevelViewGroup = new LinearLayout(getActivity());
            //enable layout transition
            thirdLevelViewGroup.setLayoutTransition(new LayoutTransition());
            //set orientation for LinearLayout
            thirdLevelViewGroup.setOrientation(LinearLayout.VERTICAL);

            //create container for fourth-level menu items
            LinearLayout fourthLevelViewGroup = new LinearLayout(getActivity());
            //enable layout transition
            fourthLevelViewGroup.setLayoutTransition(new LayoutTransition());
            //set orientation for LinearLayout
            fourthLevelViewGroup.setOrientation(LinearLayout.VERTICAL);

            JSONArray firstLvlArray = new JSONArray(jsonData);
            int secondLvl=0,thirdLvl=0,fourthLvl=0;
            for (int firstLvl = 0; firstLvl < firstLvlArray.length(); firstLvl++) {
                JSONObject firstLvlObj = firstLvlArray.getJSONObject(firstLvl);
                if (firstLvlObj.has(TAG_TITLE)) {
                    String title = firstLvlObj.get(TAG_TITLE).toString();
                    firstLevelViewGroup.addView(createMenuItem(title, Utils.NESTING_LEVEL.FIRST_LEVEL.ordinal(), firstLvl, -1));
                }
                if (firstLvlObj.has(TAG_CHILDREN)) {
                    JSONArray secondLvlArray = firstLvlObj.getJSONArray(TAG_CHILDREN);
                    for (int j = 0; j < secondLvlArray.length(); j++) {

                        JSONObject secondLvlObj = secondLvlArray.getJSONObject(j);
                        if (secondLvlObj.has(TAG_TITLE)) {
                            String title = secondLvlObj.get(TAG_TITLE).toString();
                            secondLevelViewGroup.addView(createMenuItem(title, Utils.NESTING_LEVEL.SECOND_LEVEL.ordinal(), secondLvl, firstLvl));
                        }
                        if (secondLvlObj.has(TAG_CHILDREN)) {
                            JSONArray thirdLvlArray = secondLvlObj.getJSONArray(TAG_CHILDREN);
                            for (int k = 0; k < thirdLvlArray.length(); k++) {

                                JSONObject thirdLvlObj = thirdLvlArray.getJSONObject(k);
                                if (thirdLvlObj.has(TAG_TITLE)) {
                                    String title = thirdLvlObj.get(TAG_TITLE).toString();
                                    thirdLevelViewGroup.addView(createMenuItem(title, Utils.NESTING_LEVEL.THIRD_LEVEL.ordinal(), thirdLvl, secondLvl));
                                }
                                if (thirdLvlObj.has(TAG_CHILDREN)) {
                                    JSONArray fourthLvlArray = thirdLvlObj.getJSONArray(TAG_CHILDREN);
                                    for (int m = 0; m < fourthLvlArray.length(); m++) {

                                        JSONObject fourthLvlObj = fourthLvlArray.getJSONObject(m);
                                        if (fourthLvlObj.has(TAG_TITLE)) {
                                            String title = fourthLvlObj.get(TAG_TITLE).toString();
                                            fourthLevelViewGroup.addView(createMenuItem(title, Utils.NESTING_LEVEL.FOURTH_LEVEL.ordinal(), fourthLvl, thirdLvl));
                                        }
                                        fourthLvl++;
                                    }
                                }
                                thirdLvl++;
                            }
                        }
                        secondLvl++;
                    }
                }
            }
            mDrawerViewGroup.addView(firstLevelViewGroup);
            mDrawerViewGroup.addView(secondLevelViewGroup);
            mDrawerViewGroup.addView(thirdLevelViewGroup);
            mDrawerViewGroup.addView(fourthLevelViewGroup);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //create menu item with particular tag and OnClickListener
    private TextView createMenuItem(String title, int level, int position, int parentPosition) {
        TextView view = new TextView(getActivity());
        view.setPadding(TEXT_VIEW_PADDING, TEXT_VIEW_PADDING, TEXT_VIEW_PADDING, TEXT_VIEW_PADDING);
        view.setText(title);
        int[] tag = new int[3];
        tag[KEY_NESTING_LEVEL] = level;
        tag[KEY_POSITION] = position;
        tag[KEY_PARENT_POSITION] = parentPosition;
        view.setTag(tag);
        view.setOnClickListener(this);
        return view;
    }
}
