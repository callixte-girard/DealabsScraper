package callixtegirard.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Date
{
    // dates and time for naming csv exports
    private static final String safeFormat = "yyyy-MM-dd_HH-mm-ss"; // ugly but avoids problems with Windows
    public static final String nowFormatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern(safeFormat));
}
