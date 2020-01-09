public class Utils {

    public static String formatSeconds(int timeInSeconds)
    {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if(hours != 0) {
            formattedTime += hours + " h ";
        }

        if(minutes != 0) {
            formattedTime += minutes + " min. ";
        }
        if(seconds != 0) {
            formattedTime += seconds;
            formattedTime += " sec.";
        }

        return formattedTime;
    }
}
