package com.tomclaw.filepicker.util;

import android.graphics.ColorFilter;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by solkin on 21.06.2017.
 */
public class GroupMenuListener implements MenuItem.OnMenuItemClickListener {

    private Menu menu;
    private ColorFilter inactiveColorFilter;
    private ColorFilter activeColorFilter;

    public GroupMenuListener(Menu menu, ColorFilter inactiveColorFilter, ColorFilter activeColorFilter) {
        this.menu = menu;
        this.inactiveColorFilter = inactiveColorFilter;
        this.activeColorFilter = activeColorFilter;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        for (int c = 0; c < menu.size(); c++) {
            MenuItem item = menu.getItem(c);
            if (item.isCheckable()) {
                item.getIcon().setColorFilter(inactiveColorFilter);
            }
        }
        menuItem.getIcon().setColorFilter(activeColorFilter);
        return false;
    }
}
