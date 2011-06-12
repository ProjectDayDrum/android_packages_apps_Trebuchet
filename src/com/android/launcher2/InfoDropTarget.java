/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.View;

import com.android.launcher.R;

public class InfoDropTarget extends ButtonDropTarget {

    private int mHoverColor = 0xFF0000FF;

    public InfoDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Get the hover color
        Resources r = getResources();
        mHoverColor = r.getColor(R.color.info_target_hover_tint);
        mHoverPaint.setColorFilter(new PorterDuffColorFilter(
                mHoverColor, PorterDuff.Mode.SRC_ATOP));
        setBackgroundColor(mHoverColor);
        getBackground().setAlpha(0);
    }

    private boolean isApplication(Object info) {
        if (info instanceof ApplicationInfo) return true;
        return (((ItemInfo) info).itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION);
    }

    @Override
    public boolean acceptDrop(DragObject d) {
        // acceptDrop is called just before onDrop. We do the work here, rather than
        // in onDrop, because it allows us to reject the drop (by returning false)
        // so that the object being dragged isn't removed from the drag source.
        ComponentName componentName = null;
        if (d.dragInfo instanceof ApplicationInfo) {
            componentName = ((ApplicationInfo) d.dragInfo).componentName;
        } else if (d.dragInfo instanceof ShortcutInfo) {
            componentName = ((ShortcutInfo) d.dragInfo).intent.getComponent();
        }
        if (componentName != null) {
            mLauncher.startApplicationDetailsActivity(componentName);
        }
        return false;
    }

    @Override
    public void onDragStart(DragSource source, Object info, int dragAction) {
        ItemInfo item = (ItemInfo) info;
        boolean isVisible = true;

        // If we are dragging a widget or shortcut, hide the info target
        if (!isApplication(info)) {
            isVisible = false;
        }

        mActive = isVisible;
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDragEnd() {
        super.onDragEnd();
        mActive = false;
    }

    public void onDragEnter(DragObject d) {
        super.onDragEnter(d);

        ObjectAnimator anim = ObjectAnimator.ofInt(getBackground(), "alpha",
                Color.alpha(mHoverColor));
        anim.setDuration(mTransitionDuration);
        anim.start();
    }

    public void onDragExit(DragObject d) {
        super.onDragExit(d);

        ObjectAnimator anim = ObjectAnimator.ofInt(getBackground(), "alpha", 0);
        anim.setDuration(mTransitionDuration);
        anim.start();
    }
}
