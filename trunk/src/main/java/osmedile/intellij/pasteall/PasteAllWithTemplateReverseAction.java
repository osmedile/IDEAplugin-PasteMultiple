package osmedile.intellij.pasteall;

/**
 * Show a popup to select a template. List of templates is taken from "Live templates" marked as
 * "surround templates" (usage of $SELECTION$ variable).
 *
 * Paste all items (limited by the mark) with recent items first and apply the selected template.
 *
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteAllWithTemplateReverseAction extends PasteAllWithTemplateAction {

    public boolean getOlderFirst() {
        return false;
    }
}