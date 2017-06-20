package com.tomclaw.filepicker.util;

import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuItem;

import com.tomclaw.filepicker.R;

/**
 * Created by solkin on 20.06.2017.
 */
public class MenuHelper {

    private Menu menu;
    private ColorFilter inactiveColorFilter;
    private ColorFilter activeColorFilter;
    private int index = 0;

    public MenuHelper(Resources resources, Menu menu) {
        inactiveColorFilter = createColorFilter(resources, R.color.grey);
        activeColorFilter = createColorFilter(resources, R.color.primary_color);
        this.menu = menu;
    }

    public void addMenuItem(int groupId, @StringRes int titleRes, @DrawableRes int iconRes, boolean isChecked) {
        GroupMenuListener listener = new GroupMenuListener(menu, inactiveColorFilter, activeColorFilter);
        MenuItem recent = menu.add(groupId, index, index++, titleRes)
                .setIcon(iconRes)
                .setOnMenuItemClickListener(listener)
                .setChecked(isChecked);
        if (isChecked) {
            recent.getIcon().setColorFilter(inactiveColorFilter);
        }
    }

    public int getIndex() {
        return index;
    }

    private static ColorFilter createColorFilter(Resources resources, @ColorRes int color) {
        return new PorterDuffColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_ATOP);
    }
}
