/*
 * ParserRuleSet.java - A set of parser rules
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999 mike dillon
 * Portions copyright (C) 2001, 2002 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.grview.editor.syntax;

//{{{ Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//}}}

/**
 * A set of parser rules.
 * 
 * @author mike dillon
 * @version $Id$
 */
public class ParserRuleSet
{
	// {{{ Private members
	private static ParserRuleSet[] standard;

	static
	{
		standard = new ParserRuleSet[Token.ID_COUNT];
		for (byte i = 0; i < Token.ID_COUNT; i++)
		{
			standard[i] = new ParserRuleSet(null, null);
			standard[i].setDefault(i);
			standard[i].builtIn = true;
		}
	}

	private String modeName, setName;

	private Hashtable<String, String> props;

	private KeywordMap keywords;

	private int ruleCount;

	private Map<Character, List<ParserRule>> ruleMap;

	private final List<ParserRuleSet> imports;

	/**
	 * The number of chars that can be read before the parsing stops.
	 * &lt;TERMINATE AT_CHAR="1" /&gt;
	 */
	private int terminateChar = -1;

	private boolean ignoreCase = true;

	private byte defaultToken;

	private ParserRule escapeRule;

	private boolean highlightDigits;

	private Pattern digitRE;

	private String _noWordSep;

	private String noWordSep;

	private boolean builtIn;

	// }}}

	// {{{ ParserRuleSet constructor
	public ParserRuleSet(String modeName, String setName)
	{
		this.modeName = modeName;
		this.setName = setName;
		ruleMap = new HashMap<Character, List<ParserRule>>();
		imports = new ArrayList<ParserRuleSet>();
	} // }}}

	// {{{ getStandardRuleSet() method
	/**
	 * Returns a parser rule set that highlights everything with the specified
	 * token type.
	 * 
	 * @param id
	 *            The token type
	 */
	public static ParserRuleSet getStandardRuleSet(byte id)
	{
		return standard[id];
	} // }}}

	// {{{ addRule() method
	public void addRule(ParserRule r)
	{
		ruleCount++;
		Character[] keys;
		if (null == r.upHashChars)
		{
			keys = new Character[1];
			if ((null == r.upHashChar) || (0 >= r.upHashChar.length()))
			{
				keys[0] = null;
			}
			else
			{
				keys[0] = Character.valueOf(r.upHashChar.charAt(0));
			}
		}
		else
		{
			keys = new Character[r.upHashChars.length];
			int i = 0;
			for (char upHashChar : r.upHashChars)
			{
				keys[i++] = upHashChar;
			}
		}
		for (Character key : keys)
		{
			List<ParserRule> rules = ruleMap.get(key);
			if (null == rules)
			{
				rules = new ArrayList<ParserRule>();
				ruleMap.put(key, rules);
			}
			int ruleAmount = rules.size();
			rules.add(r);
			// fill the deprecated ParserRule.next pointer
			if (ruleAmount > 0)
			{
				rules.get(ruleAmount).next = r;
			}
		}
	} // }}}

	// {{{ addRuleSet() method
	/**
	 * Adds all rules contained in the given ruleset.
	 * 
	 * @param ruleset
	 *            The ruleset
	 * @since jEdit 4.2pre3
	 */
	public void addRuleSet(ParserRuleSet ruleset)
	{
		imports.add(ruleset);
	} // }}}

	// {{{ getDefault() method
	public byte getDefault()
	{
		return defaultToken;
	} // }}}

	// {{{ getDigitRegexp() method
	public Pattern getDigitRegexp()
	{
		return digitRE;
	} // }}}

	// {{{ getEscapeRule() method
	public ParserRule getEscapeRule()
	{
		return escapeRule;
	} // }}}

	// {{{ getHighlightDigits() method
	public boolean getHighlightDigits()
	{
		return highlightDigits;
	} // }}}

	// {{{ getIgnoreCase() method
	public boolean getIgnoreCase()
	{
		return ignoreCase;
	} // }}}

	// {{{ getKeywords() method
	public KeywordMap getKeywords()
	{
		return keywords;
	} // }}}

	// {{{ getModeName() method
	public String getModeName()
	{
		return modeName;
	} // }}}

	// {{{ getName() method
	public String getName()
	{
		return modeName + "::" + setName;
	} // }}}

	// {{{ getNoWordSep() method
	public String getNoWordSep()
	{
		if (_noWordSep == null)
		{
			_noWordSep = noWordSep;
			if (noWordSep == null)
				noWordSep = "";
			if (keywords != null)
				noWordSep += keywords.getNonAlphaNumericChars();
		}
		return noWordSep;
	} // }}}

