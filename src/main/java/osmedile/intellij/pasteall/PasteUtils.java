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
//
//    @NotNull
//    public static String getValue(Transferable tran) {
//        DataFlavor flavor;
//        flavor = DataFlavor.stringFlavor;
//        if (tran.isDataFlavorSupported(flavor)) {
//            try {
//                return tran.getTransferData(flavor).toString();
//            } catch (UnsupportedFlavorException e) {
//                return "";
//            } catch (IOException e) {
//                return "";
//            }
//        } else {
//            return "";
//        }
//    }

    //

    @NotNull
    public static String getValue(Transferable tran) {
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


    public static StringBuilder getContent(boolean newLine, boolean olderFirst,
                                           String[] values, String template) {
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
            if (newLine) {
                sb.append("\n");
            }
        }
        if (newLine && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb;
    }

    public static void pasteAll(Editor editor, Transferable[] trans,
                                boolean newLine,
                                boolean olderFirst, int limit,
                                String template) {

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

    public static void pasteAll(Editor editor, boolean newLine,
                                boolean olderFirst, String[] values,
                                String template) {

        StringBuilder sb = getContent(newLine, olderFirst, values, template);
        if (editor.isColumnMode()) {
            insertStringAsBlock(editor, sb.toString());
        } else {
            EditorModificationUtil.insertStringAtCaret(editor, sb.toString());
        }
    }


    public static void insertStringAsBlock(Editor editor,
                                           String content) {

        if (content != null) {
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


    public static void zeroWidthBlockSelectionAtCaretColumn(final Editor editor,
                                                            final int startLine,
                                                            final int endLine) {
        int caretColumn = editor.getCaretModel().getLogicalPosition().column;
        editor.getSelectionModel().setBlockSelection(
                new LogicalPosition(startLine, caretColumn),
                new LogicalPosition(endLine, caretColumn));
    }
}
