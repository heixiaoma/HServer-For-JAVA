package top.hserver.core.agent;

import javassist.bytecode.ClassFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import top.hserver.HServerApplication;
import top.hserver.core.server.context.ConstConfig;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * 文件变化监听器
 * <p>
 * 在Apache的Commons-IO中有关于文件的监控功能的代码. 文件监控的原理如下：
 * 由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，
 * 如果有文件的变化，则根据相关的文件比较器，判断文件时新增，还是删除，还是更改。（默认为1000毫秒执行一次扫描）
 *
 * @author hxm
 */
@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {

    private Instrumentation instrumentation;

    FileListener(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    /**
     * 文件创建执行
     */
    @Override
    public void onFileCreate(File file) {
        //重启IOC
        HServerApplication.reInitIoc();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}