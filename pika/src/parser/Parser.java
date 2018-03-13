package parser;

import java.util.Arrays;

import logging.PikaLogger;
import parseTree.*;
import parseTree.nodeTypes.*;
import tokens.*;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import lexicalAnalyzer.Scanner;


public class Parser {
	private Scanner scanner;
	private Token nowReading;
	private Token previouslyRead;
	
	public static ParseNode parse(Scanner scanner) {
		Parser parser = new Parser(scanner);
		return parser.parse();
	}
	public Parser(Scanner scanner) {
		super();
		this.scanner = scanner;
	}
	
	public ParseNode parse() {
		readToken();
		return parseProgram();
	}

	////////////////////////////////////////////////////////////
	// "program" is the start symbol S
	// S -> EXEC mainBlock
	
	private ParseNode parseProgram() {
		if((!startsProgram(nowReading))&&(!startsFunctionDeclaration(nowReading))) {
			return syntaxErrorNode("program");
		}
		ParseNode program = new ProgramNode(nowReading);
		
		while(startsFunctionDeclaration(nowReading)) {
			ParseNode func = parseFunctionDeclaration();
			program.appendChild(func);
		}
		
		expect(Keyword.EXEC);
		ParseNode mainBlock = parseMainBlock();
		program.appendChild(mainBlock);
		
		if(!(nowReading instanceof NullToken)) {
			return syntaxErrorNode("end of program");
		}
		
		return program;
	}
	private boolean startsProgram(Token token) {
		return token.isLextant(Keyword.EXEC);
	}
	
	///////////////////////////////////////////////////////////
	// function declaration
	
	private ParseNode parseFunctionDeclaration() {
		Token token = nowReading;
		
		expect(Keyword.FUNCTION);
		
		
		if(!startsIdentifier(nowReading)) {
			return syntaxErrorNode("identifier");
		}
		
		ParseNode identifier = parseIdentifier();
		
		if(!startsLambdaExpression(nowReading)) {
			return syntaxErrorNode("no matching lambda expression");
		}
		
		ParseNode lambda = parseLambdaExpression();
		
		return new FunctionDeclarationNode(token, identifier, lambda);
	}
	
	private boolean startsFunctionDeclaration(Token token) {
		return token.isLextant(Keyword.FUNCTION);
	}
	
	///////////////////////////////////////////////////////////
	// mainBlock
	
	// mainBlock -> { statement* }
	private ParseNode parseMainBlock() {
		if(!startsMainBlock(nowReading)) {
			return syntaxErrorNode("mainBlock");
		}
		ParseNode mainBlock = new MainBlockNode(nowReading);
		expect(Punctuator.OPEN_BRACE);
		
		while(startsStatement(nowReading)) {
			ParseNode statement = parseStatement();
			mainBlock.appendChild(statement);
		}
		expect(Punctuator.CLOSE_BRACE);
		return mainBlock;
	}
	private boolean startsMainBlock(Token token) {
		return token.isLextant(Punctuator.OPEN_BRACE);
	}
	
	
	///////////////////////////////////////////////////////////
	// statements
	
	// statement-> declaration | printStmt
	private ParseNode parseStatement() {
		if(!startsStatement(nowReading)) {
			return syntaxErrorNode("statement");
		}
		if(startsDeclaration(nowReading)) {
			return parseDeclaration();
		}
		if(startsMainBlock(nowReading)) {
			return parseMainBlock();
		}
		if(startsAssignmentStatement(nowReading)) {
			return parseAssignmentStatement();
		}
		if(startsPrintStatement(nowReading)) {
			return parsePrintStatement();
		}
		if(startsIfStatement(nowReading)) {
			return parseIfStatement();
		}
		if(startsWhileStatement(nowReading)) {
			return parseWhileStatement();
		}
		if(startsBreakStatement(nowReading)) {
			return parseBreakStatement();
		}
		if(startsContinueStatement(nowReading)) {
			return parseContinueStatement();
		}
		if(startsReleaseStatement(nowReading)) {
			return parseReleaseStatement();
		}
		if(startsReturnStatement(nowReading)) {
			return parseReturnStatement();
		}
		if(startsCallStatement(nowReading)) {
			return parseCallStatement();
		}
		return syntaxErrorNode("statement");
	}
	private boolean startsStatement(Token token) {
		return startsPrintStatement(token) ||
			   startsDeclaration(token) ||
			   startsMainBlock(token)||
			   startsAssignmentStatement(token)||
			   startsIfStatement(token)||
			   startsWhileStatement(token)||
			   startsReleaseStatement(token)||
			   startsBreakStatement(token)||
			   startsContinueStatement(token)||
			   startsReturnStatement(token)||
			   startsCallStatement(token);
	}
	
