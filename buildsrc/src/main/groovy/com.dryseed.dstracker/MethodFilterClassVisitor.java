package com.dryseed.dstracker;

import com.dryseed.dstracker.annotations.TimeCost;
import com.dryseed.dstracker.utils.Log;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.annotation.Annotation;

/**
 * @User caiminming
 * <p>
 * 调用顺序：
 * ====> DsTracker : MethodFilterClassVisitor.visit -> version : 51 | access : 33 | name : com/dryseed/dstracker/MainActivity | signature : null | superName : android/support/v7/app/AppCompatActivity | interfaces : [Ljava.lang.String;@2017ce9f
 * ====> DsTracker : MethodFilterClassVisitor.visitMethod -> access : 1 | name : <init> | desc : ()V | signature : null | exceptions : null
 * ====> DsTracker : AdviceAdapter onMethodEnter
 * ====> DsTracker : AdviceAdapter onMethodExit
 * ====> DsTracker : MethodFilterClassVisitor.visitMethod -> access : 4 | name : onCreate | desc : (Landroid/os/Bundle;)V | signature : null | exceptions : null
 * ====> DsTracker : AdviceAdapter.visitAnnotation -> desc : Lcom/dryseed/dstracker/annotations/TimeCost; | visible : false
 * ====> DsTracker : AnnotationMethodsArrayValueScanner.visit: value=DDDSSS
 * ====> DsTracker : AdviceAdapter onMethodEnter
 * ====> DsTracker : AdviceAdapter onMethodExit
 */
public class MethodFilterClassVisitor extends ClassVisitor {
    private String className;
    private String mAnnotationValue;

    public MethodFilterClassVisitor(String className, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.className = className;
    }

    /**
     * 各参数代表的含义是：类版本、修饰符、类名、泛型信息、继承的父类、实现的接口。
     * 我们只需关心继承的父类和实现的接口，当执行到 visit 方法时，可以通过全局变量保存继承的父类和实现的接口信息。
     *
     * @param version
     * @param access
     * @param name
     * @param signature
     * @param superName
     * @param interfaces
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        Log.info(String.format("MethodFilterClassVisitor.visit -> version : %d | access : %d | name : %s | signature : %s | superName : %s | interfaces : %s",
                version, access, name, signature, superName, interfaces));
        // version : 51 | access : 33 | name : com/dryseed/dstracker/MainActivity | signature : null | superName : android/support/v7/app/AppCompatActivity | interfaces : [Ljava.lang.String;@a2c22c0
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Log.info(String.format("MethodFilterClassVisitor.visitAnnotation -> desc : %s | visible : %s", desc, visible));
        return super.visitAnnotation(desc, visible);
    }

    /**
     * 当扫描器扫描到类的方法时调用该方法。各参数代表的含义是：修饰符、方法名、方法签名、泛型信息、抛出的异常。
     * 其中，方法签名的格式如下：(参数列表)返回值类型；例如void onClick(View v)的方法签名为(Landroid/view/View;)V。
     *
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     * @return
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        Log.info(String.format("MethodFilterClassVisitor.visitMethod -> access : %d | name : %s | desc : %s | signature : %s | exceptions : %s",
                access, name, desc, signature, exceptions));
        //visitMethod -> access : 1 | name : <init> | desc : ()V | signature : null | exceptions : null
        //visitMethod -> access : 4 | name : onCreate | desc : (Landroid/os/Bundle;)V | signature : null | exceptions : null

        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        methodVisitor = new AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {

            boolean inject = false;

            @Override
            public void visitCode() {
                super.visitCode();

            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }

            @Override
            protected void onMethodEnter() {
                Log.info("AdviceAdapter onMethodEnter");
                //统计方法耗时
                if (isInject()) {
                    mv.visitLdcInsn(className + ":" + name + desc);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    mv.visitMethodInsn(INVOKESTATIC, "com/dryseed/dstracker/TimeCostLog", "setStartTime",
                            "(Ljava/lang/String;J)V", false);
                }
            }

            private boolean isInject() {
               /* if(name.equals("setStartTime") || name.equals("setEndTime") || name.equals("getCostTime")){
                   return false;
                }
                return true;*/
                return inject;
            }

            @Override
            protected void onMethodExit(int i) {
                Log.info("AdviceAdapter onMethodExit");
                //super.onMethodExit(i);
                if (isInject()) {
                    mv.visitLdcInsn(className + ":" + name + desc);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    mv.visitMethodInsn(INVOKESTATIC, "com/dryseed/dstracker/TimeCostLog", "setEndTime",
                            "(Ljava/lang/String;J)V", false);

                    /*mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn(className+":"+name+desc);
                    mv.visitMethodInsn(INVOKESTATIC, "com/meiyou/meetyoucost/CostLog", "getCostTime",
                            "(Ljava/lang/String;)Ljava/lang/String;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                            "(Ljava/lang/String;)V", false);*/

                    //mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    //mv.visitLdcInsn("========end=========");
                    //mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    //      "(Ljava/lang/String;)V", false);
                }
            }

            @Override
            public org.objectweb.asm.AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                // visitAnnotation -> desc : Lcom/dryseed/dstracker/annotations/TimeCost; | visible : false
                Log.info(String.format("AdviceAdapter.visitAnnotation -> desc : %s | visible : %s", desc, visible));
                if (Type.getDescriptor(TimeCost.class).equals(desc)) {
                    inject = true;
                }
                //return super.visitAnnotation(desc, visible);
                return new AnnotationMethodsArrayValueScanner();
            }

            public void print(String msg) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn(msg);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                        "(Ljava/lang/String;)V", false);
            }
        };
        return methodVisitor;
        //return new MethodAnnotationScanner();


        /*
            eg :
                @TimeCost
                protected void onCreate(Bundle savedInstanceState) {
                    TimeCostLog.setStartTime("MainActivity:onCreate(Landroid/os/Bundle;)V", System.nanoTime());
                    super.onCreate(savedInstanceState);
                    this.setContentView(2130968603);
                    TimeCostLog.setEndTime("MainActivity:onCreate(Landroid/os/Bundle;)V", System.nanoTime());
                }
         */
    }

    static class AnnotationMethodsArrayValueScanner extends AnnotationVisitor {
        AnnotationMethodsArrayValueScanner() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(String name, Object value) {
            Log.info(String.format("AnnotationMethodsArrayValueScanner.visit: %s -> %s", name, value));
            super.visit(name, value);
        }
    }
}
