package osmedile.intellij.pasteall;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.util.text.StringUtil;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteAllAction extends EditorAction {


    private boolean newLine = true;

    private boolean olderFirst = true;

    private static int numberOfItems = -1;


    public PasteAllAction(EditorActionHandler editorActionHandler) {
        super(editorActionHandler);
    }

    public PasteAllAction() {
        super(null);


        this.setupHandler(new EditorWriteActionHandler() {
            public void executeWriteAction(Editor editor,
                                           DataContext dataContext) {
                SelectionModel selectionModel = editor.getSelectionModel();

                //Recent items are first in transferable array

                Transferable[] trans =
                        CopyPasteManager.getInstance().getAllContents();

                StringBuilder sb = new StringBuilder();
                if (getOlderFirst()) {
                    int end;
                    if (numberOfItems == -1) {
                        end = trans.length;
                    } else {
                        end = numberOfItems;
                    }
                    for (int i = end - 1; i >= 0; i--) {
                        pasteTransferable(sb, trans[i]);
                    }

                } else {
                    int start;
                    if (numberOfItems == -1) {
                        start = 0;
                    } else {
                        start = trans.length - numberOfItems;
                    }
                    for (int i = start; i < trans.length; i++) {
                        pasteTransferable(sb, trans[i]);
                    }
                }

                editor.getDocument().insertString(
                        selectionModel.getSelectionStart(), sb.toString());
            }
        });
    }

    protected void pasteTransferable(StringBuilder sb, Transferable tran) {
        pasteTransferable(sb, tran, getNewLine());
    }

    protected void pasteTransferable(StringBuilder sb, Transferable tran,
                                     boolean newLine) {
        String s = PasteUtils.getValue(tran);
        if (StringUtil.isNotEmpty(s)) {
            if (newLine) {
                sb.append("\n");
            }
            sb.append(s);
        }

    }

    protected void pasteTransferable(StringBuilder sb, String value,
                                     boolean newLine) {
        if (newLine) {
            sb.append("\n");
        }
        sb.append(value);

    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public boolean isNewLine() {
        return newLine;
    }

    public boolean getNewLine() {
        return newLine;
    }

    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }

    public boolean getOlderFirst() {
        return olderFirst;
    }

    public void setOlderFirst(boolean olderFirst) {
        this.olderFirst = olderFirst;
    }

    public static void cancelLastTransferable() {
        numberOfItems = 0;
    }

    public static void incNumberOfItems() {
        numberOfItems++;
    }

    public static int getNumberOfItems() {
        return numberOfItems;
    }

    public void pasteAll(Editor editor, Transferable[] trans) {
        pasteAll(editor, trans, getNewLine(), getOlderFirst(), -1);
    }

    public void pasteAll(Editor editor, Transferable[] trans, boolean newLine,
                         boolean olderFirst) {
        pasteAll(editor, trans, newLine, olderFirst, -1);
    }

    public void pasteAll(Editor editor, Transferable[] trans, boolean newLine,
                         boolean olderFirst, int limit) {

        //Recent items are first in transferable array


        List<String> values = new ArrayList<String>();
        for (int i = 0; i < trans.length && values.size() < limit; i++) {
            Transferable tran = trans[i];
            String s = PasteUtils.getValue(tran);
            if (StringUtil.isNotEmpty(s)) {
                values.add(s);
            }
        }

        pasteAll(editor, newLine, olderFirst,
                values.toArray(new String[values.size()]));
    }

    public void pasteAll(Editor editor, boolean newLine, boolean olderFirst,
                         String[] values) {
        StringBuilder sb = new StringBuilder();


        SelectionModel selectionModel = editor.getSelectionModel();

        if (olderFirst) {
            for (int i = values.length - 1; i >= 0; i--) {
                if (newLine) {
                    sb.append("\n");
                }
                sb.append(values[i]);
            }
        } else {
            for (String value : values) {
                if (newLine) {
                    sb.append("\n");
                }
                sb.append(value);
            }
        }

        editor.getDocument().insertString(
                selectionModel.getSelectionStart(), sb.toString());
    }
}
