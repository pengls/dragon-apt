package com.dragon.apt.handle;

import com.dragon.apt.ConfigUtil;
import com.dragon.apt.annotation.Debugger;
import com.dragon.apt.helper.JcContext;
import com.dragon.apt.helper.JcMethodHelper;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.Set;

import static com.sun.tools.javac.tree.JCTree.*;

/**
 * @author eason peng
 */
public class DebuggerAnnotationHandle extends AnnotationHandle {

    public DebuggerAnnotationHandle(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    @Override
    public void handleAnnotation() {
        if (!ConfigUtil.getDebuggerOpen()) {
            return;
        }
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Debugger.class);
        if (elements == null || elements.isEmpty()) {
            return;
        }
        for (Element element : elements) {
            if (!(element instanceof ExecutableElement)) {
                continue;
            }
            handleMethod(element);
        }
    }

    private void handleMethod(Element element) {
        JCTree tree = trees.getTree(element);
        tree.accept(new TreeTranslator() {
            @Override
            public void visitBlock(JCBlock tree) {
                JcMethodHelper methodHelper = new JcMethodHelper(element, processingEnv);
                ListBuffer<JCStatement> statements = new ListBuffer();

                /**
                 * create code: Long _methodStartTime = System.currentTimeMillis();
                 */
                String startTimeVar = JcContext.VARIABLE_PREFIX + "methodStartTime";
                JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString("System")), names.fromString("currentTimeMillis"));
                JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
                JCVariableDecl variableDecl = methodHelper.makeVarDef(treeMaker.Modifiers(0), startTimeVar, "Long", methodInvocation);
                statements.append(variableDecl);

                for (int i = 0; i < tree.getStatements().size(); i++) {
                    statements.append(tree.getStatements().get(i));
                }

                /**
                 * create code: Long _methodEndTime = System.currentTimeMillis();
                 */
                String endTimeVar = JcContext.VARIABLE_PREFIX + "methodEndTime";
                fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString("System")), names.fromString("currentTimeMillis"));
                methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
                variableDecl = methodHelper.makeVarDef(treeMaker.Modifiers(0), endTimeVar, "Long", methodInvocation);
                statements.append(variableDecl);

                /**
                 * create code: String _out = "";
                 */
                String printVar = JcContext.VARIABLE_PREFIX + "out";
                JCVariableDecl printDecl = methodHelper.makeVarDef(treeMaker.Modifiers(0), printVar, "String", treeMaker.Literal(""));
                statements.append(printDecl);

                /**
                 * create code: Long _fee = 0;
                 */
                String feeVar = JcContext.VARIABLE_PREFIX + "fee";
                JCVariableDecl feeDecl = methodHelper.makeVarDef(treeMaker.Modifiers(0), feeVar, ("Long"), treeMaker.Literal(0L));
                statements.append(feeDecl);

                /**
                 * create code: _fee = _methodEndTime - _methodStartTime;
                 */
                JCExpressionStatement feeStat = methodHelper.makeAssignment(Tag.MINUS, feeVar, treeMaker.Ident(methodHelper.getNameFromString(endTimeVar)), treeMaker.Ident(methodHelper.getNameFromString(startTimeVar)));
                statements.append(feeStat);

                /**
                 * create code: _out = "method-name : " + methodName + ", fee : " + _fee;
                 */
                String methodName = methodHelper.getMethodName();
                String output_A = "method-name : " + methodName + ", fee : ";
                JCExpressionStatement outStat = methodHelper.makeAssignment(Tag.PLUS, printVar, treeMaker.Literal(output_A), treeMaker.Ident(methodHelper.getNameFromString(feeVar)));
                statements.append(outStat);

                /**
                 * create code: System.out.println(_out);
                 */
                JCExpressionStatement printStat = treeMaker.Exec(treeMaker.Apply(
                        List.of(methodHelper.memberAccess("String")),
                        methodHelper.memberAccess("System.out.println"),
                        List.of(treeMaker.Ident(methodHelper.getNameFromString(printVar))))
                );
                statements.append(printStat);

                result = treeMaker.Block(0, statements.toList());
            }
        });
    }
}