	// return statement
	private ParseNode parseReturnStatement() {
		Token token = nowReading;
		expect(Keyword.RETURN);
		
		if(!startsExpression(nowReading)) {
			return syntaxErrorNode("return statement");
		}
		
		ParseNode expr = parseExpression();
		expect(Punctuator.TERMINATOR);
		
		return new ReturnStatementNode(token, expr);
	}
	
	private boolean startsReturnStatement(Token token) {
		return token.isLextant(Keyword.RETURN);
	}
	
	// call statement
	private ParseNode parseCallStatement() {
		Token token = nowReading;
		expect(Keyword.CALL);
		
		if(!startsExpression(nowReading)) {
			return syntaxErrorNode("call statement");
		}
		
		ParseNode callFunc = parseExpression();
		expect(Punctuator.TERMINATOR);
		
		return new CallStatementNode(token, callFunc);
	}
	
	private boolean startsCallStatement(Token token) {
		return token.isLextant(Keyword.CALL);
	}
	
	// printStmt -> PRINT printExpressionList .
	private ParseNode parsePrintStatement() {
		if(!startsPrintStatement(nowReading)) {
			return syntaxErrorNode("print statement");
		}
		PrintStatementNode result = new PrintStatementNode(nowReading);
		
		readToken();
		result = parsePrintExpressionList(result);
		
		expect(Punctuator.TERMINATOR);
		return result;
	}
	private boolean startsPrintStatement(Token token) {
		return token.isLextant(Keyword.PRINT);
	}	

	// This adds the printExpressions it parses to the children of the given parent
	// printExpressionList -> printExpression* bowtie (,|;)  (note that this is nullable)

	private PrintStatementNode parsePrintExpressionList(PrintStatementNode parent) {
		while(startsPrintExpression(nowReading) || startsPrintSeparator(nowReading)) {
			parsePrintExpression(parent);
			parsePrintSeparator(parent);
		}
		return parent;
	}
	

	// This adds the printExpression it parses to the children of the given parent
	// printExpression -> (expr | nl)?     (nullable)
	
	private void parsePrintExpression(PrintStatementNode parent) {
		if(startsExpression(nowReading)) {
			ParseNode child = parseExpression();
			parent.appendChild(child);
		}
		else if(nowReading.isLextant(Keyword.NEWLINE)) {
			readToken();
			ParseNode child = new NewlineNode(previouslyRead);
			parent.appendChild(child);
		}
		else if(nowReading.isLextant(Keyword.TAB)) {
			readToken();
			ParseNode child = new TabNode(previouslyRead);
			parent.appendChild(child);
		}
		// else we interpret the printExpression as epsilon, and do nothing
	}
	private boolean startsPrintExpression(Token token) {
		return startsExpression(token) || token.isLextant(Keyword.NEWLINE) || token.isLextant(Keyword.TAB) ;
	}
	
	
	// This adds the printExpression it parses to the children of the given parent
	// printExpression -> expr? ,? nl? 
	
	private void parsePrintSeparator(PrintStatementNode parent) {
		if(!startsPrintSeparator(nowReading) && !nowReading.isLextant(Punctuator.TERMINATOR)) {
			ParseNode child = syntaxErrorNode("print separator");
			parent.appendChild(child);
			return;
		}
		
		if(nowReading.isLextant(Punctuator.SPACE)) {
			readToken();
			ParseNode child = new SpaceNode(previouslyRead);
			parent.appendChild(child);
		}
		else if(nowReading.isLextant(Punctuator.SEPARATOR)) {
			readToken();
		}		
		else if(nowReading.isLextant(Punctuator.TERMINATOR)) {
			// we're at the end of the bowtie and this printSeparator is not required.
			// do nothing.  Terminator is handled in a higher-level nonterminal.
		}
	}
	private boolean startsPrintSeparator(Token token) {
		return token.isLextant(Punctuator.SEPARATOR, Punctuator.SPACE) ;
	}
	
	
	
