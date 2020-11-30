package top.hserver.core.agent;

import javassist.bytecode.ClassFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.HServerApplication;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

/**
 * @author hxm
 */
public class FileListener extends FileAlterationListenerAdaptor {
    private static final Logger log = LoggerFactory.getLogger(FileListener.class);
    private Instrumentation instrumentation;

    private String tempFilePath;

    FileListener(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public void onFileDelete(File file) {
        tempFilePath = file.getPath();
    }

    /**
     * 文件创建执行
     */
    @Override
    public void onFileCreate(File file) {

        //表示修改的文件
        if (file.getPath().equals(tempFilePath)) {
            onFileChange(file);
        } else {
            log.info("[创建]:" + file.getAbsolutePath());
            HServerApplication.reInitIoc();
        }
    }

    /**
     * 文件创建修改
     */
    @Override
    public void onFileChange(File file) {
        //热更新文件
        log.info("[修改]:" + file.getAbsolutePath());
        try {
            Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
            BufferedInputStream fin
                    = new BufferedInputStream(new FileInputStream(file));
            ClassFile cf = new ClassFile(new DataInputStream(fin));
            for (Class allLoadedClass : allLoadedClasses) {
                if (allLoadedClass.getName().equals(cf.getName())) {
                    ClassDefinition classDefinition = new ClassDefinition(allLoadedClass, IOUtils.toByteArray(file.toURI()));
                    instrumentation.redefineClasses(classDefinition);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}