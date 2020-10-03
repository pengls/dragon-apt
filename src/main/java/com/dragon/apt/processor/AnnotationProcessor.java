package com.dragon.apt.processor;

import com.dragon.apt.ConfigUtil;
import com.dragon.apt.annotation.Debugger;
import com.dragon.apt.handle.DebuggerAnnotationHandle;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author eason peng
 * http://zhouni.net/questions/a55311559405609.html
 * https://www.shuzhiduo.com/A/E35prajgzv/
 * https://blog.csdn.net/chenjiashe6037/article/details/100914038
 * https://juejin.im/post/6844903879524483086
 * <p>
 * AST：抽象语法树
 * TreeMaker.Apply --> 用于创建方法调用语法树节点（JCMethodInvocation）
 *                     --> typeargs：参数类型列表 fn：调用语句 args：参数列表
 * TreeMaker.VarDef --> 用于创建字段/变量定义语法树节点（JCVariableDecl）
 *                     --> mods：访问标志  vartype：类型  init：初始化语句  v：变量符号
 * TreeMaker.Ident  --> 用于创建标识符语法树节点（JCIdent）
 *
 */
public class AnnotationProcessor extends AbstractProcessor {
    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        ConfigUtil.init();
        super.init(processingEnv);
        this.processingEnv = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        new DebuggerAnnotationHandle(processingEnv, roundEnv).handleAnnotation();
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Debugger.class.getName());
        return set;
    }
}
