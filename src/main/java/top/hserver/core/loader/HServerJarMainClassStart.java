package top.hserver.core.loader;

import top.hserver.core.loader.archive.Archive;
import top.hserver.core.loader.util.AsciiBytes;

import java.util.List;


/**
 * @author hxm
 */
public class HServerJarMainClassStart extends ExecutableArchiveLauncher {

    private static final AsciiBytes LIB = new AsciiBytes("lib/");

    public HServerJarMainClassStart() {
    }

    protected HServerJarMainClassStart(Archive archive) {
        super(archive);
    }

    @Override
    protected boolean isNestedArchive(Archive.Entry entry) {
        return !entry.isDirectory() && entry.getName().startsWith(LIB);
    }

    @Override
    protected void postProcessClassPathArchives(List<Archive> archives) throws Exception {
        archives.add(0, getArchive());
    }

    public static void main(String[] args) {
        new JarLauncher().launch(args);
    }

}
