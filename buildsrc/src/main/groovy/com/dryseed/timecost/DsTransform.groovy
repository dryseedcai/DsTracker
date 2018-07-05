package com.dryseed.timecost

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.dryseed.timecost.utils.Log
import com.dryseed.timecost.utils.Constants
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

/**
 * @author caiminming
 */
class DsTransform extends Transform {
    private static AppExtension android
    private static Project project
    private static HashSet<String> whitePackageList = []
    private static boolean isAutoInject = false
    private static HashSet<String> blackPackageList = []

    DsTransform(Project project) {
        DsTransform.project = project
    }

    /**
     * transform的名称
     * transformClassesWithMyClassTransformForDebug 运行时的名字
     * transformClassesWith + getName() + For + Debug或Release
     * @return
     */
    @Override
    String getName() {
        return "DsTransform"
    }

    /**
     * 需要处理的数据类型，有两种枚举类型
     * CLASSES和RESOURCES，CLASSES代表处理的java的class文件，RESOURCES代表要处理java的资源
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指Transform要操作内容的范围，官方文档Scope有7种类型：
     * EXTERNAL_LIBRARIES        只有外部库
     * PROJECT                   只有项目内容
     * PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * PROVIDED_ONLY             只提供本地或远程依赖项
     * SUB_PROJECTS              只有子项目。
     * SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * TESTED_CODE               由当前变量(包括依赖项)测试的代码
     * @return
     */
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 指明当前Transform是否支持增量编译
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * Transform中的核心方法，
     * inputs中是传过来的输入流，其中有两种格式，一种是jar包格式一种是目录格式。
     * outputProvider 获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做不然编译会报错
     * @param context
     * @param inputs
     * @param referencedInputs
     * @param outputProvider
     * @param isIncremental
     * @throws IOException
     * @throws com.android.build.api.transform.TransformException
     * @throws InterruptedException
     */
    @Override
    void transform(Context context,
                   Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        Log.info(String.format("----------------%s %s--------------", getName(), " transform start"))

        android = project.extensions.getByType(AppExtension)

        // auto inject
        isAutoInject = project.timeCostConfig.autoInject
        Log.info("===> config -> autoInject : ${isAutoInject}")

        // black list
        blackPackageList = project.timeCostConfig.blackPackageList
        blackPackageList.add(Constants.TIME_COST_PACKAGE_NAME)

        // 3rd party JAR packages that want our plugin to inject.
        HashSet<String> whiteConfigPackageList = project.timeCostConfig.whitePackageList
        if (whiteConfigPackageList != null) {
            whitePackageList.addAll(whiteConfigPackageList)
            Log.info("===> config -> whitePackageList : ${whitePackageList}")
        }

        //删除之前的输出
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }

        //Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs.each { TransformInput input ->
            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each {
                DirectoryInput directoryInput ->
                    //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等

                    //是否是目录
                    if (directoryInput.file.isDirectory()) {
                        //遍历目录
                        directoryInput.file.eachFileRecurse {
                            File file ->
                                def name = file.name
                                // log : file.absolutePath = E:\CodeDs\TimeCost\app\build\intermediates\classes\debug\com\dryseed\timecost\MainActivity.class
                                // log : file.name = MainActivity.class
                                // log : directoryInput.file.absolutePath = E:\CodeDs\TimeCost\app\build\intermediates\classes\debug
                                // Log.info("file.absolutePath = ${file.absolutePath}")
                                // Log.info("file.name = ${file.name}")
                                // Log.info("directoryInput.file.absolutePath = ${directoryInput.file.absolutePath}")
                                if (shouldModifyFile(file, directoryInput.file)) {
                                    Log.info("filePath = ${file.absolutePath}")
                                    ClassReader classReader = new ClassReader(file.bytes)
                                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                                    def className = name.split(".class")[0]
                                    ClassVisitor cv = new MethodFilterClassVisitor(className, classWriter, isAutoInject)
                                    classReader.accept(cv, EXPAND_FRAMES)
                                    byte[] code = classWriter.toByteArray()
                                    FileOutputStream fos = new FileOutputStream(
                                            file.parentFile.absolutePath + File.separator + name)
                                    fos.write(code)
                                    fos.close()
                                }
                        }
                    }

                    // 获取output目录
                    def dest = outputProvider.getContentLocation(
                            directoryInput.name,
                            directoryInput.contentTypes,
                            directoryInput.scopes,
                            Format.DIRECTORY
                    )

                    // 将input的目录复制到output指定目录
                    FileUtils.copyDirectory(directoryInput.file, dest)
            }