	// declaration -> CONST identifier := expression or VAR identifier := expression.
	private ParseNode parseDeclaration() {
		if(!startsDeclaration(nowReading)) {
			return syntaxErrorNode("declaration");
		}
		Token declarationToken = nowReading;
		
		readToken();
		
		ParseNode identifier = parseIdentifier();
		expect(Punctuator.ASSIGN);
		
		if(!startsExpression(nowReading)) {
			return syntaxErrorNode("expression");
		}
		ParseNode initializer = parseExpression();
		expect(Punctuator.TERMINATOR);
		
		return DeclarationNode.withChildren(declarationToken, identifier, initializer);
	}
	private boolean startsDeclaration(Token token) {
		return token.isLextant(Keyword.CONST) || token.isLextant(Keyword.VAR);
	}

	// Assignment Statement
	private ParseNode parseAssignmentStatement() {
		if(!startsAssignmentStatement(nowReading)) {
			return syntaxErrorNode("assignment statement");
		}
		
		ParseNode leftIdentifier = parseExpression();
		Token assign = nowReading;
		expect(Punctuator.ASSIGN);
		
		if(!startsExpression(nowReading)) {
			return syntaxErrorNode("expression");
		}
		ParseNode rightExpression = parseExpression();
		expect(Punctuator.TERMINATOR);
		
		return AssignmentStatementNode.withChildren(assign, leftIdentifier, rightExpression);
	}
	
	private boolean startsAssignmentStatement(Token token) {
		return startsExpression(token);
	}
	
	///////////////////////////////////////////////////////////
	// if statement
	private ParseNode parseIfStatement() {
		Token token = nowReading;
		expect(Keyword.IF);
		
		if(!startsParenthesesExpression(nowReading)) {
			return syntaxErrorNode("if statement");
		}
		
		ParseNode condition = parseParenthesesExpression();
		
		if(!startsMainBlock(nowReading)) {
			return syntaxErrorNode("if statement");
		}
		
		ParseNode ifContent = parseMainBlock();
		
		if(nowReading.isLextant(Keyword.ELSE)) {
			expect(Keyword.ELSE);
			
			if(!startsMainBlock(nowReading)) {
				return syntaxErrorNode("if statement");
			}
			
			ParseNode elseContent = parseMainBlock();
			
			return new IfStatementNode(token, condition, ifContent, elseContent);
		}
		else {
			return new IfStatementNode(token, condition, ifContent);
		}
	}
	
	private boolean startsIfStatement(Token token) {
		return token.isLextant(Keyword.IF);
	}
	
	///////////////////////////////////////////////////////////
	// while statement
	
	private ParseNode parseWhileStatement() {
		Token token = nowReading;
		expect(Keyword.WHILE);
		
		if(!startsParenthesesExpression(nowReading)) {
			return syntaxErrorNode("while statement");
		}
		
		ParseNode condition = parseParenthesesExpression();
		
		if(!startsMainBlock(nowReading)) {
			return syntaxErrorNode("while statement");
		}
		
		ParseNode content = parseMainBlock();
		
		return new WhileStatementNode(token, condition, content);
	}
	
	private boolean startsWhileStatement(Token token) {
		return token.isLextant(Keyword.WHILE);
	}
	
	///////////////////////////////////////////////////////////
	// break statement
	
	private ParseNode parseBreakStatement() {
		Token token = nowReading;
		expect(Keyword.BREAK);
		expect(Punctuator.TERMINATOR);
		
		return new BreakStatementNode(token);
	}
	
	private boolean startsBreakStatement(Token token) {
		return token.isLextant(Keyword.BREAK);
	}
	
	///////////////////////////////////////////////////////////
	// continue statement
	
	private ParseNode parseContinueStatement() {
		Token token = nowReading;
		expect(Keyword.CONTINUE);
		expect(Punctuator.TERMINATOR);
		
		return new ContinueStatementNode(token);
	}
	
