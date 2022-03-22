package configurations;

import fox.interfaces.JConfigurable;

import java.nio.file.Path;

public class UserSave implements JConfigurable {
    Path source;

    @Override
    public void setSource(Path path) {
        source = path;
    }

    @Override
    public Path getSource() {
        return source;
    }

//    initNewSaveFile() {
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_ANN, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_DMI, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_KUR, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_MAR, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_MSH, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_OKS, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_OLG, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_OLE, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_LIS, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_POS, 0);
//        IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_NEG, 0);
//    }
}
