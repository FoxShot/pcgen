/*
 * Copyright (c) 2008-14 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.template;

import java.util.Collection;

import pcgen.base.calculation.Modifier;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CodeControl;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with FACE Token
 */
public class FaceToken extends AbstractNonEmptyToken<PCTemplate> implements
		CDOMPrimaryToken<PCTemplate>
{

	private static final String VAR_NAME = "Face";
	private static final int MOD_PRIORITY = 100;
	private static final String MOD_IDENTIFICATION = "SET";

	@Override
	public String getTokenName()
	{
		return "FACE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		PCTemplate template, String value)
	{
		return parseFace(context, template, value);
	}

	protected ParseResult parseFace(LoadContext context, PCTemplate fObj,
		String value)
	{
		CodeControl controller =
				context.getReferenceContext().silentlyGetConstructedCDOMObject(
					CodeControl.class, "Controller");
		if (controller != null)
		{
			if (controller.get(ObjectKey.getKeyFor(String.class, "*FACE")) != null)
			{
				return new ParseResult.Fail(
					"FACE: LST Token is disabled when FACE: control is used",
					context);
			}
		}
		if (value.indexOf(',') == -1)
		{
			value = value + "," + 0;
		}
		FormatManager<OrderedPair> formatManager =
				context.getReferenceContext().getFormatManager(OrderedPair.class);
		ScopeInstance scopeInst = context.getActiveScope();
		LegalScope scope = scopeInst.getLegalScope();
		Modifier<OrderedPair> modifier;
		try
		{
			modifier =
					context.getVariableContext().getModifier(
						MOD_IDENTIFICATION, value, MOD_PRIORITY, scope,
						formatManager);
		}
		catch (IllegalArgumentException iae)
		{
			return new ParseResult.Fail(getTokenName()
				+ " Modifier SET had value " + value
				+ " but it was not valid: " + iae.getMessage(), context);
		}
		OrderedPair pair = modifier.process(null, null, null);
		if (pair.getPreciseX().doubleValue() < 0.0)
		{
			return new ParseResult.Fail(getTokenName() + " had value " + value
				+ " but first item cannot be negative", context);
		}
		if (pair.getPreciseY().doubleValue() < 0.0)
		{
			return new ParseResult.Fail(getTokenName() + " had value " + value
				+ " but second item cannot be negative", context);
		}
		String varName = VAR_NAME;
		if (!context.getVariableContext().isLegalVariableID(scope, varName))
		{
			return new ParseResult.Fail(getTokenName()
				+ " internal error: found invalid fact name: " + varName
				+ ", Modified on " + fObj.getClass().getSimpleName() + " "
				+ fObj.getKeyName(), context);
		}
		VarModifier<OrderedPair> vm =
				new VarModifier<OrderedPair>(varName, scope, modifier);
		context.getObjectContext().addToList(fObj, ListKey.MODIFY, vm);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<VarModifier<?>> changes =
				context.getObjectContext().getListChanges(pct, ListKey.MODIFY);
		Collection<VarModifier<?>> added = changes.getAdded();
		String face = null;
		if (added != null)
		{
			for (VarModifier<?> vm : added)
			{
				Modifier<?> modifier = vm.modifier;
				if (VAR_NAME.equals(vm.varName)
					&& (vm.legalScope.getParentScope() == null)
					&& (modifier.getUserPriority() == MOD_PRIORITY)
					&& (vm.modifier.getIdentification()
						.equals(MOD_IDENTIFICATION)))
				{
					face = vm.modifier.getInstructions();
					if (face.endsWith(",0"))
					{
						face = face.substring(0, face.length() - 2);
					}
				}
			}
		}
		return (face == null) ? null : new String[]{face};
	}

	@Override
	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
