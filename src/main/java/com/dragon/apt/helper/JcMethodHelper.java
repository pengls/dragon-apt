package com.dragon.apt.helper;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static com.sun.tools.javac.code.Symbol.MethodSymbol;
import static com.sun.tools.javac.tree.JCTree.*;

/**
 * @author eason peng
 */
public class JcMethodHelper extends JcContext {
    private JCMethodDecl methodDecl;
    private MethodSymbol methodSym;
    private Element element;

    public JcMethodHelper(Element element, ProcessingEnvironment env) {
        super(env);
        this.methodDecl = (JCMethodDecl) trees.getTree(element);
        this.methodSym = (MethodSymbol) element;
        this.element = element;
    }

    public String getMethodName() {
        return methodSym.name.toString();
    }

    public JCExpression getReturnType() {
        return methodDecl.restype;
    }

    public boolean hasReturnValue() {
        return !RETURN_TYPE_VOID.equals(getReturnType().toString());
    }

    public com.sun.tools.javac.util.List<Symbol.VarSymbol> getArgs() {
        return methodSym.params;
    }

    public boolean hasModifier(int modifier) {
        return methodDecl.mods.flags % (modifier * 2) >= modifier;
    }

    public void setModifier(int modifier) {
        methodDecl.mods.flags = modifier;
    }

    /**
     * 在方法第一行或最后一行添加代码块
     *
     * @param statement
     * @param pos       0:insert pre  1:insert after
     */
    public void insertBlock(JCStatement statement, int pos) {
        ListBuffer<JCStatement> statements = new ListBuffer<>();
        if (pos == 0) {
            statements.append(statement);
        }
        for (JCTree.JCStatement stat : methodDecl.body.stats) {
            statements.append(stat);
        }
        if (pos == 1) {
            statements.append(statement);
        }
        methodDecl.body.stats = statements.toList();
    }

    /**
     * 在方法第一行或最后一行添加表达式
     *
     * @param express
     * @param pos     0:insert pre  1:insert after
     */
    public void insertExpression(JCExpression express, int pos) {
        ListBuffer<JCStatement> statements = new ListBuffer<>();
        if (pos == 0) {
            statements.append(treeMaker.Exec(express));
        }
        for (JCStatement statement : methodDecl.body.stats) {
            statements.append(statement);
        }
        if (pos == 1) {
            statements.append(treeMaker.Exec(express));
        }
        methodDecl.body.stats = statements.toList();
    }
}
