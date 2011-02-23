package com.googlecode.sweetened.typedef;

import junit.framework.TestCase;

public class ScopeLogicTest extends TestCase {
	
	public void testIsInScope() {
		// path scope of all, all resources should be included
		assertTrue(SweetenedPath.isInScope(SweetenedScope.ALL, null));
		assertTrue(SweetenedPath.isInScope(SweetenedScope.ALL, SweetenedScope.ALL));
		assertTrue(SweetenedPath.isInScope(SweetenedScope.ALL, SweetenedScope.COMPILE));
		assertTrue(SweetenedPath.isInScope(SweetenedScope.ALL, SweetenedScope.RUNTIME));
		assertTrue(SweetenedPath.isInScope(SweetenedScope.ALL, SweetenedScope.UNIT));
		
		
		// runtime path should include runtime and compile resources
		assertTrue(SweetenedPath.isInScope(SweetenedScope.RUNTIME, SweetenedScope.ALL));
		assertTrue(SweetenedPath.isInScope(SweetenedScope.RUNTIME, SweetenedScope.RUNTIME));
		assertTrue(SweetenedPath.isInScope(SweetenedScope.RUNTIME, SweetenedScope.COMPILE));
		
		assertFalse(SweetenedPath.isInScope(SweetenedScope.RUNTIME, null));
		assertFalse(SweetenedPath.isInScope(SweetenedScope.RUNTIME, SweetenedScope.UNIT));

		// unit path should include runtime and compile resources
		assertTrue(SweetenedPath.isInScope(SweetenedScope.UNIT, SweetenedScope.ALL));
		assertTrue(SweetenedPath.isInScope(SweetenedScope.UNIT, SweetenedScope.UNIT));
		assertTrue(SweetenedPath.isInScope(SweetenedScope.UNIT, SweetenedScope.COMPILE));
		
		assertFalse(SweetenedPath.isInScope(SweetenedScope.UNIT, null));
		assertFalse(SweetenedPath.isInScope(SweetenedScope.UNIT, SweetenedScope.RUNTIME));

		// compile path should include compile resources
		assertTrue(SweetenedPath.isInScope(SweetenedScope.COMPILE, SweetenedScope.ALL));
		assertTrue(SweetenedPath.isInScope(SweetenedScope.COMPILE, SweetenedScope.COMPILE));
		
		assertFalse(SweetenedPath.isInScope(SweetenedScope.COMPILE, null));
		assertFalse(SweetenedPath.isInScope(SweetenedScope.COMPILE, SweetenedScope.RUNTIME));
		assertFalse(SweetenedPath.isInScope(SweetenedScope.COMPILE, SweetenedScope.UNIT));

		
		// null path should only include null resources
		assertTrue(SweetenedPath.isInScope(null, SweetenedScope.ALL));
		assertTrue(SweetenedPath.isInScope(null, null));
		
		assertFalse(SweetenedPath.isInScope(null, SweetenedScope.RUNTIME));
		assertFalse(SweetenedPath.isInScope(null, SweetenedScope.UNIT));
		assertFalse(SweetenedPath.isInScope(null, SweetenedScope.COMPILE));
		
	}
	
}