            //对类型为jar文件的input进行遍历。
            input.jarInputs.each { JarInput jarInput ->
                // 重命名输出文件（同目录copyFile会冲突）
                def jarInputName = jarInput.name
                def jarFilePath = jarInput.file.getAbsolutePath()
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                Log.info(String.format("jarInput.name : %s | jarFileName : %s | jarPath : %s",
                        jarInputName,
                        jarInput.file.name,
                        jarFilePath
                ))
                if (jarInputName.endsWith(".jar")) {
                    jarInputName = jarInputName.substring(0, jarInputName.length() - 4)
                }

                File tmpFile = null
                if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
                    JarFile jarFile = new JarFile(jarInput.file)
                    Enumeration enumeration = jarFile.entries()
                    //Log.info("tmpFile Name : " + jarInput.file.getParent() + File.separator + jarInput.file.name)
                    tmpFile = new File(jarInput.file.getParent() + File.separator + Constants.JAR_TMP_FILE_NAME)
                    //避免上次的缓存被重复插入
                    if (tmpFile.exists()) {
                        tmpFile.delete()
                    }
                    JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
                    //用于保存
                    ArrayList<String> processorList = new ArrayList<>()
                    while (enumeration.hasMoreElements()) {
                        JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                        String entryName = jarEntry.getName()
                        ZipEntry zipEntry = new ZipEntry(entryName)

                        // log : entryName : com/dryseed/timecost/TimeCostCanary.class
                        // Log.info(String.format("entryName : %s", entryName))

                        InputStream inputStream = jarFile.getInputStream(jarEntry)

                        // log : simpleEntryName : com.dryseed.timecost.TimeCostCanary
                        String simpleEntryName = entryName.replace("/", ".").replace(".class", "")

                        //插桩class
                        if (shouldModifyClass(entryName, simpleEntryName)) {
                            //class文件处理
                            Log.info("jar class : ${simpleEntryName}")
                            jarOutputStream.putNextEntry(zipEntry)
                            ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                            def className = entryName.split(".class")[0]
                            ClassVisitor cv = new MethodFilterClassVisitor(className, classWriter, isAutoInject)
                            classReader.accept(cv, EXPAND_FRAMES)
                            byte[] code = classWriter.toByteArray()
                            jarOutputStream.write(code)
                        } else if (entryName.contains("META-INF/services/javax.annotation.processing.Processor")) {
                            if (!processorList.contains(entryName)) {
                                processorList.add(entryName)
                                jarOutputStream.putNextEntry(zipEntry)
                                jarOutputStream.write(IOUtils.toByteArray(inputStream))
                            } else {
                                Log.info("duplicate entry : ${entryName}")
                            }
                        } else {
                            jarOutputStream.putNextEntry(zipEntry)
                            jarOutputStream.write(IOUtils.toByteArray(inputStream))
                        }

                        jarOutputStream.closeEntry()
                    }

                    jarOutputStream.close()
                    jarFile.close()
                }

                //生成输出路径
                def dest = outputProvider.getContentLocation(
                        jarInputName + md5Name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR
                )
                //将输入内容复制到输出
                if (tmpFile == null) {
                    FileUtils.copyFile(jarInput.file, dest)
                } else {
                    FileUtils.copyFile(tmpFile, dest)
                    tmpFile.delete()
                }
            }
        }
        Log.info(String.format("----------------%s %s--------------", getName(), " transform end"))
    }

    /**
     * check file whether is inject target
     * @param classFile
     * @param dirFile
     * @return
     */
    private boolean shouldModifyFile(File classFile, File dirFile) {
        // eg : MainActivity.class
        String name = classFile.name
        // eg : E:\CodeDs\TimeCost\app\build\intermediates\classes\debug\com\dryseed\timecost\MainActivity.class
        String classFilePath = classFile.absolutePath
        // eg : E:\CodeDs\TimeCost\app\build\intermediates\classes\debug
        String dirFilePath = dirFile.absolutePath
        // eg : com\dryseed\timecost\MainActivity.class
        String className = classFilePath.replace(dirFilePath + File.separator, "")
        // eg : com.dryseed.timecost.MainActivity
        className = className.replace(File.separator, ".").replace(".class", "")

        return shouldModifyClass(name, className)
    }

    /**
     * check class whether is inject target
     * @param name eg : MainActivity.class
     * @param className eg : com.dryseed.timecost.MainActivity
     * @return
     */
    private boolean shouldModifyClass(String name, String className) {
        if (name.startsWith("R\$") || "R.class".equals(name) || "BuildConfig.class".equals(name)) {
            return false
        }

        if (name.endsWith(".class")) {
            if (whitePackageList.isEmpty() && isAutoInject) {
                // auto inject is based on white list
                Log.info("return false : auto inject is based on white list - ${className}")
                return false
            }

            if (null != blackPackageList && !blackPackageList.isEmpty()) {
                // has black list
                Iterator<String> iterator = blackPackageList.iterator()
                while (iterator.hasNext()) {
                    String packagename = iterator.next()
                    if (className.contains(packagename)) {
                        Log.info("return false : hit black list ${className} - ${packagename}")
                        return false
                    }
                }
            }

            if (whitePackageList.isEmpty()) {
                // no white list, all class is valid
                Log.info("return true : no white list, all class is valid - ${className}")
                return true
            }

            // has white list
            Iterator<String> iterator = whitePackageList.iterator()
            while (iterator.hasNext()) {
                String packagename = iterator.next()
                //Log.info("=================================== packagename : ${packagename} | name : ${className}")
                if (className.contains(packagename)) {
                    Log.info("return true : class is in whitelist : ${className}")
                    return true
                }
            }

            Log.info("return false : other - ${className}")
            return false
        }

        //Log.info("check class : the class has not be recognized -- ${name}")
        return false
    }

}