	// {{{ getProperties() method
	public Hashtable<String, String> getProperties()
	{
		return props;
	} // }}}

	// {{{ getRuleCount() method
	public int getRuleCount()
	{
		return ruleCount;
	} // }}}

	// {{{ getRules() method
	/**
	 * @deprecated As the linking between rules is not anymore done within the
	 *             rule, use {@link #getRules(Character)} instead
	 */
	@Deprecated
	public ParserRule getRules(char ch)
	{
		List<ParserRule> rules = getRules(Character.valueOf(ch));
		return rules.get(0);
	} // }}}

	// {{{ getRules() method
	public List<ParserRule> getRules(Character key)
	{
		Character upperKey = null == key ? null : Character.valueOf(Character.toUpperCase(key.charValue()));
		List<ParserRule> rules = ruleMap.get(upperKey);
		if (null == rules)
		{
			rules = new ArrayList<ParserRule>();
		}
		else
		{
			rules = new ArrayList<ParserRule>(rules);
		}
		if (null != upperKey)
		{
			List<ParserRule> nullRules = ruleMap.get(null);
			if (null != nullRules)
			{
				int rulesSize = rules.size();
				if ((0 < rulesSize) && (0 < nullRules.size()))
				{
					rules.get(rulesSize - 1).next = nullRules.get(0);
				}
				rules.addAll(nullRules);
			}
		}
		return rules;
	} // }}}
		// {{{ getSetName() method

	public String getSetName()
	{
		return setName;
	} // }}}

	// {{{ getTerminateChar() method
	/**
	 * Returns the number of chars that can be read before the rule parsing
	 * stops.
	 * 
	 * @return a number of chars or -1 (default value) if there is no limit
	 */
	public int getTerminateChar()
	{
		return terminateChar;
	} // }}}

	// {{{ isBuiltIn() method
	/**
	 * Returns if this is a built-in ruleset.
	 * 
	 * @since jEdit 4.2pre1
	 */
	public boolean isBuiltIn()
	{
		return builtIn;
	} // }}}

	// {{{ resolveImports() method
	/**
	 * Resolves all rulesets added with {@link #addRuleSet(ParserRuleSet)}.
	 * 
	 * @since jEdit 4.2pre3
	 */
	public void resolveImports()
	{
		for (ParserRuleSet ruleset : imports)
		{
			if (!ruleset.imports.isEmpty())
			{
				// prevent infinite recursion
				ruleset.imports.remove(this);
				ruleset.resolveImports();
			}

			for (List<ParserRule> rules : ruleset.ruleMap.values())
			{
				for (ParserRule rule : rules)
				{
					addRule(rule);
				}
			}

			if (ruleset.keywords != null)
			{
				if (keywords == null)
					keywords = new KeywordMap(ignoreCase);
				keywords.add(ruleset.keywords);
			}
		}
		imports.clear();
	} // }}}

	// {{{ setDefault() method
	public void setDefault(byte def)
	{
		defaultToken = def;
	} // }}}

	// {{{ setDigitRegexp() method
	public void setDigitRegexp(Pattern digitRE)
	{
		this.digitRE = digitRE;
	} // }}}
		// {{{ setEscapeRule() method

	public void setEscapeRule(ParserRule escapeRule)
	{
		this.escapeRule = escapeRule;
	} // }}}
		// {{{ setHighlightDigits() method

	public void setHighlightDigits(boolean highlightDigits)
	{
		this.highlightDigits = highlightDigits;
	} // }}}
		// {{{ setIgnoreCase() method

	public void setIgnoreCase(boolean b)
	{
		ignoreCase = b;
	} // }}}

	// {{{ setKeywords() method
	public void setKeywords(KeywordMap km)
	{
		keywords = km;
		_noWordSep = null;
	} // }}}
		// {{{ setNoWordSep() method

	public void setNoWordSep(String noWordSep)
	{
		this.noWordSep = noWordSep;
		_noWordSep = null;
	} // }}}

	// {{{ setProperties() method
	public void setProperties(Hashtable<String, String> props)
	{
		this.props = props;
		_noWordSep = null;
	} // }}}
		// {{{ setTerminateChar() method

	public void setTerminateChar(int atChar)
	{
		terminateChar = (atChar >= 0) ? atChar : -1;
	} // }}}

	// {{{ toString() method
	@Override
	public String toString()
	{
		return getClass().getName() + '[' + modeName + "::" + setName + ']';
	} // }}}
}
