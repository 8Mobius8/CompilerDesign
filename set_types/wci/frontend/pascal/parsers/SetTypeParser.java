package wci.frontend.pascal.parsers;

import static wci.intermediate.symtabimpl.DefinitionImpl.FIELD;
import static wci.intermediate.typeimpl.TypeFormImpl.RECORD;
import static wci.intermediate.typeimpl.TypeFormImpl.SET;
import static wci.intermediate.typeimpl.TypeKeyImpl.RECORD_SYMTAB;
import wci.frontend.Token;
import wci.frontend.pascal.PascalParserTD;
import wci.intermediate.ICodeNode;
import wci.intermediate.TypeFactory;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.typeimpl.TypeFormImpl;
import wci.intermediate.typeimpl.TypeKeyImpl;

public class SetTypeParser extends TypeSpecificationParser {

	public SetTypeParser(PascalParserTD parent) {
		super(parent);
	}
	
	public TypeSpec parse(Token token)
		throws Exception
	{
		TypeSpec setType = TypeFactory.createType(SET);
		// See word set GOOODBYE BABY!
		nextToken(); // Consume SET
		token = nextToken(); // Consume of
		
		
		SimpleTypeParser newParser = new SimpleTypeParser(this);
		TypeSpec aType = newParser.parse(token);
		
		TypeSpec aSetType = TypeFactory.createType(TypeFormImpl.SET);
		aSetType.setAttribute(TypeKeyImpl.SET_ELEMENT_TYPE, aType);
		
		
		return aSetType;
	}

}
