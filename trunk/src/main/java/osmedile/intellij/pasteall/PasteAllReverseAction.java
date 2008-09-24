package osmedile.intellij.pasteall;

/**
 * Paste all items (limited by the mark) with recent items first.
 *
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteAllReverseAction extends PasteAllAction {

    public boolean getOlderFirst() {
        return false;
    }
}