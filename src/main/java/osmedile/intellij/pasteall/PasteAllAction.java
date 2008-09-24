package osmedile.intellij.pasteall;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.ide.CopyPasteManager;

/**
 * Paste all items (limited by the mark) with older items first.
 *
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteAllAction extends EditorAction {

    /**
     * Return true if a new line character is added after each item pasted.
     */
    private boolean newLine = true;

    /**
     * Limit the number of items to be pasted.
     * <0, means that all items are pasted.
     */
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
                        getNewLine(), getOlderFirst(), numberOfItems, null);
            }
        });
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public boolean getNewLine() {
        return newLine;
    }

    /**
     * Return true if the items must be pasted with older items first
     *
     * @return
     */
    public boolean getOlderFirst() {
        return true;
    }

    /**
     * Set the mark, so only items copied from now will be pasted
     */
    public static void setMark() {
        numberOfItems = 0;
    }

    /**
     * Cancel mark -> all items will be pasted
     */
    public static void cancelMark() {
        numberOfItems = -1;
    }

    /**
     * Increment mark, one more item can be pasted
     */
    public static void incNumberOfItems() {
        numberOfItems++;
    }

    /**
     * Returns the number of items to be pasted.
     * <0, means that all items must be pasted
     *
     * @return
     */
    public static int getNumberOfItems() {
        return numberOfItems;
    }

}
