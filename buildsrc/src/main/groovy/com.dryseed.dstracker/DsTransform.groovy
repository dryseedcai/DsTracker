package com.dryseed.dstracker

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
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

class DsTransform extends Transform {
    private static AppExtension android
    private static Project project

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
        println String.format("----------------%s %s--------------", getName(), " transform start")

        android = project.extensions.getByType(AppExtension)

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

                    //注入代码
                    //DsInjects.inject(directoryInput.file.absolutePath, mProject)

                    //是否是目录
                    if (directoryInput.file.isDirectory()) {
                        //遍历目录
                        directoryInput.file.eachFileRecurse {
                            File file ->
                                def filename = file.name;
                                def name = file.name
                                //这里进行我们的处理 TODO
                                if (name.endsWith(".class") && !name.startsWith("R\$") &&
                                        !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {

                                    println("filePath = " + file.absolutePath)
                                    ClassReader classReader = new ClassReader(file.bytes)
                                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                                    def className = name.split(".class")[0]
                                    ClassVisitor cv = new MethodFilterClassVisitor(className, classWriter)
                                    classReader.accept(cv, EXPAND_FRAMES)
                                    //classReader.accept(new AnnotationScanner(Opcodes.ASM5),EXPAND_FRAMES)
                                    byte[] code = classWriter.toByteArray()
                                    FileOutputStream fos = new FileOutputStream(
                                            file.parentFile.absolutePath + File.separator + name)
                                    fos.write(code)
                                    fos.close()

                                }
                        }
                    }

                    // 获取output目录
                    def dest = outputProvider.getContentLocation(directoryInput.name,
                            directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                    // 将input的目录复制到output指定目录
                    FileUtils.copyDirectory(directoryInput.file, dest)
            }

            //对类型为jar文件的input进行遍历。
            input.jarInputs.each { JarInput jarInput ->
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                //println("jar = " + jarInput.file.getAbsolutePath())
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                // TODO:

                //生成输出路径
                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        System.out.println(String.format("----------------%s %s--------------", getName(), " transform end"));
    }
}