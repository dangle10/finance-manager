
package pack_Project.GUI;

import javax.swing.*;
import javax.swing.text.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;


public class NumberFormatHelper {
    
    private static final DecimalFormat numberFormatter = new DecimalFormat("#,###");
    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    
    static {
        symbols.setGroupingSeparator(',');
        numberFormatter.setDecimalFormatSymbols(symbols);
        numberFormatter.setGroupingUsed(true);
    }
    

    public static String formatNumber(double number) {

        if (number == (long) number) {
            return numberFormatter.format((long) number);
        } else {
            DecimalFormat df = new DecimalFormat("#,###.##");
            df.setDecimalFormatSymbols(symbols);
            return df.format(number);
        }
    }
    

    public static String formatNumber(long number) {
        return numberFormatter.format(number);
    }
    

    public static double parseNumber(String formattedNumber) throws NumberFormatException {
        if (formattedNumber == null || formattedNumber.trim().isEmpty()) {
            throw new NumberFormatException("Empty string");
        }

        String cleaned = formattedNumber.replace(",", "").replace(" ", "").trim();
        return Double.parseDouble(cleaned);
    }
    

    public static JTextField createFormattedNumberField() {
        JTextField field = new JTextField();
        field.setDocument(new NumberFormatDocument());
        return field;
    }
    

    public static void applyNumberFormatting(JTextField field) {
        field.setDocument(new NumberFormatDocument());
    }
    

    private static class NumberFormatDocument extends PlainDocument {
        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) return;
            
            String currentText = getText(0, getLength());
            String beforeOffset = currentText.substring(0, offset);
            String afterOffset = currentText.substring(offset);
            String newText = beforeOffset + str + afterOffset;
            

            String cleaned = newText.replaceAll("[^0-9.]", "");
            

            if (cleaned.isEmpty() || cleaned.equals(".")) {
                super.insertString(offset, str, attr);
                formatText();
                return;
            }
            
            try {
                double value = Double.parseDouble(cleaned);

                super.insertString(offset, str, attr);

                SwingUtilities.invokeLater(() -> formatText());
            } catch (NumberFormatException e) {

            }
        }
        
        @Override
        public void remove(int offset, int length) throws BadLocationException {
            super.remove(offset, length);
            SwingUtilities.invokeLater(() -> formatText());
        }
        
        private void formatText() {
            try {
                String text = getText(0, getLength());
                if (text == null || text.trim().isEmpty()) {
                    return;
                }
                

                String cleaned = text.replaceAll("[^0-9.]", "");
                if (cleaned.isEmpty()) {
                    return;
                }
                
                double value = Double.parseDouble(cleaned);
                String formatted = formatNumber(value);
                

                if (!formatted.equals(text)) {
                    int caretPosition = getCaretPosition();
                    remove(0, getLength());
                    insertString(0, formatted, null);

                    if (caretPosition <= formatted.length()) {
                        setCaretPosition(caretPosition);
                    }
                }
            } catch (Exception e) {

            }
        }
        
        private int getCaretPosition() {

            return getLength();
        }
        
        private void setCaretPosition(int position) {

        }
    }
    

    public static class NumberFormatFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            try {
                sb.append(doc.getText(0, doc.getLength()));
                sb.insert(offset, string);
                
                String newText = sb.toString().replaceAll("[^0-9.]", "");
                if (newText.isEmpty() || isValidNumber(newText)) {
                    super.insertString(fb, offset, string, attr);
                    formatField(fb);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            try {
                sb.append(doc.getText(0, doc.getLength()));
                sb.replace(offset, offset + length, text);
                
                String newText = sb.toString().replaceAll("[^0-9.]", "");
                if (newText.isEmpty() || isValidNumber(newText)) {
                    super.replace(fb, offset, length, text, attrs);
                    formatField(fb);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
            formatField(fb);
        }
        
        private boolean isValidNumber(String str) {
            if (str.isEmpty()) return true;
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        private void formatField(FilterBypass fb) {
            SwingUtilities.invokeLater(() -> {
                try {
                    Document doc = fb.getDocument();
                    String text = doc.getText(0, doc.getLength());
                    if (text == null || text.trim().isEmpty()) {
                        return;
                    }
                    
                    String cleaned = text.replaceAll("[^0-9.]", "");
                    if (cleaned.isEmpty()) {
                        return;
                    }
                    
                    try {
                        double value = Double.parseDouble(cleaned);
                        String formatted = formatNumber(value);
                        
                        if (!formatted.equals(text)) {
                            int caretPos = 0;
                            try {

                                if (doc instanceof AbstractDocument) {

                                }
                            } catch (Exception e) {

                            }
                            

                            doc.remove(0, doc.getLength());
                            doc.insertString(0, formatted, null);
                        }
                    } catch (NumberFormatException e) {

                    }
                } catch (Exception e) {

                }
            });
        }
    }
}
