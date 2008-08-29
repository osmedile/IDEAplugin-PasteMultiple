package osmedile.intellij.pasteall;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.ide.CopyPasteManager;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteAllAction extends EditorAction {
    private boolean newLine = true;

    private boolean recentFirst = false;

    public PasteAllAction() {
        super(null);

        this.setupHandler(new EditorWriteActionHandler() {
            public void executeWriteAction(Editor editor,
                                           DataContext dataContext) {
                SelectionModel selectionModel = editor.getSelectionModel();


//                CopyPasteManager.getInstance().setContents(null);
                Transferable[] trans =
                        CopyPasteManager.getInstance().getAllContents();
                if (getRecentFirst()) {
                    for (int i = trans.length - 1; i >= 0; i--) {
                        pasteTransferable(editor, selectionModel, trans[i]);
                    }
                } else {
                    for (Transferable tran : trans) {
                        pasteTransferable(editor, selectionModel, tran);
                    }
                }
            }
        });
    }

    protected void pasteTransferable(Editor editor,
                                     SelectionModel selectionModel,
                                     Transferable tran) {
        final Object data;
        try {
            data = tran.getTransferData(tran.getTransferDataFlavors()[0]);
            editor.getDocument().insertString(selectionModel.getSelectionEnd(),
                    data.toString());
            if (getNewLine()) {
                editor.getDocument()
                        .insertString(selectionModel.getSelectionEnd(), "\n");
            }
        } catch (UnsupportedFlavorException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public boolean getRecentFirst() {
        return recentFirst;
    }

    public void setRecentFirst(boolean recentFirst) {
        this.recentFirst = recentFirst;
    }
}
