// Generated code from Butter Knife. Do not modify!
package com.chejiaozhen.weex;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class CropActivity$$ViewBinder<T extends com.chejiaozhen.weex.CropActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492977, "field 'mUCropView'");
    target.mUCropView = finder.castView(view, 2131492977, "field 'mUCropView'");
    view = finder.findRequiredView(source, 2131492978, "field 'mSaveFab'");
    target.mSaveFab = finder.castView(view, 2131492978, "field 'mSaveFab'");
  }

  @Override public void unbind(T target) {
    target.mUCropView = null;
    target.mSaveFab = null;
  }
}
