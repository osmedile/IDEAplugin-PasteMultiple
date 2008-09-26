package osmedile.intellij.pasteall;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteUtils {
    /**
     * Return a String value of the specified transferable.
     * If the value of transferable can't be converted to a String, then an empty string is
     * returned.
     *
     * Method adapted from EditorModificationUtil to catch all exceptions related to transferable.
     *
     * @param tran transferable to be converted to a String
     *
     * @return a String value of the specified transferable or empty string if an error occurs.
     *
     * @see com.intellij.openapi.editor.EditorModificationUtil#getStringContent(java.awt.datatransfer.Transferable)
     */
    @NotNull
    public static String getValue(@Nullable Transferable tran) {
        if (tran == null) {
            return "";
        }
        RawText raw = null;
        try {
            raw = (RawText) tran.getTransferData(RawText.FLAVOR);
        } catch (UnsupportedFlavorException e) {
            // OK. raw will be null and we'll get plain string
        } catch (IOException e) {
            // OK. raw will be null and we'll get plain string
        }

        String s;
        if (raw != null) {
            s = raw.rawText;
        } else {
            try {
                s = (String) tran.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException e) {
                return "";
            } catch (IOException e) {
                return "";
            }
        }

        if (s != null) {
            return StringUtil.convertLineSeparators(s, "\n");
        } else {
            return "";
        }
    }

    /**
     * Paste transferables into the editor at caret.
     * Number of items to be pasted are limited by the value of limit.
     *
     * @param editor
     * @param trans      list of items to pasted. Recent items are first in transferable array.
     * @param newLine    true if a newline character is added between each item pasted
     * @param olderFirst true if older items (Transferable) must be pasted first.
     * @param limit      number of items to be pasted
     * @param template   template to used before pasting items
     */
    public static void pasteAll(@NotNull Editor editor, @NotNull Transferable[] trans,
                                boolean newLine,
                                boolean olderFirst, int limit,
                                @Nullable String template) {

        //Recent items are first in transferable array

        List<String> values = new ArrayList<String>();
        for (int i = 0;
             i < trans.length && (limit < 0 || values.size() < limit); i++) {
            Transferable tran = trans[i];
            String s = getValue(tran);
            if (StringUtil.isNotEmpty(s)) {
                values.add(s);
            }
        }

        pasteAll(editor, newLine, olderFirst,
                values.toArray(new String[values.size()]), template);
    }

    /**
     * @param editor
     * @param values     list of items to pasted. Recent items are first in array.
     * @param newLine    true if a newline character is added between each item pasted
     * @param olderFirst true if older items (Transferable) must be pasted first.
     * @param template   template to used before pasting items
     */
    public static void pasteAll(@NotNull Editor editor, boolean newLine,
                                boolean olderFirst, @NotNull String[] values,
                                @Nullable String template) {

        StringBuilder sb = new StringBuilder();

        if (olderFirst) {
            CollectionUtils.reverseArray(values);
        }
        for (String value : values) {
            if (template != null) {
                sb.append(template.replaceAll("\\$SELECTION\\$", value));
            } else {
                sb.append(value);
            }
            if (newLine && !template.endsWith("\n")) {
                sb.append("\n");
            }
        }
        if (newLine && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        if (editor.isColumnMode()) {
            insertStringAsBlock(editor, sb.toString());
        } else {
            EditorModificationUtil.insertStringAtCaret(editor, sb.toString());
        }
    }


    /**
     * Insert the specified string at caret.
     * Method copied from EditorModificationUtil#pasteFromClipboardAsBlock() and adapted to paste
     * the specified content
     *
     * @param editor
     * @param content string to insert in editor at caret.
     *
     * @see EditorModificationUtil#pasteFromClipboardAsBlock(com.intellij.openapi.editor.Editor)
     */
    public static void insertStringAsBlock(@Nullable Editor editor,
                                           @Nullable String content) {

        if (editor != null && content != null) {
            try {
                int caretLine =
                        editor.getCaretModel().getLogicalPosition().line;
                int originalCaretLine = caretLine;

                int selectedLinesCount = 0;
                final SelectionModel selectionModel =
                        editor.getSelectionModel();
                if (selectionModel.hasBlockSelection()) {
                    final LogicalPosition start =
                            selectionModel.getBlockStart();
                    final LogicalPosition end = selectionModel.getBlockEnd();
                    LogicalPosition caret = new LogicalPosition(
                            Math.min(start.line, end.line),
                            Math.min(start.column, end.column));
                    selectedLinesCount = Math.abs(end.line - start.line);
                    caretLine = caret.line;

                    EditorModificationUtil.deleteSelectedText(editor);
                    editor.getCaretModel().moveToLogicalPosition(caret);
                }

                LogicalPosition caretToRestore =
                        editor.getCaretModel().getLogicalPosition();

                String[] lines =
                        LineTokenizer.tokenize(content.toCharArray(), false);
                if (lines.length > 1 || selectedLinesCount <= 1) {
                    int longestLineLength = 0;
                    for (String line : lines) {
                        longestLineLength =
                                Math.max(longestLineLength, line.length());
                        EditorModificationUtil
                                .insertStringAtCaret(editor, line, false, true);
                        editor.getCaretModel().moveCaretRelatively(
                                -line.length(), 1, false, false, true);
                    }
                    caretToRestore = new LogicalPosition(originalCaretLine,
                            caretToRestore.column + longestLineLength);
                } else {
                    for (int i = 0; i <= selectedLinesCount; i++) {
                        EditorModificationUtil
                                .insertStringAtCaret(
                                        editor, content, false, true);
                        editor.getCaretModel()
                                .moveCaretRelatively(-content.length(),
                                        1, false, false, true);
                    }
                    caretToRestore = new LogicalPosition(originalCaretLine,
                            caretToRestore.column + content.length());
                }
                editor.getCaretModel().moveToLogicalPosition(caretToRestore);
                zeroWidthBlockSelectionAtCaretColumn(editor, caretLine,
                        caretLine + selectedLinesCount);
            } catch (Exception exception) {
                editor.getComponent().getToolkit().beep();
            }
        }
    }


    /**
     * Method copied from EditorModificationUtil because it's private.
     *
     * @param editor
     * @param startLine
     * @param endLine
     *
     * @see com.intellij.openapi.editor.EditorModificationUtil#zeroWidthBlockSelectionAtCaretColumn(com.intellij.openapi.editor.Editor, int, int)
     */
    public static void zeroWidthBlockSelectionAtCaretColumn(@NotNull final Editor editor,
                                                            final int startLine,
                                                            final int endLine) {
        int caretColumn = editor.getCaretModel().getLogicalPosition().column;
        editor.getSelectionModel().setBlockSelection(
                new LogicalPosition(startLine, caretColumn),
                new LogicalPosition(endLine, caretColumn));
    }
}
