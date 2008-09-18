package osmedile.intellij.pasteall;

import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class MarkAction extends com.intellij.openapi.actionSystem.AnAction {

    public void actionPerformed(AnActionEvent anActionEvent) {
        PasteAllAction.setMark();
    }
}