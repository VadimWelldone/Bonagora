package com.yalantis.androidtechtest.app.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yalantis.androidtechtest.app.R;

/**
 * Created by vadim on 6/12/14.
 */
public class ClickEventsProcessor {

    /*
    variables
     */
    private Context mContext;
    private ViewGroup mDrawerViewGroup;

    public ClickEventsProcessor(Context context, ViewGroup container) {
        mContext = context;
        mDrawerViewGroup = container;
    }

    /*
    show menu items only on first level and hide all other
     */
    public void showOnlyFirstLevelMenu(View v) {
        //hide zero level menu item
        v.setVisibility(View.GONE);
        //retrieve container for first level menu items
        ViewGroup firstLvlViewGroup = (ViewGroup) mDrawerViewGroup.getChildAt(Utils.NESTING_LEVEL.FIRST_LEVEL.ordinal());
        firstLvlViewGroup.setVisibility(View.VISIBLE);
        for (int position = 0; position < firstLvlViewGroup.getChildCount(); position++) {
            View view = firstLvlViewGroup.getChildAt(position);
            view.setVisibility(View.VISIBLE);
            view.setBackgroundColor(mContext.getResources().getColor(Consts.MENU_CATEGORY_COLORS[position]));
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
    public void hidePreviousLevels(int nestingLevel) {
        for (int position = nestingLevel + 2; position <= Utils.NESTING_LEVEL.FOURTH_LEVEL.ordinal(); position++) {
            mDrawerViewGroup.getChildAt(position).setVisibility(View.GONE);
        }
    }

    //hide all elements from the same level
    public void hideElementsFromSameLevel(int nestingLevel, int itemPosition) {
        ViewGroup nestedViewGroup = (ViewGroup) mDrawerViewGroup.getChildAt(nestingLevel);
        for (int position = 0; position < nestedViewGroup.getChildCount(); position++) {
            if (position != itemPosition) {
                nestedViewGroup.getChildAt(position).setVisibility(View.GONE);
            }
        }
    }

    //change background and text color for currently selected view
    public void changeCurrentlySelectedViewStyle(View v, int currentColor) {
        v.setBackgroundColor(mContext.getResources().getColor(currentColor));
        ((TextView) v.findViewById(R.id.text_view)).setTextColor(Color.WHITE);
        ((TextView)v.findViewById(R.id.text_view)).setTypeface(null, Typeface.BOLD);
    }

    //change background and text color for previously selected view
    public void changePreviouslySelectedViewStyle(View v, View selectedView) {
        if (selectedView != null && !selectedView.equals(v)) {
            selectedView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            ((TextView) selectedView.findViewById(R.id.text_view)).setTextColor(mContext.getResources().getColor(R.color.menu_upper_level_item));
            ((TextView) selectedView.findViewById(R.id.text_view)).setTypeface(null, Typeface.BOLD);
        }
    }

    //show child elements for currently selected menu item
    public void showChildElements(int nestingLevel, int itemPosition, int currentColor) {
        if (nestingLevel + 1 < mDrawerViewGroup.getChildCount()) {
            ViewGroup descendantsViewGroup = (ViewGroup) mDrawerViewGroup.getChildAt(nestingLevel + 1);
            descendantsViewGroup.setVisibility(View.VISIBLE);
            for (int position = 0; position < descendantsViewGroup.getChildCount(); position++) {
                LinearLayout view = (LinearLayout) descendantsViewGroup.getChildAt(position);
                int[] tag1 = (int[]) view.getTag();
                if (itemPosition == tag1[Consts.KEY_PARENT_POSITION]) {
                    view.setVisibility(View.VISIBLE);
                    ((TextView)view.findViewById(R.id.text_view)).setTextColor(mContext.getResources().getColor(currentColor));
                    ((TextView)view.findViewById(R.id.text_view)).setTypeface(null, Typeface.BOLD);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

}