	private boolean startsContinueStatement(Token token) {
		return token.isLextant(Keyword.CONTINUE);
	}
	
	///////////////////////////////////////////////////////////
	// release statement
	
	private ParseNode parseReleaseStatement() {
		Token token = nowReading;
		expect(Keyword.RELEASE);
		
		ParseNode expr = parseExpression();
		expect(Punctuator.TERMINATOR);
		
		return new ReleaseStatementNode(token, expr);
	}
	
	private boolean startsReleaseStatement(Token token) {
		return token.isLextant(Keyword.RELEASE);
	}
	
	///////////////////////////////////////////////////////////
	// expressions
	// expr                     -> comparisonExpression
	// comparisonExpression     -> additiveExpression [> additiveExpression]?
	// additiveExpression       -> multiplicativeExpression [+ multiplicativeExpression]*  (left-assoc)
	// multiplicativeExpression -> atomicExpression [MULT atomicExpression]*  (left-assoc)
	// atomicExpression         -> literal
	// literal                  -> intNumber | identifier | booleanConstant

	// expr  -> comparisonExpression
	private ParseNode parseExpression() {		
		if(!startsExpression(nowReading)) {
			return syntaxErrorNode("expression"); 
		}
		return parseOrExpression();
	}
	private boolean startsExpression(Token token) {
		return startsOrExpression(token);
	}
	
	// Lambda expression
	private ParseNode parseLambdaExpression() {
		ParseNode lambdaParamType = parseLambdaParamType();
		
		if(!startsFunctionBody(nowReading)) {
			return syntaxErrorNode("function body");
		}
		
		ParseNode functionBody = parseFunctionBody();
		
		return new LambdaNode(lambdaParamType, functionBody);
	}
	
	private boolean startsLambdaExpression(Token token) {
		return startsLambdaParamType(token);
	}
	
	// Lambda Parameter Type
	private ParseNode parseLambdaParamType() {
		ParseNode node = new LambdaParameterTypeNode();
		
		expect(Punctuator.SMALLER);
		
		while(startsParameter(nowReading)) {
			ParseNode para = parseParameter();
			
			node.appendChild(para);
			
			if(nowReading.isLextant(Punctuator.SEPARATOR)) {
				expect(Punctuator.SEPARATOR);
			}
		}
		
		expect(Punctuator.GREATER);
		
		expect(Punctuator.TO);
		
		ParseNode returnType = parseType();
		node.insertChild(returnType);
		
		return node;
	}
	
	private boolean startsLambdaParamType(Token token) {
		return token.isLextant(Punctuator.SMALLER);
	}
	
	// Lambda function body
	private ParseNode parseFunctionBody() {
		if(!startsFunctionBody(nowReading)) {
			return syntaxErrorNode("function body");
		}
		ParseNode functionBody = new FunctionBodyNode();
		expect(Punctuator.OPEN_BRACE);
		
		while(startsStatement(nowReading)) {
			ParseNode statement = parseStatement();
			functionBody.appendChild(statement);
		}
		expect(Punctuator.CLOSE_BRACE);
		return functionBody;
	}
	
	private boolean startsFunctionBody(Token token) {
		return token.isLextant(Punctuator.OPEN_BRACE);
	}
	
	
	// Parameter
	
	private ParseNode parseParameter() {
		if(!startsType(nowReading)) {
			return syntaxErrorNode("parameter");
		}
		
		ParseNode type = parseType();
		ParseNode identifier = parseIdentifier();
		return new ParameterNode(type, identifier);
	}
	
	private boolean startsParameter(Token token) {
		return startsType(token);
	}
	
	
	
	// || expression
	private ParseNode parseOrExpression() {
		if(!startsOrExpression(nowReading)) {
			return syntaxErrorNode("or expression");
		}
		
		ParseNode left = parseAndExpression();
		
		while(nowReading.isLextant(Punctuator.OR)) {
			Token token = nowReading;
			readToken();
			
			ParseNode right = parseAndExpression();
			left = BinaryOperatorNode.withChildren(token,left,right);
		}
		
		return left;
	}
	
	private boolean startsOrExpression(Token token) {
		return startsAndExpression(token);
	}

