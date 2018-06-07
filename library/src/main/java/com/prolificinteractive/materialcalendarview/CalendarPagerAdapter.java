package com.prolificinteractive.materialcalendarview;

import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView.ShowOtherDates;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pager adapter backing the calendar view
 */
abstract class CalendarPagerAdapter<V extends CalendarPagerView> extends PagerAdapter {

    private final ArrayDeque<V> currentViews;

    protected final MaterialCalendarView mcv;
    private final CalendarDay today;

    private TitleFormatter titleFormatter = null;
    private Integer color = null;
    private Integer currentDateTextColor = null;
    private Integer dateTextColor = null;
    private Integer disabledDateOnThisMonthTextColor = null;
    private String currentDateTextTypeface = null;
    private Integer dateTextAppearance = null;
    private String dateTextTypeface = null;
    private String disabledDateOnThisMonthTypeface = null;
    private Integer weekDayTextAppearance = null;
    private String weekDayTextTypeface = null;
    @ShowOtherDates
    private int showOtherDates = MaterialCalendarView.SHOW_DEFAULTS;
    private CalendarDay minDate = null;
    private CalendarDay maxDate = null;
    private DateRangeIndex rangeIndex;
    private List<CalendarDay> selectedDates = new ArrayList<>();
    private WeekDayFormatter weekDayFormatter = WeekDayFormatter.DEFAULT;
    private DayFormatter dayFormatter = DayFormatter.DEFAULT;
    private List<DayViewDecorator> decorators = new ArrayList<>();
    private List<DecoratorResult> decoratorResults = null;
    private boolean selectionEnabled = true;

    CalendarPagerAdapter(MaterialCalendarView mcv) {
        this.mcv = mcv;
        this.today = CalendarDay.today();
        currentViews = new ArrayDeque<>();
        currentViews.iterator();
        setRangeDates(null, null);
    }

    public void setDecorators(List<DayViewDecorator> decorators) {
        this.decorators = decorators;
        invalidateDecorators();
    }

    public void invalidateDecorators() {
        decoratorResults = new ArrayList<>();
        for (DayViewDecorator decorator : decorators) {
            DayViewFacade facade = new DayViewFacade();
            decorator.decorate(facade);
            if (facade.isDecorated()) {
                decoratorResults.add(new DecoratorResult(decorator, facade));
            }
        }
        for (V pagerView : currentViews) {
            pagerView.setDayViewDecorators(decoratorResults);
        }
    }

    @Override
    public int getCount() {
        return rangeIndex.getCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleFormatter == null ? "" : titleFormatter.format(getItem(position));
    }

    public CalendarPagerAdapter<?> migrateStateAndReturn(CalendarPagerAdapter<?> newAdapter) {
        newAdapter.titleFormatter = titleFormatter;
        newAdapter.color = color;
        newAdapter.currentDateTextColor = currentDateTextColor;
        newAdapter.dateTextColor = dateTextColor;
        newAdapter.disabledDateOnThisMonthTextColor = disabledDateOnThisMonthTextColor;
        newAdapter.currentDateTextTypeface = currentDateTextTypeface;
        newAdapter.dateTextAppearance = dateTextAppearance;
        newAdapter.dateTextTypeface = dateTextTypeface;
        newAdapter.disabledDateOnThisMonthTypeface = disabledDateOnThisMonthTypeface;
        newAdapter.weekDayTextAppearance = weekDayTextAppearance;
        newAdapter.weekDayTextTypeface = weekDayTextTypeface;
        newAdapter.showOtherDates = showOtherDates;
        newAdapter.minDate = minDate;
        newAdapter.maxDate = maxDate;
        newAdapter.selectedDates = selectedDates;
        newAdapter.weekDayFormatter = weekDayFormatter;
        newAdapter.dayFormatter = dayFormatter;
        newAdapter.decorators = decorators;
        newAdapter.decoratorResults = decoratorResults;
        newAdapter.selectionEnabled = selectionEnabled;
        return newAdapter;
    }

    public int getIndexForDay(CalendarDay day) {
        if (day == null) {
            return getCount() / 2;
        }
        if (minDate != null && day.isBefore(minDate)) {
            return 0;
        }
        if (maxDate != null && day.isAfter(maxDate)) {
            return getCount() - 1;
        }
        return rangeIndex.indexOf(day);
    }

    protected abstract V createView(int position);

    protected abstract int indexOf(V view);

    protected abstract boolean isInstanceOfView(Object object);

    protected abstract DateRangeIndex createRangeIndex(CalendarDay min, CalendarDay max);

