package RPC.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class ReflectUtil {

    /**
     * 根据包名获得该包下所有的类
     *
     * @param packageNames 包名集合
     * @return 包下所有的类
     */
    public static Set<Class<?>> getClassesByPackageName(String[] packageNames) {
        Set<Class<?>> classes = new HashSet<>();
        for (String packageName : packageNames) {
            String packageDirName = packageName.replace(".", File.separator);
            Enumeration<URL> resources;
            try {
                // 获取所有名为packageName的URL(包括目录、jar包)
                resources = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    String protocol = url.getProtocol();
                    // 如果是普通文件 即是目录 则在目录下递归找
                    if ("file".equals(protocol)) {
                        Files.walkFileTree(Paths.get(url.toURI()), new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                String filePath = file.toFile().getPath();
                                // 如果是java类文件 则将全类名加入集合中
                                if (filePath.endsWith(".class")) {
                                    // 转换为全类名
                                    String pref = filePath.substring(filePath.indexOf(File.separator + "classes"));
                                    // 9 = "/classes/".length()
                                    String className = pref.substring(9, pref.length() - 6).replace(File.separator, ".");
                                    // 放入集合中
                                    try {
                                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return super.visitFile(file, attrs);
                            }
                        });
                    }
                }
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    /**
     * 获取启动类的类名，创建异常，然后通过异常的堆栈来获得启动类
     *
     * @return 启动类的全类名
     */
    public static String getMainClassName() {
        StackTraceElement[] stackTraceElements = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if ("main".equals(stackTraceElement.getMethodName())) {
                return stackTraceElement.getClassName();
            }
        }
        return "";
    }

}
