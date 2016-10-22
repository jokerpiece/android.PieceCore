package jp.co.jokerpiece.piecebase.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Antifuture on 2016/9/30.
 */

public class DateValidator
{
    private Pattern pattern;
    private Matcher matcher;

    private static final String DATE_PATTERN =
            "((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";


    public DateValidator()
    {
        pattern = Pattern.compile(DATE_PATTERN);
    }

    /**
     * Validate date format with regular expression
     * @param date date address for validation
     * @return true valid date format, false invalid date format
     */
    public boolean validate(final String date) {

        matcher = pattern.matcher(date);
        return matcher.matches();

    }
}
