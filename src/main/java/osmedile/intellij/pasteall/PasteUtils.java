package osmedile.intellij.pasteall;

import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteUtils {
//
//    public static final DataFlavor PLAIN_TEXT_DF =
    //            DataFlavor.getTextPlainUnicodeFlavor();
//    private static final DataFlavor PLAIN_TEXT_DF =
//            DataFlavor.plainTextFlavor;


    @NotNull
    public static String getValue(Transferable tran) {
        DataFlavor flavor;
//        flavor = DataFlavor.selectBestTextFlavor(tran.getTransferDataFlavors());
//        if (flavor == null) {
        flavor = DataFlavor.stringFlavor;
//        }
        if (tran.isDataFlavorSupported(flavor)) {
            try {
                return tran.getTransferData(flavor).toString();
            } catch (UnsupportedFlavorException e) {
                return "";
            } catch (IOException e) {
                return "";
            }
        } else {
            return "";
        }
    }
}
