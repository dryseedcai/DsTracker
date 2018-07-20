package com.dryseed.timecost

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.dryseed.timecost.utils.Constants
import com.dryseed.timecost.utils.Log
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.gradle.internal.hash.HashUtil
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
    private static boolean isJarInject = false
    private static HashSet<String> blackPackageList = []
    private static final String I = File.separator
    private static final String TRANSFORM_NAME = "DsTransform"
    private final File mJarCacheDir
    private final Set<QualifiedContent.Scope> mScopes
    private final Set<QualifiedContent.Scope> mCareScopes

    DsTransform(Project project, File buildDir, Set<QualifiedContent.Scope> scopes) {
        DsTransform.project = project
        mJarCacheDir = new File(buildDir, "jar-cache")
        mScopes = Collections.unmodifiableSet(scopes)
        mCareScopes = new HashSet<>()
    }

    /**
     * transform的名称
     * transformClassesWithMyClassTransformForDebug 运行时的名字
     * transformClassesWith + getName() + For + Debug或Release
     * @return
     */
    @Override
    String getName() {
        return TRANSFORM_NAME
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
        return mScopes
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
        Log.info(String.format("---------------- %s transform start [%s] --------------", getName(), project.name))

        DsPluginParams timeCostConfig = project.timeCostConfig

        // auto inject
        isAutoInject = timeCostConfig.autoInject
        Log.info("===> config -> autoInject : ${isAutoInject}")

        // jar inject
        isJarInject = timeCostConfig.jarInject
        Log.info("===> config -> jarInject : ${isJarInject}")

        // black list
        blackPackageList = timeCostConfig.blackPackageList
        blackPackageList.add(Constants.TIME_COST_PACKAGE_NAME)
        Log.info("===> config -> jarInject : ${blackPackageList}")

        // 3rd party JAR packages that want our plugin to inject.
        HashSet<String> whiteConfigPackageList = timeCostConfig.whitePackageList
        if (whiteConfigPackageList != null) {
            whitePackageList.addAll(whiteConfigPackageList)
            Log.info("===> config -> whitePackageList : ${whitePackageList}")
        }

        //scope
        Log.info("===> config -> scope : ${timeCostConfig.scope}")
        setCareScope(timeCostConfig.scope)

        // 删除TRANSFORM_NAME目录文件
        File transformsDir = new File(project.getBuildDir().absolutePath + "${I}intermediates${I}transforms${I}${TRANSFORM_NAME}")
        FileUtils.deleteDirectory(transformsDir)

        /**
         * 获取所有依赖的classPaths
         */
        def classPaths = []
        inputs.each { TransformInput input ->
            Log.info('>>>>>>>>>>>>>>>>>>>>>>>>>')
            input.directoryInputs.each { DirectoryInput directoryInput ->
                classPaths.add(directoryInput.file.absolutePath)
                Log.info("class dir in project(${project.name}) : ${directoryInput.file.absolutePath}")
            }
            input.jarInputs.each { JarInput jarInput ->
                classPaths.add(jarInput.file.absolutePath)
                Log.info("jar in project(${project.name}) : ${jarInput.file.absolutePath}")
            }
            Log.info('<<<<<<<<<<<<<<<<<<<<<<<<')
        }

        for (TransformInput transformInput : inputs) {
            for (DirectoryInput directoryInput : transformInput.getDirectoryInputs()) {
                // 获取output目录
                File output = outputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY
                )
                Log.info(String.format("destFile : %s \n directoryInput.file.getAbsolutePath() : %s", output, directoryInput.file.getAbsolutePath()))
                Log.info("directoryInput.getScopes() : ${directoryInput.getScopes()}")

                if (mCareScopes.containsAll(directoryInput.getScopes())) {
                    processClassPath(directoryInput.file, directoryInput.file)
                }

                FileUtils.copyDirectory(directoryInput.file, output)
            }

            for (JarInput jarInput : transformInput.getJarInputs()) {
                File input = jarInput.getFile()
                File tmpFile = null
                String name = HashUtil.createHash(input, "MD5").asHexString()

                Log.info("=> jarInput.getScopes() : ${jarInput.getScopes()} | ${jarInput.file.getAbsolutePath()}")
                if (mCareScopes.containsAll(jarInput.getScopes())) {
                    File cache = findCachedJar(name)
                    if (cache != null) {
                        input = cache
                    } else {
                        File tmpDir = context.getTemporaryDir()
                        if (!tmpDir.isDirectory()) {
                            if (tmpDir.exists()) {
                                tmpDir.delete()
                            }
                        }
                        tmpDir.mkdirs()
                        tmpFile = new File(tmpDir, name + ".jar")
                        FileUtils.copyFile(input, tmpFile)
                        input = tmpFile
                        processClassPath(input, jarInput.file)
                        cacheProcessedJar(input, name)
                    }
                }

                File output = outputProvider.getContentLocation(
                        name, jarInput.getContentTypes(),
                        jarInput.getScopes(), Format.JAR)
                FileUtils.copyFile(input, output)
                if (tmpFile != null) {
                    tmpFile.delete()
                }
            }
        }

        Log.info(String.format("----------------%s %s--------------", getName(), " transform end"))
    }

    private void processClassPath(File inputFile, File dirFile) {
        String path = inputFile.absolutePath
        if (inputFile.isDirectory()) {
            File[] children = inputFile.listFiles()
            for (File child : children) {
                processClassPath(child, dirFile)
            }
        } else if (path.endsWith(".jar")) {
            processJar(inputFile)
        } else if (path.endsWith(".class") && !path.contains("${File.separator}R\$") && !path.endsWith("${File.separator}R.class") && !path.endsWith("${File.separator}BuildConfig.class")) {
            processClass(inputFile, dirFile)
        }
    }

    private void processJar(File file) {
        Log.info("===> processJar : ${file.getAbsolutePath()}")
        JarFile jarFile = new JarFile(file)
        Enumeration enumeration = jarFile.entries()
        File tmpFile = new File(file.getParent(), file.name + ".opt")
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)

            InputStream inputStream = jarFile.getInputStream(jarEntry)

            jarOutputStream.putNextEntry(zipEntry)

            //插桩class
            if (shouldModifyClassInJar(entryName)) {
                // log : entryName : com/dryseed/timecost/TimeCostCanary.class
                String simpleEntryName = entryName.replace("/", ".").replace(".class", "")
                if (shouldModifyClass(simpleEntryName)) {
                    //class文件处理
                    Log.info("  ===> modifyJarClass : ${simpleEntryName}")

                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    def className = entryName.split(".class")[0]
                    ClassVisitor cv = new MethodFilterClassVisitor(className, classWriter, isAutoInject)
                    classReader.accept(cv, EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }


            jarOutputStream.closeEntry()
        }

        jarOutputStream.close()
        jarFile.close()

        if (file.exists()) {
            file.delete()
        }
        tmpFile.renameTo(file)
        if (tmpFile.exists()) {
            tmpFile.delete()
        }
    }


    private void processClass(File file, File dirFile) {
        Log.info("===> processClass : ${file.getAbsolutePath()}")
        if (shouldModifyFile(file, dirFile)) {
            Log.info("  ===> modifyClass : ${file.getAbsolutePath()}")
            try {
                ClassReader classReader = new ClassReader(file.bytes)
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                def className = file.name.split(".class")[0]
                ClassVisitor cv = new MethodFilterClassVisitor(className, classWriter, isAutoInject)
                classReader.accept(cv, EXPAND_FRAMES)
                byte[] code = classWriter.toByteArray()
                File optClass = new File(file.getParent(), file.name + ".opt")
                FileOutputStream fos = new FileOutputStream(optClass)
                fos.write(code)
                IOUtils.closeQuietly(fos)
                FileUtils.forceDelete(file)
                FileUtils.moveFile(optClass, file)
                if (optClass.exists()) {
                    optClass.delete()
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
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

        return shouldModifyClass(className)
    }

    /**
     * check class in jar whether is inject target
     * @param entryName
     * @return
     */
    private boolean shouldModifyClassInJar(String entryName) {
        if (!entryName.endsWith(".class")) {
            return false
        }
        if (entryName.contains("/R\$") || entryName.endsWith("/R.class") || entryName.endsWith("/BuildConfig.class")) {
            return false
        }
        return true
    }

    /**
     * check class whether is inject target
     * @param className eg : com.dryseed.timecost.MainActivity
     * @return
     */
    private boolean shouldModifyClass(String className) {
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

        //Log.info("return false : other - ${className}")
        return false
    }

    File findCachedJar(String md5) {
        if (!mJarCacheDir.isDirectory()) {
            if (mJarCacheDir.exists()) {
                mJarCacheDir.delete()
            }
            return null
        }
        String target = md5 + ".jar"
        String[] files = mJarCacheDir.list()
        for (String name : files) {
            if (target == name) {
                return new File(mJarCacheDir, target)
            }
        }
        return null
    }

    void cacheProcessedJar(File jar, String md5) {
        if (!mJarCacheDir.isDirectory()) {
            if (mJarCacheDir.exists()) {
                mJarCacheDir.delete()
            }
        }
        if (!mJarCacheDir.exists()) {
            mJarCacheDir.mkdirs()
        }
        FileUtils.copyFile(jar, new File(mJarCacheDir, md5 + ".jar"))
    }

    void setCareScope(DsPluginParams.Scope scope) {
        mCareScopes.clear()
        if (scope.project) {
            mCareScopes.add(QualifiedContent.Scope.PROJECT)
        }
        if (scope.projectLocalDep) {
            mCareScopes.add(QualifiedContent.Scope.PROJECT_LOCAL_DEPS)
        }
        if (scope.subProject) {
            mCareScopes.add(QualifiedContent.Scope.SUB_PROJECTS)
        }
        if (scope.subProjectLocalDep) {
            mCareScopes.add(QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS)
        }
        if (scope.externalLibraries) {
            mCareScopes.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
        }
    }
}