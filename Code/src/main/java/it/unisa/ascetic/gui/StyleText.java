package src.main.java.it.unisa.ascetic.gui;

import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyleText {

    private Map<String, AttributeSet> attributesMap;
    private AttributeSet defaultAttr;

    public StyleText() {
        this.attributesMap = new HashMap<String,AttributeSet>();
        SimpleAttributeSet attr;

        attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, new Color(204, 120, 50));
        attributesMap.put("boolean", attr);
        attributesMap.put("byte", attr);
        attributesMap.put("char", attr);
        attributesMap.put("short", attr);
        attributesMap.put("int", attr);
        attributesMap.put("long", attr);
        attributesMap.put("float", attr);
        attributesMap.put("double", attr);
        attributesMap.put("true", attr);
        attributesMap.put("false", attr);
        attributesMap.put("protected", attr);
        attributesMap.put("public", attr);
        attributesMap.put("private", attr);
        attributesMap.put("static", attr);
        attributesMap.put("global", attr);
        attributesMap.put("final", attr);
        attributesMap.put("import", attr);
        attributesMap.put("package", attr);
        attributesMap.put("return", attr);
        attributesMap.put(";", attr);
        attributesMap.put(",", attr);
        attributesMap.put("do", attr);
        attributesMap.put("while", attr);
        attributesMap.put("for", attr);
        attributesMap.put("try", attr);
        attributesMap.put("catch", attr);
        attributesMap.put("finally", attr);
        attributesMap.put("throw", attr);
        attributesMap.put("new", attr);
        attributesMap.put("this", attr);
        attributesMap.put("enum", attr);
        attributesMap.put("if", attr);
        attributesMap.put("else", attr);

        attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, new Color(104, 151, 187));
        
        attributesMap.put("0", attr);
        attributesMap.put("1", attr);
        attributesMap.put("2", attr);
        attributesMap.put("3", attr);
        attributesMap.put("4", attr);
        attributesMap.put("5", attr);
        attributesMap.put("6", attr);
        attributesMap.put("7", attr);
        attributesMap.put("8", attr);
        attributesMap.put("9", attr);

        this.defaultAttr = new SimpleAttributeSet();
    }

    public StyledDocument createDocument(CharSequence charSeq) {
        try {
            StyledDocument styledDoc = new DefaultStyledDocument();

            // A word is one or more of: letters, numbers, underscores, hyphens.
            //
            // \p{L} means "any letter as in the Unicode standard"
            // \p{N} means "any number as in the Unicode standard"
            Pattern wordPattern = Pattern.compile("[\\p{L}]+|\\p{Po}|[\\p{N}+]");

            Matcher matcher = wordPattern.matcher(charSeq);

            int pos = 0;

            while (matcher.find()) {
                String word = matcher.group();

                AttributeSet attrSet = attributesMap.get(word);

                if (attrSet != null) {
                    if (pos < matcher.start()) {
                        String str = charSeq.subSequence(pos, matcher.start()).toString();

                        // Inserts the previous part of the text using the default attributes.
                        styledDoc.insertString(styledDoc.getLength(), str, defaultAttr);
                    }

                    // Inserts the word using the mapped attributes.
                    styledDoc.insertString(styledDoc.getLength(), word, attrSet);
                    pos = matcher.end();
                }
            }

            if (pos < charSeq.length()) {
                String str = charSeq.subSequence(pos, charSeq.length()).toString();

                // Inserts the last remaining part of the text using the default attributes.
                styledDoc.insertString(styledDoc.getLength(), str, defaultAttr);
            }

            return styledDoc;
        } catch (BadLocationException e) {
            // This should not happen according to how text is inserted
            // into the document .... if happens, an Error is thrown.
            throw new Error("Unexpected BadLocationException", e);
        }
    }

}
