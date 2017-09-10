package me.codyclaborn.lox;

import jdk.nashorn.internal.objects.annotations.Optimistic;

public class AstPrinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return null;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return rightParenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return rightParenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return rightParenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitPostfixExpr(Expr.Postfix expr) {return leftParenthesize(expr.operator.lexeme, expr.left); }

    @Override
    public String visitConditionalExpr(Expr.Conditional expr) {return "(" + print(expr.left) + " ? " + print(expr.mid) + " : " + print(expr.right) + ")"; }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return null;
    }

    private String rightParenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    private String leftParenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        for (Expr expr : exprs) {
            builder.append(expr.accept(this));
            builder.append(" ");
        }
        builder.append(name).append(")");

        return builder.toString();
    }
}