	// && expression
	private ParseNode parseAndExpression() {
		if(!startsAndExpression(nowReading)) {
			return syntaxErrorNode("and node");
		}
		
		ParseNode left = parseComparisonExpression();
		
		while(nowReading.isLextant(Punctuator.AND)) {
			Token token = nowReading;
			readToken();
			
			ParseNode right = parseComparisonExpression();
			left = BinaryOperatorNode.withChildren(token,left,right);
		}
		
		return left;
	}
	
	private boolean startsAndExpression(Token token) {
		return startsComparisonExpression(token);
	}
	
	// comparisonExpression -> additiveExpression [> additiveExpression]?
	private ParseNode parseComparisonExpression() {
		if(!startsComparisonExpression(nowReading)) {
			return syntaxErrorNode("comparison expression");
		}
		
		ParseNode left = parseAdditiveOrSubtractExpression();
		while(nowReading.isLextant( Punctuator.SMALLER , Punctuator.SMALLEROREQUAL 
				, Punctuator.GREATER , Punctuator.GREATEROREQUAL 
				,Punctuator.EQUAL, Punctuator.NOTEQUAL)
				) {
			Token compareToken = nowReading;
			readToken();
			ParseNode right = parseAdditiveOrSubtractExpression();
			
			left =  BinaryOperatorNode.withChildren(compareToken, left, right);
		}
		return left;

	}
	private boolean startsComparisonExpression(Token token) {
		return startsAdditiveOrSubtractExpression(token);
	}

	// additiveExpression -> multiplicativeExpression [+ multiplicativeExpression]*  (left-assoc)
	private ParseNode parseAdditiveOrSubtractExpression() {
		if(!startsAdditiveOrSubtractExpression(nowReading)) {
			return syntaxErrorNode("additiveOrSubtractExpression");
		}
		
		ParseNode left = parseMultiplicativeOrDivisionOrRationalExpression();
		while(nowReading.isLextant(Punctuator.ADD, Punctuator.SUBTRACT)) {
			Token token = nowReading;
			readToken();
			ParseNode right = parseMultiplicativeOrDivisionOrRationalExpression();
			
			left = BinaryOperatorNode.withChildren(token, left, right);
		}
		return left;
	}
	private boolean startsAdditiveOrSubtractExpression(Token token) {
		return startsMultiplicativeOrDivisionOrRationalExpression(token);
	}	

	// multiplicativeExpression -> atomicExpression [MULT atomicExpression]*  (left-assoc)
	private ParseNode parseMultiplicativeOrDivisionOrRationalExpression() {
		if(!startsMultiplicativeOrDivisionOrRationalExpression(nowReading)) {
			return syntaxErrorNode("multiplicativeOrDivisionExpression");
		}
		
		ParseNode left = parseUnaryOperatorExpression();
		while(nowReading.isLextant(Punctuator.MULTIPLY, Punctuator.DIVIDE, Punctuator.OVER, Punctuator.EXPRESSOVER, Punctuator.RATIONALIZE)) {
			Token multiplicativeToken = nowReading;
			readToken();
			ParseNode right = parseUnaryOperatorExpression();
			
			left = BinaryOperatorNode.withChildren(multiplicativeToken, left, right);
		}
		return left;
	}
	private boolean startsMultiplicativeOrDivisionOrRationalExpression(Token token) {
		return startsUnaryOperatorExpression(token);
	}
	
	// unary operator
	private ParseNode parseUnaryOperatorExpression() {
		if(!startsUnaryOperatorExpression(nowReading)) {
			return syntaxErrorNode("unary operator expression");
		}
		
		if(startsCloneExpression(nowReading)) {
			return parseCloneExpression();
		}
		else if(startsNotExpression(nowReading)) {
			return parseNotExpression();
		}
		else if(startsLengthExpression(nowReading)) {
			return parseLengthExpression();
		}
		else {
			return parseArrayIndexExpression();
		}
	}
	
	private boolean startsUnaryOperatorExpression(Token token) {
		return startsArrayIndexExpression(token)||startsCloneExpression(token)||startsNotExpression(token)||startsLengthExpression(token);
	}
	
