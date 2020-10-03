package com.dragon.apt.helper;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.sun.tools.javac.code.Symbol.ClassSymbol;
import static com.sun.tools.javac.tree.JCTree.*;
import static com.sun.tools.javac.util.List.nil;


/**
 * @author eason peng
 */
public class JcClassHelper extends JcContext {
    private JCClassDecl classDecl;
    private ClassSymbol classSym;
    private Element element;

    public JcClassHelper(Element element, ProcessingEnvironment env) {
        super(env);
        TypeElement classElement = (TypeElement) element;
        this.classDecl = trees.getTree(classElement);
        this.classSym = (ClassSymbol) element;
        this.element = element;
    }

    public String getName() {
        return classSym.name.toString();
    }

    public String getFullName() {
        return classSym.fullname.toString();
    }

    public String getPackageName() {
        return classSym.owner.toString();
    }

    /**
     * 是否具有指定修饰符
     *
     * @param modifier
     * @return
     */
    public boolean hasModifier(int modifier) {
        return classDecl.mods.flags % (modifier * 2) >= modifier;
    }

    /**
     * 是否实现了指定接口
     *
     * @param interfaceClass
     * @return
     */
    public boolean hasInterface(Class<?> interfaceClass) {
        for (JCExpression impl : classDecl.implementing) {
            if (impl.type.toString().equals(interfaceClass.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在指定字段
     *
     * @param fieldName
     * @return
     */
    public boolean hasField(String fieldName) {
        for (JCTree jcTree : classDecl.defs) {
            if (jcTree.getKind() == Kind.VARIABLE) {
                JCVariableDecl var = (JCVariableDecl) jcTree;
                if (fieldName.equals(var.name.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否存在指定方法
     *
     * @param methodName
     * @return
     */
    public boolean hasMethod(String methodName) {
        for (JCTree jcTree : classDecl.defs) {
            if (jcTree.getKind() == Kind.METHOD) {
                JCMethodDecl var = (JCMethodDecl) jcTree;
                if (methodName.equals(var.name.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setModifier(int modifier) {
        classDecl.mods.flags = modifier;
    }

    public void importClass(Class<?> importClass) {
        appendCompilationUnit(importClass.getPackage().getName(), importClass.getSimpleName());
    }

    public void importClass(String className) {
        String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
        String packageName = className.substring(0, className.lastIndexOf("."));
        appendCompilationUnit(packageName, simpleClassName);
    }

    private void appendCompilationUnit(String packageName, String simpleClassName) {
        JCCompilationUnit compilationUnit = (JCCompilationUnit) trees.getPath(element).getCompilationUnit();
        JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(packageName)), names.fromString(simpleClassName));
        JCImport jcImport = treeMaker.Import(fieldAccess, false);
        ListBuffer<JCTree> imports = new ListBuffer<>();
        imports.append(jcImport);
        for (int i = 0; i < compilationUnit.defs.size(); i++) {
            imports.append(compilationUnit.defs.get(i));
        }
        compilationUnit.defs = imports.toList();
    }

    public void addInterface(Class<?> interfaceClass) {
        if (!hasInterface(interfaceClass)) {
            importClass(interfaceClass);
            java.util.List<JCExpression> implementing = classDecl.implementing;
            ListBuffer<JCExpression> statements = new ListBuffer<>();
            for (JCExpression impl : implementing) {
                statements.append(impl);
            }

            Symbol.ClassSymbol sym = new Symbol.ClassSymbol(SEQUENCE.nextId(), names.fromString(interfaceClass.getSimpleName()), null);
            statements.append(treeMaker.Ident(sym));
            classDecl.implementing = statements.toList();
        }
    }

    public void setNoArgPrivateConstructor() {
        for (JCTree jcTree : classDecl.defs) {
            if (jcTree instanceof JCMethodDecl) {
                JCMethodDecl methodDecl = (JCMethodDecl) jcTree;
                if (CONSTRUCTOR_NAME.equals(methodDecl.name.toString()) && methodDecl.params.isEmpty()) {
                    methodDecl.mods = treeMaker.Modifiers(Flags.PRIVATE);
                }
            }
        }
    }

    public void addNoArgPrivateConstructor() {
        ListBuffer<JCTree> out = new ListBuffer<>();
        JCMethodDecl constructor = treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString(CONSTRUCTOR_NAME),
                null,
                nil(),
                nil(),
                nil(),
                treeMaker.Block(0L, nil()),
                null);
        out.add(constructor);

        for (JCTree jcTree : classDecl.defs) {
            if (jcTree instanceof JCMethodDecl) {
                JCMethodDecl methodDecl = (JCMethodDecl) jcTree;
                if (CONSTRUCTOR_NAME.equals(methodDecl.name.toString()) && methodDecl.params.isEmpty()) {
                    continue;
                }
            }
            out.add(jcTree);
        }

        classDecl.defs = out.toList();
    }

}
