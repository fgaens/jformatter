package be.codesolutions;

import jakarta.inject.Singleton;

@Singleton
public class FormatService {

    public String formatJavaObject(String input) {

        StringBuilder formatted = new StringBuilder();
        int indentLevel = 0;
        boolean inQuotes = false;

        for (char c : input.toCharArray()) {
            if (c == '(' || c == '[') {
                formatted.append(c).append("\n");
                indentLevel++;
                formatted.append("    ".repeat(indentLevel));
            } else if (c == ')' || c == ']') {
                formatted.append("\n");
                indentLevel--;
                formatted.append("    ".repeat(indentLevel)).append(c);
            } else if (c == ',') {
                formatted.append(c).append("\n");
                formatted.append("    ".repeat(indentLevel));
            } else if (c == '=') {
                formatted.append(" = ");
            } else {
                if (c == '"') inQuotes = !inQuotes;
                formatted.append(c);
            }
        }

        return formatted.toString();
    }
}