	// clone expression
	private ParseNode parseCloneExpression() {
		Token token = nowReading;
		ParseNode expr;
		
		expect(Keyword.CLONE);
		
		if(nowReading.isLextant(Keyword.CLONE)) {
			expr = parseCloneExpression();
		}
		else {
			expr = parseArrayIndexExpression();
		}
		
		return new CloneExpressionNode(token,expr); 
	}
	
	private boolean startsCloneExpression(Token token) {
		return token.isLextant(Keyword.CLONE);
	}
	
	// !(expression)
	private ParseNode parseNotExpression() {
		Token token = nowReading;
		ParseNode expr;
		
		expect(Punctuator.NOT);
		
		if(nowReading.isLextant(Punctuator.NOT)) {
			expr = parseNotExpression();
		}
		else {
			expr = parseArrayIndexExpression();
		}
		
			
		return new NotExpressionNode(token, expr);
	}
		
	private boolean startsNotExpression(Token token) {
		return token.isLextant(Punctuator.NOT);
	}
		
	// length expression
	private ParseNode parseLengthExpression() {
		Token token = nowReading;
		expect(Keyword.LENGTH);
		ParseNode expr = parseArrayIndexExpression();
			
		return new LengthExpressionNode(token, expr);
	}
		
	private boolean startsLengthExpression(Token token) {
		return token.isLextant(Keyword.LENGTH);
	}
	
	// array index expression
	private ParseNode parseArrayIndexExpression() {
		if(!startsArrayIndexExpression(nowReading)) {
			return syntaxErrorNode("array index");
		}
		
		ParseNode arrayVariable = parseAtomicExpression();
		ParseNode indexValue;
		while (nowReading.isLextant(Punctuator.OPEN_BRACKET)) {
			expect(Punctuator.OPEN_BRACKET);
			indexValue = parseExpression();
			expect(Punctuator.CLOSE_BRACKET);
			arrayVariable = BinaryOperatorNode.withChildren(LextantToken.fakeToken("array index", Punctuator.ARRAY_INDEX), arrayVariable, indexValue);
		}
		
		return arrayVariable;
	}
	
	private boolean startsArrayIndexExpression(Token token) {
		return startsAtomicExpression(token);
	}
	// atomicExpression -> literal || (expression) || [expression | type] || expression(exprList)
	private ParseNode parseAtomicExpression() {
		if(!startsAtomicExpression(nowReading)) {
			return syntaxErrorNode("atomic expression");
		}
		
		if(startsParenthesesExpression(nowReading)) {
			return parseParenthesesExpression();
		}
		else if(startsBracketExpression(nowReading)) {
			return parseBracketExpression();
		}
		else if(startsNewExpression(nowReading)) {
			return parseNewExpression();
		}
		else if(startsLambdaExpression(nowReading)) {
			ParseNode lambda = parseLambdaExpression();
			
			if(startsParenthesesExpression(nowReading)) {
				expect(Punctuator.OPEN_PARENTHESES);
				ParseNode para = parseParaExpressionList();
				expect(Punctuator.CLOSE_PARENTHESES);
				
				return new FunctionInvocationNode(lambda, para);
			}
			
			return lambda;
		}
		else {
			return parseLiteral();
		}
	}
	
	private ParseNode parseParaExpressionList() {
		ParseNode paraList = new ParaExpressionListNode();
		
		while(startsExpression(nowReading)) {
			ParseNode expr = parseExpression();
			
			paraList.appendChild(expr);
			
			if(nowReading.isLextant(Punctuator.SEPARATOR)) {
				expect(Punctuator.SEPARATOR);
			}
		}
		
		return paraList;
	}
	private boolean startsAtomicExpression(Token token) {
		return startsLiteral(token) || startsParenthesesExpression(token) || startsBracketExpression(token) || startsNewExpression(token) || startsLambdaExpression(token);
	}
		
		
	// (expression) -> expression
	private ParseNode parseParenthesesExpression() {
		expect(Punctuator.OPEN_PARENTHESES);	
		ParseNode res = parseExpression();
		expect(Punctuator.CLOSE_PARENTHESES);
		
		return res;
	}
	
	private boolean startsParenthesesExpression(Token token) {
		return token.isLextant(Punctuator.OPEN_PARENTHESES);
	}
	
