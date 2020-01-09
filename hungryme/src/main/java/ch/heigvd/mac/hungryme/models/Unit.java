package ch.heigvd.mac.hungryme.models;

import java.util.regex.*;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public enum Unit {
    NONE {
        @Override
        public String toString() {
            return "unit";
        }
    },
    SPOON {
        @Override
        public String toString() {
            return "spoon";
        }
    },
    CUP {
        @Override
        public String toString() {
            return "cup";
        }
    },
    POUND {
        @Override
        public String toString() {
            return "lb";
        }
    },  // lb
    ONCE {
        @Override
        public String toString() {
            return "oz";
        }
    };   // oz

    public static Unit getUnitFromString(String s) {
        Pattern spoonPattern = Pattern.compile("(sp(oon)?)$", CASE_INSENSITIVE);
        Pattern cupPattern = Pattern.compile("(c(up)?)$",CASE_INSENSITIVE);
        Pattern poundPattern = Pattern.compile("(pound|lb)$", CASE_INSENSITIVE);
        Pattern oncePattern = Pattern.compile("(once(s)?|oz)$", CASE_INSENSITIVE);

        if(spoonPattern.matcher(s).find()) {
            return SPOON;
        } else if(cupPattern.matcher(s).find()) {
            return CUP;
        } else if(poundPattern.matcher(s).find()) {
            return POUND;
        }else if(oncePattern.matcher(s).find()) {
            return ONCE;
        } else {
            return NONE;
        }
    }
}
