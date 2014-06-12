package com.yalantis.androidtechtest.app.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.yalantis.androidtechtest.app.R;
import com.yalantis.androidtechtest.app.utils.ClickEventsProcessor;
import com.yalantis.androidtechtest.app.utils.Consts;
import com.yalantis.androidtechtest.app.utils.JSONWorker;
import com.yalantis.androidtechtest.app.utils.Utils;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ScrollView mDrawerScrollView;
    private ViewGroup mDrawerViewGroup;
    private View mFragmentContainerView;

    //color of selected main menu item
    private int mCurrentColor;
    //currently selected view
    private View mSelectedView;
    private ClickEventsProcessor mProcessor;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerScrollView = (ScrollView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerViewGroup = (ViewGroup) mDrawerScrollView.findViewById(R.id.nav_drawer_view_group);
        mDrawerViewGroup = new JSONWorker(getActivity(), mDrawerViewGroup, new CustomClickListener()).parseJsonData();
        return mDrawerScrollView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showInitialState();
        mProcessor = new ClickEventsProcessor(getActivity(), mDrawerViewGroup);
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
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /*
    show menu items only on first level and hide all other
     */
    private void showOnlyFirstLevelMenu(View v) {
        //hide zero level menu item
        v.setVisibility(View.GONE);
        //retrieve container for first level menu items
        ViewGroup firstLvlViewGroup = (ViewGroup) mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.FIRST_LEVEL.ordinal());
        firstLvlViewGroup.setVisibility(View.VISIBLE);
        for (int position = 0; position < firstLvlViewGroup.getChildCount(); position++) {
            View view = firstLvlViewGroup.getChildAt(position);
            view.setVisibility(View.VISIBLE);
            view.setBackgroundColor(getResources().getColor(Consts.MENU_CATEGORY_COLORS[position]));
            ((TextView) view.findViewById(R.id.text_view)).setTextColor(Color.WHITE);
            ((TextView)view.findViewById(R.id.text_view)).setTypeface(null, Typeface.BOLD);
        }
        //hide second level container
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.SECOND_LEVEL.ordinal()).setVisibility(View.GONE);
        //hide third level container
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.THIRD_LEVEL.ordinal()).setVisibility(View.GONE);
        //hide fourth level container
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.FOURTH_LEVEL.ordinal()).setVisibility(View.GONE);
    }

    //hide all menu items from underlying levels
    private void hidePreviousLevels(int nestingLevel) {
        for (int position = nestingLevel + 2; position <= Utils.NESTING_LEVEL.FOURTH_LEVEL.ordinal(); position++) {
            mDrawerViewGroup.getChildAt(position).setVisibility(View.GONE);
        }
    }

    //hide all elements from the same level
    private void hideElementsFromSameLevel(int nestingLevel, int itemPosition) {
        ViewGroup nestedViewGroup = (ViewGroup) mDrawerViewGroup.getChildAt(nestingLevel);
        for (int position = 0; position < nestedViewGroup.getChildCount(); position++) {
            if (position != itemPosition) {
                nestedViewGroup.getChildAt(position).setVisibility(View.GONE);
            }
        }
    }

    //change background and text color for currently selected view
    private void changeCurrentlySelectedViewStyle(View v) {
        v.setBackgroundColor(getResources().getColor(mCurrentColor));
        ((TextView) v.findViewById(R.id.text_view)).setTextColor(Color.WHITE);
        ((TextView)v.findViewById(R.id.text_view)).setTypeface(null, Typeface.BOLD);
    }

    //change background and text color for previously selected view
    private void changePreviouslySelectedViewStyle(View v) {
        if (mSelectedView != null && !mSelectedView.equals(v)) {
            mSelectedView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            ((TextView) mSelectedView.findViewById(R.id.text_view)).setTextColor(getResources().getColor(R.color.menu_upper_level_item));
            ((TextView) mSelectedView.findViewById(R.id.text_view)).setTypeface(null, Typeface.BOLD);
        }
    }

    //show child elements for currently selected menu item
    private void showChildElements(int nestingLevel, int itemPosition) {
        if (nestingLevel + 1 < mDrawerViewGroup.getChildCount()) {
            ViewGroup descendantsViewGroup = (ViewGroup) mDrawerViewGroup.getChildAt(nestingLevel + 1);
            descendantsViewGroup.setVisibility(View.VISIBLE);
            for (int position = 0; position < descendantsViewGroup.getChildCount(); position++) {
                LinearLayout view = (LinearLayout) descendantsViewGroup.getChildAt(position);
                int[] tag1 = (int[]) view.getTag();
                if (itemPosition == tag1[Consts.KEY_PARENT_POSITION]) {
                    view.setVisibility(View.VISIBLE);
                    ((TextView)view.findViewById(R.id.text_view)).setTextColor(getResources().getColor(mCurrentColor));
                    ((TextView)view.findViewById(R.id.text_view)).setTypeface(null, Typeface.BOLD);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    //show only first level menu items
    private void showInitialState() {
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.ZERO_LEVEL.ordinal()).setVisibility(View.GONE);
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.SECOND_LEVEL.ordinal()).setVisibility(View.GONE);
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.THIRD_LEVEL.ordinal()).setVisibility(View.GONE);
        mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.FOURTH_LEVEL.ordinal()).setVisibility(View.GONE);

        ViewGroup firstLvlContainer = (ViewGroup)mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.FIRST_LEVEL.ordinal());
        for(int position = 0; position < firstLvlContainer.getChildCount(); position++) {
            View view = firstLvlContainer.getChildAt(position);
            view.setBackgroundColor(getResources().getColor(Consts.MENU_CATEGORY_COLORS[position]));
            view.setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.text_view)).setTextColor(Color.WHITE);
            ((TextView)view.findViewById(R.id.text_view)).setTypeface(null, Typeface.BOLD);
        }

    }

    public class CustomClickListener implements View.OnClickListener{


        @Override
        public void onClick(View v) {
            //retrieve selected view tag
            int[] tag = (int[]) v.getTag();

            if (mSelectedView == null || !mSelectedView.equals(v) || (mSelectedView.equals(v) && tag[0] == Utils.NESTING_LEVEL.FIRST_LEVEL.ordinal())) {
                //save color for menu items text
                if (tag[Consts.KEY_NESTING_LEVEL] == Utils.NESTING_LEVEL.FIRST_LEVEL.ordinal()) {
                    mCurrentColor = Consts.MENU_CATEGORY_COLORS[tag[Consts.KEY_POSITION]];
                }

                if (tag[Consts.KEY_NESTING_LEVEL] == Utils.NESTING_LEVEL.ZERO_LEVEL.ordinal()) {
                    mProcessor.showOnlyFirstLevelMenu(v);
                } else {
                    //change background and text color for selected view
                    mProcessor.changeCurrentlySelectedViewStyle(v, mCurrentColor);
                    //change background and text color for previously selected view
                    mProcessor.changePreviouslySelectedViewStyle(v, mSelectedView);

                    //save new selected view
                    mSelectedView = v;
                    //make visible zero level menu item
                    mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.ZERO_LEVEL.ordinal()).setVisibility(View.VISIBLE);

                    //hide all elements from the same level
                    mProcessor.hideElementsFromSameLevel(tag[Consts.KEY_NESTING_LEVEL], tag[Consts.KEY_POSITION]);

                    //hide all elements from the previous levels
                    mProcessor.hidePreviousLevels(tag[Consts.KEY_NESTING_LEVEL]);

                    //show belong elements from next level (current_level + 1)
                    mProcessor.showChildElements(tag[Consts.KEY_NESTING_LEVEL], tag[Consts.KEY_POSITION], mCurrentColor);
                }
            }
        }
    }
}