    @Override
    public int getItemPosition(Object object) {
        if (!(isInstanceOfView(object))) {
            return POSITION_NONE;
        }
        CalendarPagerView pagerView = (CalendarPagerView) object;
        CalendarDay firstViewDay = pagerView.getFirstViewDay();
        if (firstViewDay == null) {
            return POSITION_NONE;
        }
        int index = indexOf((V) object);
        if (index < 0) {
            return POSITION_NONE;
        }
        return index;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        V pagerView = createView(position);
        pagerView.setContentDescription(mcv.getCalendarContentDescription());
        pagerView.setAlpha(0);
        pagerView.setSelectionEnabled(selectionEnabled);

        pagerView.setWeekDayFormatter(weekDayFormatter);
        pagerView.setDayFormatter(dayFormatter);
        if (color != null) {
            pagerView.setSelectionColor(color);
        }
        if (dateTextColor != null) {
            pagerView.setDateTextColor(dateTextColor);
        }  // current date text color after date text color
        if (currentDateTextColor != null) {
            pagerView.setCurrentDateTextColor(currentDateTextColor);
        }
        if (disabledDateOnThisMonthTextColor != null) {
            pagerView.setDisabledDateOnThisMonthTextColor(disabledDateOnThisMonthTextColor);
        }
        if (dateTextAppearance != null) {
            pagerView.setDateTextAppearance(dateTextAppearance);
        }
        if (dateTextTypeface != null) {
            pagerView.setDateTextTypeface(obtainTypeface(dateTextTypeface));
        }  // current date typeface after date typeface
        if (currentDateTextTypeface != null) {
            pagerView.setCurrentDateTextTypeface(obtainTypeface(currentDateTextTypeface));
        }
        if (disabledDateOnThisMonthTypeface != null) {
            pagerView.setDisabledDateOnThisMonthTypeface(obtainTypeface(disabledDateOnThisMonthTypeface));
        }
        if (weekDayTextAppearance != null) {
            pagerView.setWeekDayTextAppearance(weekDayTextAppearance);
        }
        if (weekDayTextTypeface != null) {
            pagerView.setWeekDayTextTypeface(obtainTypeface(weekDayTextTypeface));
        }
        pagerView.setShowOtherDates(showOtherDates);
        pagerView.setMinimumDate(minDate);
        pagerView.setMaximumDate(maxDate);
        pagerView.setSelectedDates(selectedDates);

        container.addView(pagerView);
        currentViews.add(pagerView);

        pagerView.setDayViewDecorators(decoratorResults);

        return pagerView;
    }

