package com.yalantis.androidtechtest.app.utils;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yalantis.androidtechtest.app.R;
import com.yalantis.androidtechtest.app.activity.MainActivity;
import com.yalantis.androidtechtest.app.fragment.NavigationDrawerFragment.CustomClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vadim on 6/12/14.
 */
public class JSONWorker {

    /*
    constants
     */
    //json tags
    private static final String TAG_TITLE = "title";
    private static final String TAG_CHILDREN = "children";
    //TextView padding
    private static final int TEXT_VIEW_PADDING = 30;
    //containers
    private LinearLayout mFirstLevelViewGroup;
    private LinearLayout mSecondLevelViewGroup;
    private LinearLayout mThirdLevelViewGroup;
    private LinearLayout mFourthLevelViewGroup;

    /*
    variables
     */
    private Context mContext;
    private ViewGroup mDrawerViewGroup;
    private CustomClickListener mListener;

    public JSONWorker(Context context, ViewGroup viewGroup, CustomClickListener listener) {
        mContext = context;
        mDrawerViewGroup = viewGroup;
        mListener = listener;
    }

    //load JSON initial data
    private String loadJsonData() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("data.json");
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
    public ViewGroup parseJsonData() {
        String jsonData = loadJsonData();
        try {
            createContainers();

            JSONArray firstLvlArray = new JSONArray(jsonData);
            int secondLvl = 0, thirdLvl = 0, fourthLvl = 0;
            for (int firstLvl = 0; firstLvl < firstLvlArray.length(); firstLvl++) {
                JSONObject firstLvlObj = processJSONObj(firstLvlArray.getJSONObject(firstLvl), mFirstLevelViewGroup, Utils.NESTING_LEVEL.FIRST_LEVEL.ordinal(), firstLvl, -1);
                if (firstLvlObj.has(TAG_CHILDREN)) {
                    JSONArray secondLvlArray = firstLvlObj.getJSONArray(TAG_CHILDREN);
                    for (int j = 0; j < secondLvlArray.length(); j++) {

                        JSONObject secondLvlObj = processJSONObj(secondLvlArray.getJSONObject(j), mSecondLevelViewGroup, Utils.NESTING_LEVEL.SECOND_LEVEL.ordinal(), secondLvl, firstLvl);
                        if (secondLvlObj.has(TAG_CHILDREN)) {
                            JSONArray thirdLvlArray = secondLvlObj.getJSONArray(TAG_CHILDREN);
                            for (int k = 0; k < thirdLvlArray.length(); k++) {

                                JSONObject thirdLvlObj = processJSONObj(thirdLvlArray.getJSONObject(k), mThirdLevelViewGroup, Utils.NESTING_LEVEL.THIRD_LEVEL.ordinal(), thirdLvl, secondLvl);
                                if (thirdLvlObj.has(TAG_CHILDREN)) {
                                    JSONArray fourthLvlArray = thirdLvlObj.getJSONArray(TAG_CHILDREN);
                                    for (int m = 0; m < fourthLvlArray.length(); m++) {

                                        processJSONObj(fourthLvlArray.getJSONObject(m), mFourthLevelViewGroup, Utils.NESTING_LEVEL.FOURTH_LEVEL.ordinal(), fourthLvl, thirdLvl);
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
            addContainers();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mDrawerViewGroup;
    }

    //create containers for each level menu items
    private void createContainers() {
        //create container for first-level menu items
        mFirstLevelViewGroup = new LinearLayout(mContext);

        //create layout transition
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, null);
        layoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, null);

        setContainerTransitionAndOrientation(mFirstLevelViewGroup, layoutTransition);

        //create container for second-level menu items
        mSecondLevelViewGroup = new LinearLayout(mContext);
        setContainerTransitionAndOrientation(mSecondLevelViewGroup, layoutTransition);

        //create container for third-level menu items
        mThirdLevelViewGroup = new LinearLayout(mContext);
        setContainerTransitionAndOrientation(mThirdLevelViewGroup, new LayoutTransition());

        //create container for fourth-level menu items
        mFourthLevelViewGroup = new LinearLayout(mContext);
        setContainerTransitionAndOrientation(mFourthLevelViewGroup, new LayoutTransition());
    }

    //add specific level containers into one container
    private void addContainers() {
        View view = createMenuItem(mContext.getString(R.string.menu_item_all_products), Utils.NESTING_LEVEL.ZERO_LEVEL.ordinal(), 0, 0, false);
        view.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        mDrawerViewGroup.addView(view);
        mDrawerViewGroup.addView(mFirstLevelViewGroup);
        mDrawerViewGroup.addView(mSecondLevelViewGroup);
        mDrawerViewGroup.addView(mThirdLevelViewGroup);
        mDrawerViewGroup.addView(mFourthLevelViewGroup);
    }

    //add specific level view to container
    private void addViewToContainer(View view, ViewGroup container) {
        view.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        container.addView(view);
    }

    //set LayoutTransition and LinearLayout orientation property for each level container
    private void setContainerTransitionAndOrientation(LinearLayout container, LayoutTransition transition) {
        //enable layout transition
        container.setLayoutTransition(transition);
        //set orientation for LinearLayout
        container.setOrientation(LinearLayout.VERTICAL);
    }

    //create menu item with particular tag and OnClickListener
    private LinearLayout createMenuItem(String title, int level, int position, int parentPosition, boolean isFirstLevel) {
        //inflate menu item
        LinearLayout layout = (LinearLayout)((MainActivity)mContext).getLayoutInflater().inflate(R.layout.custom_menu_item, null);

        //customize menu item text color and background
        TextView textView = (TextView)layout.findViewById(R.id.text_view);
        textView.setPadding(TEXT_VIEW_PADDING, TEXT_VIEW_PADDING, TEXT_VIEW_PADDING, TEXT_VIEW_PADDING);
        textView.setText(title);
        textView.setTextColor(mContext.getResources().getColor(R.color.menu_upper_level_item));
        textView.setTypeface(null, Typeface.BOLD);

        //define neediness of divider
        if(isFirstLevel) {
            layout.findViewById(R.id.divider).setVisibility(View.GONE);
        }

        //define and set tag for layout
        int[] tag = new int[3];
        tag[Consts.KEY_NESTING_LEVEL] = level;
        tag[Consts.KEY_POSITION] = position;
        tag[Consts.KEY_PARENT_POSITION] = parentPosition;
        layout.setTag(tag);

        //set listeners
        layout.setOnClickListener(mListener);

        return layout;
    }

    private JSONObject processJSONObj(JSONObject json, ViewGroup container, int nestingLevel, int position, int parentPosition) {
        try {
            if (json.has(TAG_TITLE)) {
                String title = json.get(TAG_TITLE).toString();
                boolean isFirstLevel = nestingLevel == Utils.NESTING_LEVEL.FIRST_LEVEL.ordinal() ? true : false;
                View view = createMenuItem(title, nestingLevel, position, parentPosition, isFirstLevel);
                if(isFirstLevel) {
                    view.setBackgroundColor(mContext.getResources().getColor(Consts.MENU_CATEGORY_COLORS[position]));
                    ((TextView) view.findViewById(R.id.text_view)).setTextColor(Color.WHITE);
                    ((TextView) view.findViewById(R.id.text_view)).setTypeface(null, Typeface.BOLD);
                }
                addViewToContainer(view, container);

            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
