package com.dryseed.timecost;

import com.dryseed.timecost.annotations.TimeCost;
import com.dryseed.timecost.utils.Constants;
import com.dryseed.timecost.utils.Log;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.concurrent.ConcurrentHashMap;

import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * @User caiminming
 * <p>
 * Method Link Order :
 * ====> TimeCost : MethodFilterClassVisitor.visit -> version : 51 | access : 33 | name : com/dryseed/timecost/MainActivity | signature : null | superName : android/support/v7/app/AppCompatActivity | interfaces : [Ljava.lang.String;@2017ce9f
 * ====> TimeCost : MethodFilterClassVisitor.visitMethod -> access : 1 | name : <init> | desc : ()V | signature : null | exceptions : null
 * ====> TimeCost : AdviceAdapter onMethodEnter
 * ====> TimeCost : AdviceAdapter onMethodExit
 * ====> TimeCost : MethodFilterClassVisitor.visitMethod -> access : 4 | name : onCreate | desc : (Landroid/os/Bundle;)V | signature : null | exceptions : null
 * ====> TimeCost : AdviceAdapter.visitAnnotation -> desc : Lcom/dryseed/timecost/annotations/TimeCost; | visible : false
 * ====> TimeCost : AnnotationMethodsArrayValueScanner.visit: value=DDDSSS
 * ====> TimeCost : AdviceAdapter onMethodEnter
 * ====> TimeCost : AdviceAdapter onMethodExit
 */
public class MethodFilterClassVisitor extends ClassVisitor {
    private String mMethodName;
    private String mClassName;
    private ConcurrentHashMap mAnnotationHashMap = new ConcurrentHashMap();
    boolean mIsInject = false;
    boolean mIsAutoInject = false;

    public MethodFilterClassVisitor(String className, ClassVisitor cv, boolean isAutoInject) {
        super(Opcodes.ASM5, cv);
        this.mClassName = className;
        this.mIsAutoInject = isAutoInject;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        Log.info(String.format(
                "MethodFilterClassVisitor.visit -> version : %d | access : %d | name : %s | signature : %s | superName : %s | interfaces : %s",
                version, access, name, signature, superName, interfaces)
        );
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        //Log.info(String.format("MethodFilterClassVisitor.visitAnnotation -> desc : %s | visible : %s", desc, visible));
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        /*Log.info(String.format(
                "MethodFilterClassVisitor.visitMethod -> access : %d | name : %s | desc : %s | signature : %s | exceptions : %s",
                access, name, desc, signature, exceptions)
        );*/
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        methodVisitor = new AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {

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
                //Log.info("AdviceAdapter onMethodEnter");
                //统计方法耗时
                if (mIsAutoInject || (mIsInject && mAnnotationHashMap != null)) {
                    if (mAnnotationHashMap.containsKey(Constants.ANNOTATION_COLUMN_NAME)) {
                        mMethodName = (String) mAnnotationHashMap.get(Constants.ANNOTATION_COLUMN_NAME);
                    } else {
                        mMethodName = mClassName + ":" + name + desc;
                    }

                    long exceededTime = 0;
                    boolean monitorOnlyMainThread = false;
                    if (mAnnotationHashMap.isEmpty()) {
                        //generateStartCodeWithNoParam(mv, mMethodName);
                    } else if (mAnnotationHashMap.containsKey(Constants.ANNOTATION_COLUMN_MILLITIME)) {
                        exceededTime = (long) mAnnotationHashMap.get(Constants.ANNOTATION_COLUMN_MILLITIME);
                        //generateStartCodeWithExceededTimeParam(mv, mMethodName, (long) mAnnotationHashMap.get(Constants.ANNOTATION_COLUMN_MILLITIME));
                    } else if (mAnnotationHashMap.containsKey(Constants.ANNOTATION_COLUMN_MONITOR_ONLY_MAIN_THREAD)) {
                        monitorOnlyMainThread = (boolean) mAnnotationHashMap.get(Constants.ANNOTATION_COLUMN_MONITOR_ONLY_MAIN_THREAD);
                        //generateStartCodeWithNoParam(mv, mMethodName);
                    }
                    generateStartCode(mv, mMethodName, exceededTime, monitorOnlyMainThread);
                    Log.info(String.format("generate code : %s", mMethodName));
                }
                mAnnotationHashMap.clear();
            }

            @Override
            protected void onMethodExit(int i) {
                //Log.info("AdviceAdapter onMethodExit");
                if (mIsAutoInject || mIsInject) {
                    generateEndCodeWithNoParam(mv, mMethodName);
                    mIsInject = false;
                }
            }

            @Override
            public org.objectweb.asm.AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                Log.info(String.format("AdviceAdapter.visitAnnotation -> desc : %s | visible : %s", desc, visible));
                if (Type.getDescriptor(TimeCost.class).equals(desc)) {
                    Log.info("found TimeCost Annotation");
                    mIsInject = true;
                }
                return new AnnotationMethodsArrayValueScanner();
            }

        };
        return methodVisitor;
    }

    private void generateStartCode(MethodVisitor mv, String name, long exceededTime, boolean monitorOnlyMainThread) {
        mv.visitMethodInsn(
                INVOKESTATIC,
                "com/dryseed/timecost/TimeCostCanary",
                "get",
                "()Lcom/dryseed/timecost/TimeCostCanary;",
                false
        );
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/System",
                "currentTimeMillis",
                "()J",
                false
        );
        mv.visitLdcInsn(new Long(exceededTime));
        mv.visitInsn(monitorOnlyMainThread ? ICONST_1 : ICONST_0);
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "com/dryseed/timecost/TimeCostCanary",
                "setStartTime",
                "(Ljava/lang/String;JJZ)V",
                false
        );
    }

    private void generateEndCodeWithNoParam(MethodVisitor mv, String name) {
        mv.visitMethodInsn(
                INVOKESTATIC,
                "com/dryseed/timecost/TimeCostCanary",
                "get",
                "()Lcom/dryseed/timecost/TimeCostCanary;",
                false
        );
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/System",
                "currentTimeMillis",
                "()J",
                false
        );
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "com/dryseed/timecost/TimeCostCanary",
                "setEndTime",
                "(Ljava/lang/String;J)V",
                false
        );
    }

    /*private void generateStartCodeWithNoParam(MethodVisitor mv, String name) {
        mv.visitMethodInsn(
                INVOKESTATIC,
                "com/dryseed/timecost/TimeCostCanary",
                "get",
                "()Lcom/dryseed/timecost/TimeCostCanary;",
                false
        );
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/System",
                "currentTimeMillis",
                "()J",
                false
        );
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "com/dryseed/timecost/TimeCostCanary",
                "setStartTime",
                "(Ljava/lang/String;J)V",
                false
        );
    }

    private void generateStartCodeWithExceededTimeParam(MethodVisitor mv, String name, long exceededTime) {
        mv.visitMethodInsn(
                INVOKESTATIC,
                "com/dryseed/timecost/TimeCostCanary",
                "get",
                "()Lcom/dryseed/timecost/TimeCostCanary;",
                false
        );
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/System",
                "currentTimeMillis",
                "()J",
                false
        );
        mv.visitLdcInsn(new Long(exceededTime));
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "com/dryseed/timecost/TimeCostCanary",
                "setStartTime",
                "(Ljava/lang/String;JJ)V",
                false
        );
    }*/

    class AnnotationMethodsArrayValueScanner extends AnnotationVisitor {
        AnnotationMethodsArrayValueScanner() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(String name, Object value) {
            if (mIsInject) {
                Log.info(String.format("AnnotationMethodsArrayValueScanner.visit: %s -> %s", name, value));
                mAnnotationHashMap.put(name, value);
                super.visit(name, value);
            }
        }
    }
}
