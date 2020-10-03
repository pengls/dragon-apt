package com.dragon.apt.handle;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * @author eason peng
 */
public abstract class AnnotationHandle {
    protected ProcessingEnvironment processingEnv;
    protected RoundEnvironment roundEnv;
    protected Messager messager;
    protected TreeMaker treeMaker;
    protected JavacElements elementUtils;
    protected JavacTrees trees;
    protected Names names;

    public AnnotationHandle(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        this.processingEnv = processingEnv;
        this.roundEnv = roundEnv;
        this.messager = processingEnv.getMessager();
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.elementUtils = (JavacElements) processingEnv.getElementUtils();
        this.trees = JavacTrees.instance(processingEnv);
        this.names = Names.instance(context);
    }

    /**
     * handle annotation
     */
    public abstract void handleAnnotation();
}
