/**
 * pcgen.core.term.PCMaxCastableAnyTermEvaluator.java
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 16-Sep-2008 00:47:07
 *
 * Current Ver: $Revision:$
 *
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;
import pcgen.core.PCClass;

public class PCMaxCastableAnyTermEvaluator  
		extends BasePCTermEvaluator implements TermEvaluator
{
	public PCMaxCastableAnyTermEvaluator(String originalText)
	{
		this.originalText = originalText;
	}

	@Override
	public Float resolve(PlayerCharacter pc)
	{
		Float max = 0.0f;
		for (PCClass spClass : pc.getDisplay().getClassSet())
		{
			int cutoff = pc.getSpellSupport(spClass).getHighestLevelSpell();
			if (pc.getSpellSupport(spClass).hasCastList())
			{
				for (int i = 0; i < cutoff; i++)
				{
					if (pc.getSpellSupport(spClass).getCastForLevel(i, pc) != 0)
					{
						max = Math.max(max, i);
					}
				}
			}
			else
			{
				for (int i = 0; i < cutoff; i++)
				{
					if (pc.getSpellSupport(spClass).getKnownForLevel(i, pc) != 0)
					{
						max = Math.max(max, i);
					}
				}
			}
		}

		return max;
	}

	@Override
	public boolean isSourceDependant()
	{
		return true;
	}

	public boolean isStatic()
	{
		return false;
	}
}