	//[expression | type] || [expressionList]
	private ParseNode parseBracketExpression() {
		Token bracket = nowReading;
		expect(Punctuator.OPEN_BRACKET);
		
		if(nowReading.isLextant(Punctuator.CLOSE_BRACKET)) {
			expect(Punctuator.CLOSE_BRACKET);
			return new ExpressionListNode(LextantToken.fakeToken("Expression List", Punctuator.EXPRESSION_LIST));
		}
		ParseNode left = parseExpression();
		if(nowReading.isLextant(Punctuator.SEPARATOR, Punctuator.CLOSE_BRACKET)) {
			ParseNode right;
			Token token;
			
			while(nowReading.isLextant(Punctuator.SEPARATOR)) {
				token = nowReading;
				readToken();
				right = parseExpression();
				
				left = BinaryOperatorNode.withChildren(token, left, right);
			}
			
			expect(Punctuator.CLOSE_BRACKET);
			
			return new ExpressionListNode(LextantToken.fakeToken("Expression List", Punctuator.EXPRESSION_LIST), left);
		}
		else if(nowReading.isLextant(Punctuator.VERTICAL)) {
			readToken();
			ParseNode type = parseType();
			expect(Punctuator.CLOSE_BRACKET);
			
			return BinaryOperatorNode.withChildren(bracket ,left, type);
		}
		else {
			return syntaxErrorNode("bracket expression.");
		}
		
	}
	
	private boolean startsBracketExpression(Token token) {
		return token.isLextant(Punctuator.OPEN_BRACKET)&&!(previouslyRead instanceof IdentifierToken);
	}
	
	
	//new expression
	private ParseNode parseNewExpression() {
		Token token = nowReading;
		expect(Keyword.NEW);
		ParseNode type = parseType();
		expect(Punctuator.OPEN_PARENTHESES);
		ParseNode expr = parseExpression();
		expect(Punctuator.CLOSE_PARENTHESES);
		
		return BinaryOperatorNode.withChildren(token, type, expr);
	}
	
	private boolean startsNewExpression(Token token) {
		return token.isLextant(Keyword.NEW);
	}
	
	// type (terminal)
	private ParseNode parseType() {
		if(nowReading.isLextant(Keyword.VOID, Keyword.INT, Keyword.FLOAT, Keyword.STRING, Keyword.CHAR, Keyword.BOOL, Keyword.RAT)) {
			readToken();
			return new TypeConstantNode(previouslyRead);
		}
		else if(nowReading.isLextant(Punctuator.OPEN_BRACKET)) {
			
			return parseArrayType();
		}
		else if(nowReading.isLextant(Punctuator.SMALLER)) {
			ParseNode lambdaType = new LambdaTypeConstantNode();
			expect(Punctuator.SMALLER);
			
			while(startsType(nowReading)) {
				ParseNode type = parseType();
				
				lambdaType.appendChild(type);
				
				if(nowReading.isLextant(Punctuator.SEPARATOR)) {
					expect(Punctuator.SEPARATOR);
				}
			}
			
			expect(Punctuator.GREATER);
			expect(Punctuator.TO);
			
			ParseNode returnType = parseType();
			
			lambdaType.insertChild(returnType);
			return lambdaType;
		}
		else {
			return syntaxErrorNode("type node error");
		}
		
	}
	private boolean startsType(Token token) {
		return token.isLextant(Keyword.VOID, Keyword.INT, Keyword.FLOAT, Keyword.STRING, Keyword.CHAR, Keyword.BOOL, Keyword.RAT, Punctuator.OPEN_BRACKET, Punctuator.SMALLER);
	}
	private ParseNode parseArrayType() {
		Token token = nowReading;
		expect(Punctuator.OPEN_BRACKET);
		ParseNode type = parseType();
		expect(Punctuator.CLOSE_BRACKET);
		return new ArrayTypeConstantNode(token, type);
	}
	
	
	// literal -> number | identifier | booleanConstant
	private ParseNode parseLiteral() {
		if(!startsLiteral(nowReading)) {
			return syntaxErrorNode("literal");
		}
		
		if(startsIntNumber(nowReading)) {
			return parseIntNumber();
		}
		if(startsFloatNumber(nowReading)) {
			return parseFloatNumber();
		}
		if(startsIdentifier(nowReading)) {
			return parseIdentifier();
		}
		if(startsBooleanConstant(nowReading)) {
			return parseBooleanConstant();
		}
		if(startsStringConstant(nowReading)) {
			return parseStringConstant();
		}
		if(startsCharConstant(nowReading)) {
			return parseCharConstant();
		}

		return syntaxErrorNode("literal");
	}
	private boolean startsLiteral(Token token) {
		return startsIntNumber(token) || startsIdentifier(token) || startsBooleanConstant(token) || startsStringConstant(token)
				|| startsCharConstant(token) || startsFloatNumber(token);
	}

