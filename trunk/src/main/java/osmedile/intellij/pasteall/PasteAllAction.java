package osmedile.intellij.pasteall;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.ide.CopyPasteManager;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteAllAction extends EditorAction {

    private boolean newLine = true;

    private static int numberOfItems = -1;


    public PasteAllAction(EditorActionHandler editorActionHandler) {
        super(editorActionHandler);
    }

    public PasteAllAction() {
        super(null);

        this.setupHandler(new EditorWriteActionHandler() {
            public void executeWriteAction(Editor editor,
                                           DataContext dataContext) {
                PasteUtils.pasteAll(editor,
                        CopyPasteManager.getInstance().getAllContents(),
                        getNewLine(), getOlderFirst(), numberOfItems);
            }
        });
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public boolean getNewLine() {
        return newLine;
    }

    public boolean getOlderFirst() {
        return true;
    }

    public static void setMark() {
        numberOfItems = 0;
    }

    public static void cancelMark() {
        numberOfItems = -1;
    }

    public static void incNumberOfItems() {
        numberOfItems++;
    }

    public static int getNumberOfItems() {
        return numberOfItems;
    }

}
