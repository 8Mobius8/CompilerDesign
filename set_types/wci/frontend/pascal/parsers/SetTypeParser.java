package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.pascal.PascalParserTD;
import wci.intermediate.ICodeNode;
import wci.intermediate.TypeFactory;
import wci.intermediate.TypeSpec;
import wci.intermediate.typeimpl.TypeFormImpl;
import wci.intermediate.typeimpl.TypeKeyImpl;

public class SetTypeParser extends TypeSpecificationParser {

	public SetTypeParser(PascalParserTD parent) {
		super(parent);
	}
	
	public TypeSpec parse(Token token)
		throws Exception
	{
		// See word set GOOODBYE BABY!
		nextToken(); // Nom
		token = nextToken(); // Nom
		
		SimpleTypeParser newParser = new SimpleTypeParser(this);
		TypeSpec aType = newParser.parse(token);
		
		TypeSpec aSetType = TypeFactory.createType(TypeFormImpl.SET);
		aSetType.setAttribute(TypeKeyImpl.SET_ELEMENT_TYPE, aType);
		
		return aType;
	}

}