    public void setSelectionEnabled(boolean enabled) {
        selectionEnabled = enabled;
        for (V pagerView : currentViews) {
            pagerView.setSelectionEnabled(selectionEnabled);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        CalendarPagerView pagerView = (CalendarPagerView) object;
        currentViews.remove(pagerView);
        container.removeView(pagerView);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setTitleFormatter(@NonNull TitleFormatter titleFormatter) {
        this.titleFormatter = titleFormatter;
    }

    public void setSelectionColor(int color) {
        this.color = color;
        for (V pagerView : currentViews) {
            pagerView.setSelectionColor(color);
        }
    }

    public void setCurrentDateTextTypeface(String tf) {
        if (TextUtils.isEmpty(tf)) {
            return;
        }
        this.currentDateTextTypeface = tf;
        Typeface typeface = obtainTypeface(tf);
        for (V pagerView : currentViews) {
            pagerView.setCurrentDateTextTypeface(typeface);
        }
    }

    public void setDateTextAppearance(int taId) {
        if (taId == 0) {
            return;
        }
        this.dateTextAppearance = taId;
        for (V pagerView : currentViews) {
            pagerView.setDateTextAppearance(taId);
        }
    }

    public void setDateTextTypeface(String tf) {
        if (TextUtils.isEmpty(tf)) {
            return;
        }
        this.dateTextTypeface = tf;
        Typeface typeface = obtainTypeface(tf);
        for (V pagerView : currentViews) {
            pagerView.setDateTextTypeface(typeface);
        }
    }

    public void setDisabledDateOnThisMonthTypeface(String tf) {
        if (TextUtils.isEmpty(tf)) {
            return;
        }
        this.disabledDateOnThisMonthTypeface = tf;
        Typeface typeface = obtainTypeface(tf);
        for (V pagerView : currentViews) {
            pagerView.setDisabledDateOnThisMonthTypeface(typeface);
        }
    }

    public void setCurrentDateTextColor(@ColorInt int color) {
        if (color == 0) {
            return;
        }
        this.currentDateTextColor = color;
        for (V pagerView : currentViews) {
            pagerView.setCurrentDateTextColor(color);
        }
    }

    public void setDateTextColor(@ColorInt int color) {
        if (color == 0) {
            return;
        }
        this.dateTextColor = color;
        for (V pagerView : currentViews) {
            pagerView.setDateTextColor(color);
        }
    }

    public void setDisabledDateOnThisMonthTextColor(@ColorInt int color) {
        if (color == 0) {
            return;
        }
        this.disabledDateOnThisMonthTextColor = color;
        for (V pagerView : currentViews) {
            pagerView.setDisabledDateOnThisMonthTextColor(color);
        }
    }

    public void setShowOtherDates(@ShowOtherDates int showFlags) {
        this.showOtherDates = showFlags;
        for (V pagerView : currentViews) {
            pagerView.setShowOtherDates(showFlags);
        }
    }

    public void setWeekDayFormatter(WeekDayFormatter formatter) {
        this.weekDayFormatter = formatter;
        for (V pagerView : currentViews) {
            pagerView.setWeekDayFormatter(formatter);
        }
    }

    public void setDayFormatter(DayFormatter formatter) {
        this.dayFormatter = formatter;
        for (V pagerView : currentViews) {
            pagerView.setDayFormatter(formatter);
        }
    }

    @ShowOtherDates
    public int getShowOtherDates() {
        return showOtherDates;
    }

    public void setWeekDayTextAppearance(int taId) {
        if (taId == 0) {
            return;
        }
        this.weekDayTextAppearance = taId;
        for (V pagerView : currentViews) {
            pagerView.setWeekDayTextAppearance(taId);
        }
    }

    public void setWeekDayTextTypeface(String tf) {
        if (TextUtils.isEmpty(tf)) {
            return;
        }
        this.weekDayTextTypeface = tf;
        Typeface typeface = obtainTypeface(tf);
        for (V pagerView : currentViews) {
            pagerView.setWeekDayTextTypeface(typeface);
        }
    }

    public void setRangeDates(CalendarDay min, CalendarDay max) {
        this.minDate = min;
        this.maxDate = max;
        for (V pagerView : currentViews) {
            pagerView.setMinimumDate(min);
            pagerView.setMaximumDate(max);
        }

        if (min == null) {
            min = CalendarDay.from(today.getYear() - 200, today.getMonth(), today.getDay());
        }

        if (max == null) {
            max = CalendarDay.from(today.getYear() + 200, today.getMonth(), today.getDay());
        }

        rangeIndex = createRangeIndex(min, max);

        notifyDataSetChanged();
        invalidateSelectedDates();
    }

    public DateRangeIndex getRangeIndex() {
        return rangeIndex;
    }

    public void clearSelections() {
        selectedDates.clear();
        invalidateSelectedDates();
    }

    public void setDateSelected(CalendarDay day, boolean selected) {
        if (selected) {
            if (!selectedDates.contains(day)) {
                selectedDates.add(day);
                invalidateSelectedDates();
            }
        } else {
            if (selectedDates.contains(day)) {
                selectedDates.remove(day);
                invalidateSelectedDates();
            }
        }
    }

    private void invalidateSelectedDates() {
        validateSelectedDates();
        for (V pagerView : currentViews) {
            pagerView.setSelectedDates(selectedDates);
        }
    }

    private void validateSelectedDates() {
        for (int i = 0; i < selectedDates.size(); i++) {
            CalendarDay date = selectedDates.get(i);

            if ((minDate != null && minDate.isAfter(date)) || (maxDate != null && maxDate.isBefore(date))) {
                selectedDates.remove(i);
                mcv.onDateUnselected(date);
                i -= 1;
            }
        }
    }

    public CalendarDay getItem(int position) {
        return rangeIndex.getItem(position);
    }

    @NonNull
    public List<CalendarDay> getSelectedDates() {
        return Collections.unmodifiableList(selectedDates);
    }

    protected int getCurrentDateTextColor() {
        return currentDateTextColor;
    }

    protected int getDateTextColor() {
        return dateTextColor;
    }

    protected int getDisabledDateOnThisMonthTextColor() {
        return disabledDateOnThisMonthTextColor;
    }

    protected String getCurrentDateTextTypeface() {
        return currentDateTextTypeface;
    }

    protected int getDateTextAppearance() {
        return dateTextAppearance == null ? 0 : dateTextAppearance;
    }

    protected String getDateTextTypeface() {
        return dateTextTypeface;
    }

    protected String getDisabledDateOnThisMonthTypeface() {
        return disabledDateOnThisMonthTypeface;
    }

    protected int getWeekDayTextAppearance() {
        return weekDayTextAppearance == null ? 0 : weekDayTextAppearance;
    }

    protected String getWeekDayTextTypeface() {
        return weekDayTextTypeface;
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    private Typeface obtainTypeface(String tf) {
        return TypefaceStore.getInstance(mcv.getContext()).get(tf);
    }
}
