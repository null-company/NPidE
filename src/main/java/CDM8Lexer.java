// Generated from ./src/main/kotlin/ru/nsu_null/npide/parser/CDM8.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CDM8Lexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COLON=1, REGISTER=2, R1=3, R2=4, R3=5, R0=6, RR_INSTR=7, R_INSTR=8, RC_INSTR=9, 
		C_INSTR=10, INSTR=11, C_PSEUDO_INSTR=12, ID_PSEUDO_INSTR=13, CMP_KEYWORD=14, 
		LOOP_KEYWORD=15, MACRO=16, C_MACRO_INST=17, R_MACRO_INST=18, PREDEFINED_MACRO_INSTRUCTIONS=19, 
		WS=20, NEWLINE=21, NUMBER=22, ID=23, COMMENT=24;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COLON", "REGISTER", "R1", "R2", "R3", "R0", "RR_INSTR", "R_INSTR", "RC_INSTR", 
			"C_INSTR", "INSTR", "C_PSEUDO_INSTR", "ID_PSEUDO_INSTR", "CMP_KEYWORD", 
			"LOOP_KEYWORD", "MACRO", "C_MACRO_INST", "R_MACRO_INST", "PREDEFINED_MACRO_INSTRUCTIONS", 
			"WS", "NEWLINE", "NUMBER", "INT", "HEX", "BIN", "ID", "Identifier", "IdentifierNondigit", 
			"Nondigit", "Digit", "UniversalCharacterName", "HexQuad", "HexadecimalDigit", 
			"COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "':'", null, "'r1'", "'r2'", "'r3'", "'r0'", null, null, null, 
			null, null, null, null, null, null, "'macro'", null, "'save'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COLON", "REGISTER", "R1", "R2", "R3", "R0", "RR_INSTR", "R_INSTR", 
			"RC_INSTR", "C_INSTR", "INSTR", "C_PSEUDO_INSTR", "ID_PSEUDO_INSTR", 
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


	public CDM8Lexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CDM8.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\32\u0253\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\3\2\3\2\3\3\3\3\3\3\3\3\5\3N\n\3\3\4\3\4\3\4\3\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\5\bz\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u00a8\n"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00b4\n\n\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13"+
		"\u00fe\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\5\f\u0124\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\5\r\u0132\n\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\5\16\u013f\n\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u0164"+
		"\n\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\5\20\u0173\n\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\5\22\u0184\n\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u01f7\n\24\3\25\6\25\u01fa"+
		"\n\25\r\25\16\25\u01fb\3\25\3\25\3\26\5\26\u0201\n\26\3\26\3\26\3\26\3"+
		"\26\3\27\3\27\3\27\6\27\u020a\n\27\r\27\16\27\u020b\3\27\3\27\3\27\6\27"+
		"\u0211\n\27\r\27\16\27\u0212\3\27\5\27\u0216\n\27\3\30\3\30\3\30\7\30"+
		"\u021b\n\30\f\30\16\30\u021e\13\30\5\30\u0220\n\30\3\31\3\31\3\32\3\32"+
		"\3\33\3\33\3\34\3\34\3\34\7\34\u022b\n\34\f\34\16\34\u022e\13\34\3\35"+
		"\3\35\5\35\u0232\n\35\3\36\3\36\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3 \3 \3"+
		" \5 \u0242\n \3!\3!\3!\3!\3!\3\"\3\"\3#\3#\7#\u024d\n#\f#\16#\u0250\13"+
		"#\3#\3#\2\2$\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16"+
		"\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\2\61\2\63\2\65\31\67"+
		"\29\2;\2=\2?\2A\2C\2E\32\3\2\n\b\2\13\13\"\"..\u00a2\u00a2\u2005\u2005"+
		"\uff01\uff01\4\2ZZzz\3\2dd\3\2\63;\3\2\62;\5\2\62;CHch\5\2C\\aac|\4\2"+
		"\f\f\16\17\2\u02bc\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2\65\3\2\2\2\2E\3\2\2\2\3G\3\2\2\2\5M\3\2\2\2\7O\3\2\2\2\tR\3\2"+
		"\2\2\13U\3\2\2\2\rX\3\2\2\2\17y\3\2\2\2\21\u00a7\3\2\2\2\23\u00b3\3\2"+
		"\2\2\25\u00fd\3\2\2\2\27\u0123\3\2\2\2\31\u0131\3\2\2\2\33\u013e\3\2\2"+
		"\2\35\u0163\3\2\2\2\37\u0172\3\2\2\2!\u0174\3\2\2\2#\u0183\3\2\2\2%\u0185"+
		"\3\2\2\2\'\u01f6\3\2\2\2)\u01f9\3\2\2\2+\u0200\3\2\2\2-\u0215\3\2\2\2"+
		"/\u021f\3\2\2\2\61\u0221\3\2\2\2\63\u0223\3\2\2\2\65\u0225\3\2\2\2\67"+
		"\u0227\3\2\2\29\u0231\3\2\2\2;\u0233\3\2\2\2=\u0235\3\2\2\2?\u0241\3\2"+
		"\2\2A\u0243\3\2\2\2C\u0248\3\2\2\2E\u024a\3\2\2\2GH\7<\2\2H\4\3\2\2\2"+
		"IN\5\7\4\2JN\5\t\5\2KN\5\13\6\2LN\5\r\7\2MI\3\2\2\2MJ\3\2\2\2MK\3\2\2"+
		"\2ML\3\2\2\2N\6\3\2\2\2OP\7t\2\2PQ\7\63\2\2Q\b\3\2\2\2RS\7t\2\2ST\7\64"+
		"\2\2T\n\3\2\2\2UV\7t\2\2VW\7\65\2\2W\f\3\2\2\2XY\7t\2\2YZ\7\62\2\2Z\16"+
		"\3\2\2\2[\\\7n\2\2\\z\7f\2\2]^\7u\2\2^z\7v\2\2_`\7o\2\2`a\7q\2\2ab\7x"+
		"\2\2bz\7g\2\2cd\7c\2\2de\7f\2\2ez\7f\2\2fg\7c\2\2gh\7f\2\2hi\7f\2\2iz"+
		"\7e\2\2jk\7\"\2\2kl\7u\2\2lm\7w\2\2mz\7d\2\2no\7e\2\2op\7o\2\2pz\7r\2"+
		"\2qr\7c\2\2rs\7p\2\2sz\7f\2\2tu\7q\2\2uz\7t\2\2vw\7z\2\2wx\7q\2\2xz\7"+
		"t\2\2y[\3\2\2\2y]\3\2\2\2y_\3\2\2\2yc\3\2\2\2yf\3\2\2\2yj\3\2\2\2yn\3"+
		"\2\2\2yq\3\2\2\2yt\3\2\2\2yv\3\2\2\2z\20\3\2\2\2{|\7p\2\2|}\7g\2\2}\u00a8"+
		"\7i\2\2~\177\7f\2\2\177\u0080\7g\2\2\u0080\u00a8\7e\2\2\u0081\u0082\7"+
		"k\2\2\u0082\u0083\7p\2\2\u0083\u00a8\7e\2\2\u0084\u0085\7u\2\2\u0085\u0086"+
		"\7j\2\2\u0086\u00a8\7t\2\2\u0087\u0088\7u\2\2\u0088\u0089\7j\2\2\u0089"+
		"\u008a\7t\2\2\u008a\u00a8\7c\2\2\u008b\u008c\7u\2\2\u008c\u008d\7j\2\2"+
		"\u008d\u008e\7n\2\2\u008e\u00a8\7c\2\2\u008f\u0090\7t\2\2\u0090\u0091"+
		"\7q\2\2\u0091\u00a8\7n\2\2\u0092\u0093\7r\2\2\u0093\u0094\7w\2\2\u0094"+
		"\u0095\7u\2\2\u0095\u00a8\7j\2\2\u0096\u0097\7r\2\2\u0097\u0098\7q\2\2"+
		"\u0098\u00a8\7r\2\2\u0099\u009a\7u\2\2\u009a\u009b\7v\2\2\u009b\u009c"+
		"\7u\2\2\u009c\u00a8\7r\2\2\u009d\u009e\7n\2\2\u009e\u009f\7f\2\2\u009f"+
		"\u00a0\7u\2\2\u00a0\u00a8\7r\2\2\u00a1\u00a2\7v\2\2\u00a2\u00a3\7u\2\2"+
		"\u00a3\u00a8\7v\2\2\u00a4\u00a5\7e\2\2\u00a5\u00a6\7n\2\2\u00a6\u00a8"+
		"\7t\2\2\u00a7{\3\2\2\2\u00a7~\3\2\2\2\u00a7\u0081\3\2\2\2\u00a7\u0084"+
		"\3\2\2\2\u00a7\u0087\3\2\2\2\u00a7\u008b\3\2\2\2\u00a7\u008f\3\2\2\2\u00a7"+
		"\u0092\3\2\2\2\u00a7\u0096\3\2\2\2\u00a7\u0099\3\2\2\2\u00a7\u009d\3\2"+
		"\2\2\u00a7\u00a1\3\2\2\2\u00a7\u00a4\3\2\2\2\u00a8\22\3\2\2\2\u00a9\u00aa"+
		"\7n\2\2\u00aa\u00ab\7f\2\2\u00ab\u00b4\7e\2\2\u00ac\u00ad\7n\2\2\u00ad"+
		"\u00ae\7f\2\2\u00ae\u00b4\7k\2\2\u00af\u00b0\7n\2\2\u00b0\u00b1\7f\2\2"+
		"\u00b1\u00b2\7u\2\2\u00b2\u00b4\7c\2\2\u00b3\u00a9\3\2\2\2\u00b3\u00ac"+
		"\3\2\2\2\u00b3\u00af\3\2\2\2\u00b4\24\3\2\2\2\u00b5\u00b6\7d\2\2\u00b6"+
		"\u00b7\7g\2\2\u00b7\u00fe\7s\2\2\u00b8\u00b9\7d\2\2\u00b9\u00fe\7|\2\2"+
		"\u00ba\u00bb\7d\2\2\u00bb\u00bc\7p\2\2\u00bc\u00fe\7g\2\2\u00bd\u00be"+
		"\7d\2\2\u00be\u00bf\7p\2\2\u00bf\u00fe\7|\2\2\u00c0\u00c1\7d\2\2\u00c1"+
		"\u00c2\7j\2\2\u00c2\u00fe\7u\2\2\u00c3\u00c4\7d\2\2\u00c4\u00c5\7e\2\2"+
		"\u00c5\u00fe\7u\2\2\u00c6\u00c7\7d\2\2\u00c7\u00c8\7n\2\2\u00c8\u00fe"+
		"\7q\2\2\u00c9\u00ca\7d\2\2\u00ca\u00cb\7e\2\2\u00cb\u00fe\7e\2\2\u00cc"+
		"\u00cd\7d\2\2\u00cd\u00ce\7o\2\2\u00ce\u00fe\7k\2\2\u00cf\u00d0\7d\2\2"+
		"\u00d0\u00d1\7r\2\2\u00d1\u00fe\7n\2\2\u00d2\u00d3\7d\2\2\u00d3\u00d4"+
		"\7x\2\2\u00d4\u00fe\7u\2\2\u00d5\u00d6\7d\2\2\u00d6\u00d7\7x\2\2\u00d7"+
		"\u00fe\7e\2\2\u00d8\u00d9\7d\2\2\u00d9\u00da\7j\2\2\u00da\u00fe\7k\2\2"+
		"\u00db\u00dc\7d\2\2\u00dc\u00dd\7n\2\2\u00dd\u00fe\7u\2\2\u00de\u00df"+
		"\7d\2\2\u00df\u00e0\7i\2\2\u00e0\u00fe\7g\2\2\u00e1\u00e2\7d\2\2\u00e2"+
		"\u00e3\7n\2\2\u00e3\u00fe\7v\2\2\u00e4\u00e5\7d\2\2\u00e5\u00e6\7i\2\2"+
		"\u00e6\u00fe\7v\2\2\u00e7\u00e8\7d\2\2\u00e8\u00e9\7n\2\2\u00e9\u00fe"+
		"\7g\2\2\u00ea\u00eb\7d\2\2\u00eb\u00fe\7t\2\2\u00ec\u00ed\7l\2\2\u00ed"+
		"\u00ee\7u\2\2\u00ee\u00fe\7t\2\2\u00ef\u00f0\7q\2\2\u00f0\u00f1\7u\2\2"+
		"\u00f1\u00f2\7k\2\2\u00f2\u00fe\7z\2\2\u00f3\u00f4\7u\2\2\u00f4\u00f5"+
		"\7g\2\2\u00f5\u00f6\7v\2\2\u00f6\u00f7\7u\2\2\u00f7\u00fe\7r\2\2\u00f8"+
		"\u00f9\7c\2\2\u00f9\u00fa\7f\2\2\u00fa\u00fb\7f\2\2\u00fb\u00fc\7u\2\2"+
		"\u00fc\u00fe\7r\2\2\u00fd\u00b5\3\2\2\2\u00fd\u00b8\3\2\2\2\u00fd\u00ba"+
		"\3\2\2\2\u00fd\u00bd\3\2\2\2\u00fd\u00c0\3\2\2\2\u00fd\u00c3\3\2\2\2\u00fd"+
		"\u00c6\3\2\2\2\u00fd\u00c9\3\2\2\2\u00fd\u00cc\3\2\2\2\u00fd\u00cf\3\2"+
		"\2\2\u00fd\u00d2\3\2\2\2\u00fd\u00d5\3\2\2\2\u00fd\u00d8\3\2\2\2\u00fd"+
		"\u00db\3\2\2\2\u00fd\u00de\3\2\2\2\u00fd\u00e1\3\2\2\2\u00fd\u00e4\3\2"+
		"\2\2\u00fd\u00e7\3\2\2\2\u00fd\u00ea\3\2\2\2\u00fd\u00ec\3\2\2\2\u00fd"+
		"\u00ef\3\2\2\2\u00fd\u00f3\3\2\2\2\u00fd\u00f8\3\2\2\2\u00fe\26\3\2\2"+
		"\2\u00ff\u0100\7y\2\2\u0100\u0101\7c\2\2\u0101\u0102\7k\2\2\u0102\u0124"+
		"\7v\2\2\u0103\u0104\7j\2\2\u0104\u0105\7c\2\2\u0105\u0106\7n\2\2\u0106"+
		"\u0124\7v\2\2\u0107\u0108\7p\2\2\u0108\u0109\7q\2\2\u0109\u0124\7r\2\2"+
		"\u010a\u010b\7t\2\2\u010b\u010c\7v\2\2\u010c\u0124\7u\2\2\u010d\u010e"+
		"\7e\2\2\u010e\u010f\7t\2\2\u010f\u0124\7e\2\2\u0110\u0111\7t\2\2\u0111"+
		"\u0112\7v\2\2\u0112\u0124\7k\2\2\u0113\u0114\7r\2\2\u0114\u0115\7w\2\2"+
		"\u0115\u0116\7u\2\2\u0116\u0117\7j\2\2\u0117\u0118\7c\2\2\u0118\u0119"+
		"\7n\2\2\u0119\u0124\7n\2\2\u011a\u011b\7r\2\2\u011b\u011c\7q\2\2\u011c"+
		"\u011d\7r\2\2\u011d\u011e\7c\2\2\u011e\u011f\7n\2\2\u011f\u0124\7n\2\2"+
		"\u0120\u0121\7k\2\2\u0121\u0122\7q\2\2\u0122\u0124\7k\2\2\u0123\u00ff"+
		"\3\2\2\2\u0123\u0103\3\2\2\2\u0123\u0107\3\2\2\2\u0123\u010a\3\2\2\2\u0123"+
		"\u010d\3\2\2\2\u0123\u0110\3\2\2\2\u0123\u0113\3\2\2\2\u0123\u011a\3\2"+
		"\2\2\u0123\u0120\3\2\2\2\u0124\30\3\2\2\2\u0125\u0126\7c\2\2\u0126\u0127"+
		"\7u\2\2\u0127\u0128\7g\2\2\u0128\u0129\7e\2\2\u0129\u0132\7v\2\2\u012a"+
		"\u012b\7f\2\2\u012b\u0132\7e\2\2\u012c\u012d\7f\2\2\u012d\u0132\7u\2\2"+
		"\u012e\u012f\7t\2\2\u012f\u0130\7w\2\2\u0130\u0132\7p\2\2\u0131\u0125"+
		"\3\2\2\2\u0131\u012a\3\2\2\2\u0131\u012c\3\2\2\2\u0131\u012e\3\2\2\2\u0132"+
		"\32\3\2\2\2\u0133\u0134\7t\2\2\u0134\u0135\7u\2\2\u0135\u0136\7g\2\2\u0136"+
		"\u0137\7e\2\2\u0137\u013f\7v\2\2\u0138\u0139\7v\2\2\u0139\u013a\7r\2\2"+
		"\u013a\u013b\7n\2\2\u013b\u013c\7c\2\2\u013c\u013d\7v\2\2\u013d\u013f"+
		"\7g\2\2\u013e\u0133\3\2\2\2\u013e\u0138\3\2\2\2\u013f\34\3\2\2\2\u0140"+
		"\u0141\7i\2\2\u0141\u0164\7v\2\2\u0142\u0143\7n\2\2\u0143\u0164\7v\2\2"+
		"\u0144\u0145\7n\2\2\u0145\u0164\7g\2\2\u0146\u0147\7i\2\2\u0147\u0164"+
		"\7g\2\2\u0148\u0149\7o\2\2\u0149\u0164\7k\2\2\u014a\u014b\7r\2\2\u014b"+
		"\u0164\7n\2\2\u014c\u014d\7g\2\2\u014d\u0164\7s\2\2\u014e\u014f\7p\2\2"+
		"\u014f\u0164\7g\2\2\u0150\u0164\7|\2\2\u0151\u0152\7p\2\2\u0152\u0164"+
		"\7|\2\2\u0153\u0154\7e\2\2\u0154\u0164\7u\2\2\u0155\u0156\7e\2\2\u0156"+
		"\u0164\7e\2\2\u0157\u0158\7x\2\2\u0158\u0164\7u\2\2\u0159\u015a\7x\2\2"+
		"\u015a\u0164\7e\2\2\u015b\u015c\7j\2\2\u015c\u0164\7k\2\2\u015d\u015e"+
		"\7n\2\2\u015e\u0164\7q\2\2\u015f\u0160\7j\2\2\u0160\u0164\7u\2\2\u0161"+
		"\u0162\7n\2\2\u0162\u0164\7u\2\2\u0163\u0140\3\2\2\2\u0163\u0142\3\2\2"+
		"\2\u0163\u0144\3\2\2\2\u0163\u0146\3\2\2\2\u0163\u0148\3\2\2\2\u0163\u014a"+
		"\3\2\2\2\u0163\u014c\3\2\2\2\u0163\u014e\3\2\2\2\u0163\u0150\3\2\2\2\u0163"+
		"\u0151\3\2\2\2\u0163\u0153\3\2\2\2\u0163\u0155\3\2\2\2\u0163\u0157\3\2"+
		"\2\2\u0163\u0159\3\2\2\2\u0163\u015b\3\2\2\2\u0163\u015d\3\2\2\2\u0163"+
		"\u015f\3\2\2\2\u0163\u0161\3\2\2\2\u0164\36\3\2\2\2\u0165\u0166\7e\2\2"+
		"\u0166\u0167\7q\2\2\u0167\u0168\7p\2\2\u0168\u0169\7v\2\2\u0169\u016a"+
		"\7k\2\2\u016a\u016b\7p\2\2\u016b\u016c\7w\2\2\u016c\u0173\7g\2\2\u016d"+
		"\u016e\7d\2\2\u016e\u016f\7t\2\2\u016f\u0170\7g\2\2\u0170\u0171\7c\2\2"+
		"\u0171\u0173\7m\2\2\u0172\u0165\3\2\2\2\u0172\u016d\3\2\2\2\u0173 \3\2"+
		"\2\2\u0174\u0175\7o\2\2\u0175\u0176\7c\2\2\u0176\u0177\7e\2\2\u0177\u0178"+
		"\7t\2\2\u0178\u0179\7q\2\2\u0179\"\3\2\2\2\u017a\u017b\7o\2\2\u017b\u017c"+
		"\7r\2\2\u017c\u017d\7q\2\2\u017d\u0184\7r\2\2\u017e\u017f\7o\2\2\u017f"+
		"\u0180\7r\2\2\u0180\u0181\7w\2\2\u0181\u0182\7u\2\2\u0182\u0184\7j\2\2"+
		"\u0183\u017a\3\2\2\2\u0183\u017e\3\2\2\2\u0184$\3\2\2\2\u0185\u0186\7"+
		"u\2\2\u0186\u0187\7c\2\2\u0187\u0188\7x\2\2\u0188\u0189\7g\2\2\u0189&"+
		"\3\2\2\2\u018a\u018b\7l\2\2\u018b\u018c\7o\2\2\u018c\u01f7\7r\2\2\u018d"+
		"\u018e\7l\2\2\u018e\u018f\7u\2\2\u018f\u0190\7t\2\2\u0190\u01f7\7t\2\2"+
		"\u0191\u0192\7u\2\2\u0192\u0193\7j\2\2\u0193\u01f7\7n\2\2\u0194\u0195"+
		"\7d\2\2\u0195\u0196\7c\2\2\u0196\u0197\7p\2\2\u0197\u0198\7{\2\2\u0198"+
		"\u0199\7v\2\2\u0199\u019a\7j\2\2\u019a\u019b\7k\2\2\u019b\u019c\7p\2\2"+
		"\u019c\u01f7\7i\2\2\u019d\u019e\7d\2\2\u019e\u019f\7p\2\2\u019f\u01a0"+
		"\7i\2\2\u01a0\u01f7\7v\2\2\u01a1\u01a2\7d\2\2\u01a2\u01a3\7p\2\2\u01a3"+
		"\u01a4\7i\2\2\u01a4\u01f7\7g\2\2\u01a5\u01a6\7d\2\2\u01a6\u01a7\7p\2\2"+
		"\u01a7\u01a8\7g\2\2\u01a8\u01f7\7s\2\2\u01a9\u01aa\7d\2\2\u01aa\u01ab"+
		"\7p\2\2\u01ab\u01ac\7p\2\2\u01ac\u01f7\7g\2\2\u01ad\u01ae\7d\2\2\u01ae"+
		"\u01af\7p\2\2\u01af\u01b0\7n\2\2\u01b0\u01f7\7v\2\2\u01b1\u01b2\7d\2\2"+
		"\u01b2\u01b3\7p\2\2\u01b3\u01b4\7n\2\2\u01b4\u01f7\7g\2\2\u01b5\u01b6"+
		"\7d\2\2\u01b6\u01b7\7p\2\2\u01b7\u01b8\7j\2\2\u01b8\u01f7\7k\2\2\u01b9"+
		"\u01ba\7d\2\2\u01ba\u01bb\7p\2\2\u01bb\u01bc\7j\2\2\u01bc\u01f7\7u\2\2"+
		"\u01bd\u01be\7d\2\2\u01be\u01bf\7p\2\2\u01bf\u01c0\7e\2\2\u01c0\u01f7"+
		"\7u\2\2\u01c1\u01c2\7d\2\2\u01c2\u01c3\7p\2\2\u01c3\u01c4\7n\2\2\u01c4"+
		"\u01f7\7q\2\2\u01c5\u01c6\7d\2\2\u01c6\u01c7\7p\2\2\u01c7\u01c8\7n\2\2"+
		"\u01c8\u01f7\7u\2\2\u01c9\u01ca\7d\2\2\u01ca\u01cb\7p\2\2\u01cb\u01cc"+
		"\7e\2\2\u01cc\u01f7\7e\2\2\u01cd\u01ce\7d\2\2\u01ce\u01cf\7p\2\2\u01cf"+
		"\u01d0\7o\2\2\u01d0\u01f7\7k\2\2\u01d1\u01d2\7d\2\2\u01d2\u01d3\7p\2\2"+
		"\u01d3\u01d4\7r\2\2\u01d4\u01f7\7n\2\2\u01d5\u01d6\7d\2\2\u01d6\u01d7"+
		"\7p\2\2\u01d7\u01d8\7h\2\2\u01d8\u01d9\7c\2\2\u01d9\u01da\7n\2\2\u01da"+
		"\u01db\7u\2\2\u01db\u01f7\7g\2\2\u01dc\u01dd\7d\2\2\u01dd\u01de\7p\2\2"+
		"\u01de\u01df\7v\2\2\u01df\u01e0\7t\2\2\u01e0\u01e1\7w\2\2\u01e1\u01f7"+
		"\7g\2\2\u01e2\u01e3\7d\2\2\u01e3\u01e4\7p\2\2\u01e4\u01e5\7x\2\2\u01e5"+
		"\u01f7\7u\2\2\u01e6\u01e7\7d\2\2\u01e7\u01e8\7p\2\2\u01e8\u01e9\7x\2\2"+
		"\u01e9\u01f7\7e\2\2\u01ea\u01eb\7f\2\2\u01eb\u01ec\7g\2\2\u01ec\u01ed"+
		"\7h\2\2\u01ed\u01ee\7k\2\2\u01ee\u01ef\7p\2\2\u01ef\u01f7\7g\2\2\u01f0"+
		"\u01f1\7n\2\2\u01f1\u01f2\7f\2\2\u01f2\u01f7\7x\2\2\u01f3\u01f4\7u\2\2"+
		"\u01f4\u01f5\7v\2\2\u01f5\u01f7\7x\2\2\u01f6\u018a\3\2\2\2\u01f6\u018d"+
		"\3\2\2\2\u01f6\u0191\3\2\2\2\u01f6\u0194\3\2\2\2\u01f6\u019d\3\2\2\2\u01f6"+
		"\u01a1\3\2\2\2\u01f6\u01a5\3\2\2\2\u01f6\u01a9\3\2\2\2\u01f6\u01ad\3\2"+
		"\2\2\u01f6\u01b1\3\2\2\2\u01f6\u01b5\3\2\2\2\u01f6\u01b9\3\2\2\2\u01f6"+
		"\u01bd\3\2\2\2\u01f6\u01c1\3\2\2\2\u01f6\u01c5\3\2\2\2\u01f6\u01c9\3\2"+
		"\2\2\u01f6\u01cd\3\2\2\2\u01f6\u01d1\3\2\2\2\u01f6\u01d5\3\2\2\2\u01f6"+
		"\u01dc\3\2\2\2\u01f6\u01e2\3\2\2\2\u01f6\u01e6\3\2\2\2\u01f6\u01ea\3\2"+
		"\2\2\u01f6\u01f0\3\2\2\2\u01f6\u01f3\3\2\2\2\u01f7(\3\2\2\2\u01f8\u01fa"+
		"\t\2\2\2\u01f9\u01f8\3\2\2\2\u01fa\u01fb\3\2\2\2\u01fb\u01f9\3\2\2\2\u01fb"+
		"\u01fc\3\2\2\2\u01fc\u01fd\3\2\2\2\u01fd\u01fe\b\25\2\2\u01fe*\3\2\2\2"+
		"\u01ff\u0201\7\17\2\2\u0200\u01ff\3\2\2\2\u0200\u0201\3\2\2\2\u0201\u0202"+
		"\3\2\2\2\u0202\u0203\7\f\2\2\u0203\u0204\3\2\2\2\u0204\u0205\b\26\2\2"+
		"\u0205,\3\2\2\2\u0206\u0207\7\62\2\2\u0207\u0209\t\3\2\2\u0208\u020a\5"+
		"\61\31\2\u0209\u0208\3\2\2\2\u020a\u020b\3\2\2\2\u020b\u0209\3\2\2\2\u020b"+
		"\u020c\3\2\2\2\u020c\u0216\3\2\2\2\u020d\u020e\7\62\2\2\u020e\u0210\t"+
		"\4\2\2\u020f\u0211\5\63\32\2\u0210\u020f\3\2\2\2\u0211\u0212\3\2\2\2\u0212"+
		"\u0210\3\2\2\2\u0212\u0213\3\2\2\2\u0213\u0216\3\2\2\2\u0214\u0216\5/"+
		"\30\2\u0215\u0206\3\2\2\2\u0215\u020d\3\2\2\2\u0215\u0214\3\2\2\2\u0216"+
		".\3\2\2\2\u0217\u0220\7\62\2\2\u0218\u021c\t\5\2\2\u0219\u021b\t\6\2\2"+
		"\u021a\u0219\3\2\2\2\u021b\u021e\3\2\2\2\u021c\u021a\3\2\2\2\u021c\u021d"+
		"\3\2\2\2\u021d\u0220\3\2\2\2\u021e\u021c\3\2\2\2\u021f\u0217\3\2\2\2\u021f"+
		"\u0218\3\2\2\2\u0220\60\3\2\2\2\u0221\u0222\t\7\2\2\u0222\62\3\2\2\2\u0223"+
		"\u0224\4\62\63\2\u0224\64\3\2\2\2\u0225\u0226\5\67\34\2\u0226\66\3\2\2"+
		"\2\u0227\u022c\59\35\2\u0228\u022b\59\35\2\u0229\u022b\5=\37\2\u022a\u0228"+
		"\3\2\2\2\u022a\u0229\3\2\2\2\u022b\u022e\3\2\2\2\u022c\u022a\3\2\2\2\u022c"+
		"\u022d\3\2\2\2\u022d8\3\2\2\2\u022e\u022c\3\2\2\2\u022f\u0232\5;\36\2"+
		"\u0230\u0232\5? \2\u0231\u022f\3\2\2\2\u0231\u0230\3\2\2\2\u0232:\3\2"+
		"\2\2\u0233\u0234\t\b\2\2\u0234<\3\2\2\2\u0235\u0236\t\6\2\2\u0236>\3\2"+
		"\2\2\u0237\u0238\7^\2\2\u0238\u0239\7w\2\2\u0239\u023a\3\2\2\2\u023a\u0242"+
		"\5A!\2\u023b\u023c\7^\2\2\u023c\u023d\7W\2\2\u023d\u023e\3\2\2\2\u023e"+
		"\u023f\5A!\2\u023f\u0240\5A!\2\u0240\u0242\3\2\2\2\u0241\u0237\3\2\2\2"+
		"\u0241\u023b\3\2\2\2\u0242@\3\2\2\2\u0243\u0244\5C\"\2\u0244\u0245\5C"+
		"\"\2\u0245\u0246\5C\"\2\u0246\u0247\5C\"\2\u0247B\3\2\2\2\u0248\u0249"+
		"\t\7\2\2\u0249D\3\2\2\2\u024a\u024e\7%\2\2\u024b\u024d\n\t\2\2\u024c\u024b"+
		"\3\2\2\2\u024d\u0250\3\2\2\2\u024e\u024c\3\2\2\2\u024e\u024f\3\2\2\2\u024f"+
		"\u0251\3\2\2\2\u0250\u024e\3\2\2\2\u0251\u0252\b#\3\2\u0252F\3\2\2\2\33"+
		"\2My\u00a7\u00b3\u00fd\u0123\u0131\u013e\u0163\u0172\u0183\u01f6\u01fb"+
		"\u0200\u020b\u0212\u0215\u021c\u021f\u022a\u022c\u0231\u0241\u024e\4\2"+
		"\3\2\2\4\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}