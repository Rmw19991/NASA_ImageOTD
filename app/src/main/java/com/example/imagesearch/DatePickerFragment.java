package com.example.imagesearch;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    private String dateString;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getContext(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        /*
            When the date is set this string will be initialized with the year, month and day values
            month needs to be incremented by one here or else the image you get will be a month
            behind the date you selected
        */
        dateString = view.getYear() + "-" + (view.getMonth()+1) + "-" + view.getDayOfMonth();
    }

    // This method returns the dateString so it can be used in the URL for the search query
    public String getDateString()
    {
        return this.dateString;
    }
}