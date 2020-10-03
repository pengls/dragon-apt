package com.dragon.apt.helper;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import javax.annotation.processing.ProcessingEnvironment;
import static com.sun.tools.javac.tree.JCTree.*;

/**
 * @author eason peng
 * Javac上下文环境
 */
public abstract class JcContext {
    public static final Sequence SEQUENCE = new Sequence();
    public static final String CONSTRUCTOR_NAME = "<init>";
    public static final String RETURN_TYPE_VOID = "void";
    public static final String VARIABLE_PREFIX = "_";

    protected JavacProcessingEnvironment javacProcessingEnvironment;
    protected JavacTrees trees;
    protected TreeMaker treeMaker;
    protected Names names;

    public JcContext(ProcessingEnvironment env) {
        this.javacProcessingEnvironment = (JavacProcessingEnvironment) env;
        this.trees = JavacTrees.instance(env);
        this.treeMaker = TreeMaker.instance(javacProcessingEnvironment.getContext());
        this.names = Names.instance(javacProcessingEnvironment.getContext());
    }

    public Name getNameFromString(String s) {
        return names.fromString(s);
    }

    /**
     * e.g : java.lang.String
     * @param components
     * @return
     */
    public JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }

    /**
     * 定义变量
     * @param modifiers
     * @param varname
     * @param vartype
     * @param init
     * @return
     */
    public JCVariableDecl makeVarDef(JCModifiers modifiers, String varname, String vartype, JCExpression init) {
        return makeVarDef(modifiers, varname, memberAccess(vartype), init);
    }

    /**
     * 定义变量
     * @param modifiers
     * @param varname
     * @param vartype
     * @param init
     * @return
     */
    public JCVariableDecl makeVarDef(JCModifiers modifiers, String varname, JCExpression vartype, JCExpression init) {
        return treeMaker.VarDef(modifiers, getNameFromString(varname), vartype, init);
    }

    /**
     * makeAssignment(treeMaker.Ident(getNameFromString("a")), treeMaker.Literal("hello world"));
     *  --> a = "assignment test";
     * @param lhs
     * @param rhs
     * @return
     */
    public JCExpressionStatement makeAssignment(JCExpression lhs, JCExpression rhs) {
        return treeMaker.Exec(treeMaker.Assign(lhs, rhs));
    }

    /**
     * a = "hello" + "word";
     * @param varname
     * @param exp1
     * @param exp2
     * @return
     */
    public JCExpressionStatement makeAssignment(Tag tag, String varname, JCExpression exp1, JCExpression exp2) {
        return treeMaker.Exec(treeMaker.Assign(treeMaker.Ident(getNameFromString(varname)), treeMaker.Binary(tag, exp1, exp2)));
    }

    public JavacProcessingEnvironment getJavacProcessingEnvironment() {
        return javacProcessingEnvironment;
    }

    public void setJavacProcessingEnvironment(JavacProcessingEnvironment javacProcessingEnvironment) {
        this.javacProcessingEnvironment = javacProcessingEnvironment;
    }

    public JavacTrees getTrees() {
        return trees;
    }

    public void setTrees(JavacTrees trees) {
        this.trees = trees;
    }

    public TreeMaker getTreeMaker() {
        return treeMaker;
    }

    public void setTreeMaker(TreeMaker treeMaker) {
        this.treeMaker = treeMaker;
    }

    public Names getNames() {
        return names;
    }

    public void setNames(Names names) {
        this.names = names;
    }
}
