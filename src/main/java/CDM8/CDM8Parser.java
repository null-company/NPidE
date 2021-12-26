// Generated from C:\Users\Artem\Documents\IDE\NPidE\.\src\main\kotlin\ru\nsu_null\npide\parser\CDM8.g4 by ANTLR 4.9
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CDM8Parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, COLON=2, REGISTER=3, R1=4, R2=5, R3=6, R0=7, RR_INSTR=8, R_INSTR=9, 
		RC_INSTR=10, C_INSTR=11, INSTR=12, C_PSEUDO_INSTR=13, ID_PSEUDO_INSTR=14, 
		CMP_KEYWORD=15, LOOP_KEYWORD=16, MACRO=17, C_MACRO_INST=18, R_MACRO_INST=19, 
		PREDEFINED_MACRO_INSTRUCTIONS=20, WS=21, NEWLINE=22, NUMBER=23, ID=24, 
		COMMENT=25;
	public static final int
		RULE_s = 0, RULE_inst = 1;
	private static String[] makeRuleNames() {
		return new String[] {
			"s", "inst"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'>'", "':'", null, "'r1'", "'r2'", "'r3'", "'r0'", null, null, 
			null, null, null, null, null, null, null, "'macro'", null, "'save'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "COLON", "REGISTER", "R1", "R2", "R3", "R0", "RR_INSTR", 
			"R_INSTR", "RC_INSTR", "C_INSTR", "INSTR", "C_PSEUDO_INSTR", "ID_PSEUDO_INSTR", 
			"CMP_KEYWORD", "LOOP_KEYWORD", "MACRO", "C_MACRO_INST", "R_MACRO_INST", 
			"PREDEFINED_MACRO_INSTRUCTIONS", "WS", "NEWLINE", "NUMBER", "ID", "COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "CDM8.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CDM8Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class SContext extends ParserRuleContext {
		public List<InstContext> inst() {
			return getRuleContexts(InstContext.class);
		}
		public InstContext inst(int i) {
			return getRuleContext(InstContext.class,i);
		}
		public SContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).enterS(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).exitS(this);
		}
	}

	public final SContext s() throws RecognitionException {
		SContext _localctx = new SContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_s);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(7);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1+1 ) {
					{
					{
					setState(4);
					inst();
					}
					} 
				}
				setState(9);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InstContext extends ParserRuleContext {
		public InstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inst; }
	 
		public InstContext() { }
		public void copyFrom(InstContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NomattersContext extends InstContext {
		public NomattersContext(InstContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).enterNomatters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).exitNomatters(this);
		}
	}
	public static class DefContext extends InstContext {
		public TerminalNode ID() { return getToken(CDM8Parser.ID, 0); }
		public TerminalNode COLON() { return getToken(CDM8Parser.COLON, 0); }
		public DefContext(InstContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).enterDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).exitDef(this);
		}
	}
	public static class UsageContext extends InstContext {
		public TerminalNode ID() { return getToken(CDM8Parser.ID, 0); }
		public UsageContext(InstContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).enterUsage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).exitUsage(this);
		}
	}
	public static class Global_defContext extends InstContext {
		public TerminalNode ID() { return getToken(CDM8Parser.ID, 0); }
		public Global_defContext(InstContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).enterGlobal_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CDM8Listener ) ((CDM8Listener)listener).exitGlobal_def(this);
		}
	}

	public final InstContext inst() throws RecognitionException {
		InstContext _localctx = new InstContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_inst);
		try {
			int _alt;
			setState(20);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new Global_defContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(10);
				match(ID);
				setState(11);
				match(T__0);
				}
				break;
			case 2:
				_localctx = new DefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(12);
				match(ID);
				setState(13);
				match(COLON);
				}
				break;
			case 3:
				_localctx = new UsageContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(14);
				match(ID);
				}
				break;
			case 4:
				_localctx = new NomattersContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(16); 
				_errHandler.sync(this);
				_alt = 1+1;
				do {
					switch (_alt) {
					case 1+1:
						{
						{
						setState(15);
						matchWildcard();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(18); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				} while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\33\31\4\2\t\2\4\3"+
		"\t\3\3\2\7\2\b\n\2\f\2\16\2\13\13\2\3\3\3\3\3\3\3\3\3\3\3\3\6\3\23\n\3"+
		"\r\3\16\3\24\5\3\27\n\3\3\3\4\t\24\2\4\2\4\2\2\2\33\2\t\3\2\2\2\4\26\3"+
		"\2\2\2\6\b\5\4\3\2\7\6\3\2\2\2\b\13\3\2\2\2\t\n\3\2\2\2\t\7\3\2\2\2\n"+
		"\3\3\2\2\2\13\t\3\2\2\2\f\r\7\32\2\2\r\27\7\3\2\2\16\17\7\32\2\2\17\27"+
		"\7\4\2\2\20\27\7\32\2\2\21\23\13\2\2\2\22\21\3\2\2\2\23\24\3\2\2\2\24"+
		"\25\3\2\2\2\24\22\3\2\2\2\25\27\3\2\2\2\26\f\3\2\2\2\26\16\3\2\2\2\26"+
		"\20\3\2\2\2\26\22\3\2\2\2\27\5\3\2\2\2\5\t\24\26";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}