	// number (terminal)
	private ParseNode parseIntNumber() {
		if(!startsIntNumber(nowReading)) {
			return syntaxErrorNode("integer constant");
		}
		readToken();
		return new IntegerConstantNode(previouslyRead);
	}
	private boolean startsIntNumber(Token token) {
		return token instanceof IntegerToken;
	}


	// identifier (terminal)
	private ParseNode parseIdentifier() {
		if(!startsIdentifier(nowReading)) {
			return syntaxErrorNode("identifier");
		}
		ParseNode identifier = new IdentifierNode(nowReading);
		readToken();
		
		if(startsParenthesesExpression(nowReading)) {
			expect(Punctuator.OPEN_PARENTHESES);
			ParseNode para = parseParaExpressionList();
			expect(Punctuator.CLOSE_PARENTHESES);
			
			return new FunctionInvocationNode(identifier, para);
		}
		
		return identifier;
	}
	private boolean startsIdentifier(Token token) {
		return token instanceof IdentifierToken;
	}

	// boolean constant (terminal)
	private ParseNode parseBooleanConstant() {
		if(!startsBooleanConstant(nowReading)) {
			return syntaxErrorNode("boolean constant");
		}
		readToken();
		return new BooleanConstantNode(previouslyRead);
	}
	private boolean startsBooleanConstant(Token token) {
		return token.isLextant(Keyword.TRUE, Keyword.FALSE);
	}

	// string constant (terminal)
	private ParseNode parseStringConstant() {
		if(!startsStringConstant(nowReading)) {
			return syntaxErrorNode("string constant");
		}
		readToken();
		return new StringConstantNode(previouslyRead);
	}
	
	private boolean startsStringConstant(Token token) {
		return token instanceof StringToken;
	}
	
	// character constant (terminal)
	private ParseNode parseCharConstant() {
		if(!startsCharConstant(nowReading)) {
			return syntaxErrorNode("char constant");
		}
		readToken();
		
		return new CharConstantNode(previouslyRead);
	}
	
	private boolean startsCharConstant(Token token) {
		return token instanceof CharacterToken;
	}
	
	// float constant (terminal)
	private ParseNode parseFloatNumber() {
		if(!startsFloatNumber(nowReading)) {
			return syntaxErrorNode("float constant");
		}
		readToken();
		
		return new FloatConstantNode(previouslyRead);
	}
	
	private boolean startsFloatNumber(Token token) {
		return token instanceof FloatToken;
	}
	
	
	private void readToken() {
		previouslyRead = nowReading;
		nowReading = scanner.next();
	}	
	
	// if the current token is one of the given lextants, read the next token.
	// otherwise, give a syntax error and read next token (to avoid endless looping).
	private void expect(Lextant ...lextants ) {
		if(!nowReading.isLextant(lextants)) {
			syntaxError(nowReading, "expecting " + Arrays.toString(lextants));
		}
		readToken();
	}	
	private ErrorNode syntaxErrorNode(String expectedSymbol) {
		syntaxError(nowReading, "expecting " + expectedSymbol);
		ErrorNode errorNode = new ErrorNode(nowReading);
		readToken();
		return errorNode;
	}
	private void syntaxError(Token token, String errorDescription) {
		String message = "" + token.getLocation() + " " + errorDescription;
		error(message);
	}
	private void error(String message) {
		PikaLogger log = PikaLogger.getLogger("compiler.Parser");
		log.severe("syntax error: " + message);
	}	
}

