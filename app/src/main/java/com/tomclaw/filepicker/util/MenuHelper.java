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

    public MenuItem addMenuItem(int groupId, @StringRes int titleRes, @DrawableRes int iconRes) {
        GroupMenuListener listener = new GroupMenuListener(menu, inactiveColorFilter, activeColorFilter);
        return menu.add(groupId, index, index++, titleRes)
                .setIcon(iconRes)
                .setOnMenuItemClickListener(listener);
    }

    public int getIndex() {
        return index;
    }

    public void markChecked(MenuItem menuItem) {
        menuItem.setCheckable(true);
        menuItem.setChecked(true);
        menuItem.getIcon().setColorFilter(activeColorFilter);
    }

    private static ColorFilter createColorFilter(Resources resources, @ColorRes int color) {
        return new PorterDuffColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_ATOP);
    }
}
