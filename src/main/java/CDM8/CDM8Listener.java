// Generated from C:\Users\Artem\Documents\IDE\NPidE\.\src\main\kotlin\ru\nsu_null\npide\parser\CDM8.g4 by ANTLR 4.9
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CDM8Parser}.
 */
public interface CDM8Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CDM8Parser#s}.
	 * @param ctx the parse tree
	 */
	void enterS(CDM8Parser.SContext ctx);
	/**
	 * Exit a parse tree produced by {@link CDM8Parser#s}.
	 * @param ctx the parse tree
	 */
	void exitS(CDM8Parser.SContext ctx);
	/**
	 * Enter a parse tree produced by the {@code global_def}
	 * labeled alternative in {@link CDM8Parser#inst}.
	 * @param ctx the parse tree
	 */
	void enterGlobal_def(CDM8Parser.Global_defContext ctx);
	/**
	 * Exit a parse tree produced by the {@code global_def}
	 * labeled alternative in {@link CDM8Parser#inst}.
	 * @param ctx the parse tree
	 */
	void exitGlobal_def(CDM8Parser.Global_defContext ctx);
	/**
	 * Enter a parse tree produced by the {@code def}
	 * labeled alternative in {@link CDM8Parser#inst}.
	 * @param ctx the parse tree
	 */
	void enterDef(CDM8Parser.DefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code def}
	 * labeled alternative in {@link CDM8Parser#inst}.
	 * @param ctx the parse tree
	 */
	void exitDef(CDM8Parser.DefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code usage}
	 * labeled alternative in {@link CDM8Parser#inst}.
	 * @param ctx the parse tree
	 */
	void enterUsage(CDM8Parser.UsageContext ctx);
	/**
	 * Exit a parse tree produced by the {@code usage}
	 * labeled alternative in {@link CDM8Parser#inst}.
	 * @param ctx the parse tree
	 */
	void exitUsage(CDM8Parser.UsageContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nomatters}
	 * labeled alternative in {@link CDM8Parser#inst}.
	 * @param ctx the parse tree
	 */
	void enterNomatters(CDM8Parser.NomattersContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nomatters}
	 * labeled alternative in {@link CDM8Parser#inst}.
	 * @param ctx the parse tree
	 */
	void exitNomatters(CDM8Parser.NomattersContext ctx);
}