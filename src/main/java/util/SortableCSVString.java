package util;

public class SortableCSVString implements Comparable<SortableCSVString> {
    public String value;
    int position;

    public SortableCSVString(String str, int pos) {
        this.value = str;
        this.position = pos;
    }

    public int compareTo(SortableCSVString anotherString) {
        String subString = this.value.substring(0, this.value.indexOf(59));
        String anotherSubString = anotherString.value.substring(0, anotherString.value.indexOf(59));
        int result = subString.compareTo(anotherSubString);
        if (result == 0) {
            if (this.position < anotherString.position) {
                return -1;
            } else {
                return this.position > anotherString.position ? 1 : 0;
            }
        } else {
            return result;
        }
    }
}
