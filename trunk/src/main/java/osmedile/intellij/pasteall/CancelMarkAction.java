package osmedile.intellij.pasteall;

import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Cancel the current mark.
 * All items will be pasted by others actions
 * @author Olivier Smedile
 * @version $Id$
 */
public class CancelMarkAction extends com.intellij.openapi.actionSystem.AnAction {

    public void actionPerformed(AnActionEvent anActionEvent) {
        PasteAllAction.cancelMark();
    }
}